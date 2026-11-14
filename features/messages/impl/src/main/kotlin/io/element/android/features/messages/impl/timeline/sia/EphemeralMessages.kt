/*
 * SIA: ephemeral messages.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.features.messages.impl.timeline.sia

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UniqueId
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.EventContent
import io.element.android.libraries.matrix.api.timeline.item.event.EventTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.LiveLocationContent
import io.element.android.libraries.matrix.api.timeline.item.event.MessageContent
import io.element.android.libraries.matrix.api.timeline.item.event.PollContent
import io.element.android.libraries.matrix.api.timeline.item.event.RedactedContent
import io.element.android.libraries.matrix.api.timeline.item.event.StickerContent
import io.element.android.libraries.matrix.api.timeline.item.event.UnableToDecryptContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** SIA rule: every message disappears 5 seconds after it was sent, on both sides. */
const val EPHEMERAL_LIFETIME_MILLIS = 5_000L
private const val EPHEMERAL_TICK_MILLIS = 1_000L

/** Emits the current time every second, so expired messages are hidden while the room is open. */
fun ephemeralClock(now: () -> Long = System::currentTimeMillis): Flow<Long> = flow {
    while (true) {
        emit(now())
        delay(EPHEMERAL_TICK_MILLIS)
    }
}

/** Content that carries what people wrote or sent. Room state (members, name...) is kept. */
fun EventContent.isEphemeral(): Boolean = when (this) {
    is MessageContent,
    is StickerContent,
    is PollContent,
    is RedactedContent,
    is UnableToDecryptContent,
    is LiveLocationContent -> true
    else -> false
}

/**
 * Hides expired messages and tells which of our own messages must be deleted from the server.
 *
 * A message expires 5 s after its server timestamp. The time this device first saw it is also used,
 * so a server clock ahead of the phone cannot make a message stay longer than 5 s on screen.
 * One instance per open room: it remembers what it has seen and what it already asked to delete.
 */
class EphemeralMessagesFilter(
    private val lifetimeMillis: Long = EPHEMERAL_LIFETIME_MILLIS,
) {
    private val firstSeenAt = mutableMapOf<UniqueId, Long>()
    private val deletionRequested = mutableSetOf<EventId>()

    data class Result(
        val visibleItems: List<MatrixTimelineItem>,
        val ownEventsToDelete: List<EventId>,
    )

    fun process(items: List<MatrixTimelineItem>, now: Long): Result {
        val visible = ArrayList<MatrixTimelineItem>(items.size)
        val toDelete = mutableListOf<EventId>()
        val presentIds = HashSet<UniqueId>(items.size)
        for (item in items) {
            if (item !is MatrixTimelineItem.Event || !item.event.content.isEphemeral()) {
                visible.add(item)
                continue
            }
            presentIds.add(item.uniqueId)
            val seenAt = firstSeenAt.getOrPut(item.uniqueId) { now }
            if (!isExpired(item.event.timestamp, seenAt, now)) {
                visible.add(item)
                continue
            }
            ownEventIdToDelete(item.event)?.let { eventId ->
                if (deletionRequested.add(eventId)) toDelete.add(eventId)
            }
        }
        firstSeenAt.keys.retainAll(presentIds)
        return Result(visibleItems = visible, ownEventsToDelete = toDelete)
    }

    private fun isExpired(timestamp: Long, seenAt: Long, now: Long): Boolean =
        now - timestamp >= lifetimeMillis || now - seenAt >= lifetimeMillis

    private fun ownEventIdToDelete(event: EventTimelineItem): EventId? {
        if (!event.isOwn || event.content is RedactedContent) return null
        return event.eventId
    }
}
