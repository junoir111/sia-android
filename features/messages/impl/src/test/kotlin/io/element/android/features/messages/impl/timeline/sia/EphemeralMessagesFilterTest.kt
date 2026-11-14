/*
 * SIA: ephemeral messages.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.features.messages.impl.timeline.sia

import com.google.common.truth.Truth.assertThat
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UniqueId
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.EventContent
import io.element.android.libraries.matrix.api.timeline.item.event.RedactedContent
import io.element.android.libraries.matrix.test.AN_EVENT_ID
import io.element.android.libraries.matrix.test.AN_EVENT_ID_2
import io.element.android.libraries.matrix.test.timeline.aMessageContent
import io.element.android.libraries.matrix.test.timeline.aProfileChangeMessageContent
import io.element.android.libraries.matrix.test.timeline.anEventTimelineItem
import org.junit.Test

class EphemeralMessagesFilterTest {
    private fun anItem(
        id: String,
        timestamp: Long,
        isOwn: Boolean = false,
        eventId: EventId = AN_EVENT_ID,
        content: EventContent = aMessageContent(),
    ) = MatrixTimelineItem.Event(
        uniqueId = UniqueId(id),
        event = anEventTimelineItem(eventId = eventId, isOwn = isOwn, timestamp = timestamp, content = content),
    )

    @Test
    fun `a message younger than 5 seconds is visible`() {
        val item = anItem("a", timestamp = 10_000)
        val result = EphemeralMessagesFilter().process(listOf(item), now = 14_999)
        assertThat(result.visibleItems).containsExactly(item)
        assertThat(result.ownEventsToDelete).isEmpty()
    }

    @Test
    fun `a message 5 seconds old is hidden`() {
        val item = anItem("a", timestamp = 10_000)
        val result = EphemeralMessagesFilter().process(listOf(item), now = 15_000)
        assertThat(result.visibleItems).isEmpty()
    }

    @Test
    fun `room state events are never hidden`() {
        val item = anItem("a", timestamp = 0, content = aProfileChangeMessageContent())
        val result = EphemeralMessagesFilter().process(listOf(item), now = 1_000_000)
        assertThat(result.visibleItems).containsExactly(item)
    }

    @Test
    fun `only our own expired messages are deleted, and only once`() {
        val filter = EphemeralMessagesFilter()
        val mine = anItem("a", timestamp = 0, isOwn = true, eventId = AN_EVENT_ID)
        val theirs = anItem("b", timestamp = 0, isOwn = false, eventId = AN_EVENT_ID_2)
        val first = filter.process(listOf(mine, theirs), now = 6_000)
        assertThat(first.ownEventsToDelete).containsExactly(AN_EVENT_ID)
        val second = filter.process(listOf(mine, theirs), now = 7_000)
        assertThat(second.ownEventsToDelete).isEmpty()
    }

    @Test
    fun `an already deleted message is hidden but not deleted again`() {
        val item = anItem("a", timestamp = 0, isOwn = true, content = RedactedContent(threadInfo = null))
        val result = EphemeralMessagesFilter().process(listOf(item), now = 6_000)
        assertThat(result.visibleItems).isEmpty()
        assertThat(result.ownEventsToDelete).isEmpty()
    }

    @Test
    fun `a server clock ahead of the phone cannot keep a message more than 5 seconds`() {
        val filter = EphemeralMessagesFilter()
        val item = anItem("a", timestamp = 60_000)
        assertThat(filter.process(listOf(item), now = 10_000).visibleItems).containsExactly(item)
        assertThat(filter.process(listOf(item), now = 15_000).visibleItems).isEmpty()
    }
}
