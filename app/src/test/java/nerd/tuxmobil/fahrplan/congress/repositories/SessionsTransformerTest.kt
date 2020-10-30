package nerd.tuxmobil.fahrplan.congress.repositories

import com.google.common.truth.Truth.assertThat
import nerd.tuxmobil.fahrplan.congress.models.Session
import org.junit.Test

class SessionsTransformerTest {
    private val prioritizedRoomProvider = object : PrioritizedRoomProvider {
        override val prioritizedRooms = listOf("Ada", "Borg", "Clarke", "Dijkstra", "Eliza")
    }

    private val transformer = SessionsTransformer(prioritizedRoomProvider)

    @Test
    fun `ScheduleData_dayIndex contains proper day value`() {
        val scheduleData = transformer.transformSessions(dayIndex = 1, sessions = emptyList())

        assertThat(scheduleData.dayIndex).isEqualTo(1)
    }

    @Test
    fun `rooms are ordered by roomIndex`() {
        val sessionsInDatabase = listOf(
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c00", roomName = "Four", roomIndex = 2342),
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c01", roomName = "Two", roomIndex = 1),
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c02", roomName = "One", roomIndex = 0),
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c03", roomName = "Three", roomIndex = 4)
        )

        val scheduleData = transformer.transformSessions(dayIndex = 0, sessions = sessionsInDatabase)

        val roomNames = scheduleData.roomDataList.map { it.roomName }
        assertThat(roomNames).isEqualTo(listOf("One", "Two", "Three", "Four"))
    }

    @Test
    fun `prioritized rooms are first in list`() {
        val sessionsInDatabase = listOf(
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c00", roomName = "Chaos-West Bühne", roomIndex = 11),
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c01", roomName = "c-base", roomIndex = 12),
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c02", roomName = "Eliza", roomIndex = 13),
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c03", roomName = "Borg", roomIndex = 14),
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c04", roomName = "Ada", roomIndex = 15)
        )

        val scheduleData = transformer.transformSessions(dayIndex = 0, sessions = sessionsInDatabase)

        val roomNames = scheduleData.roomDataList.map { it.roomName }
        assertThat(roomNames).isEqualTo(listOf("Ada", "Borg", "Eliza", "Chaos-West Bühne", "c-base"))
    }

    @Test
    fun `same roomIndex does not influence RoomData contents`() {
        val sessionsInDatabase = listOf(
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c00", roomName = "Borg", roomIndex = 2),
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c01", roomName = "Broken One", roomIndex = 2),
                createSession(sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c02", roomName = "Broken Two", roomIndex = 2)
        )

        val scheduleData = transformer.transformSessions(dayIndex = 0, sessions = sessionsInDatabase)

        with(scheduleData.roomDataList[0]) {
            assertThat(roomName).isEqualTo("Borg")
            assertThat(sessions[0].sessionId).isEqualTo("be9979c4-18bc-52bb-9480-2c0ac2782c00")
        }
        with(scheduleData.roomDataList[1]) {
            assertThat(roomName).isEqualTo("Broken One")
            assertThat(sessions[0].sessionId).isEqualTo("be9979c4-18bc-52bb-9480-2c0ac2782c01")
        }
        with(scheduleData.roomDataList[2]) {
            assertThat(roomName).isEqualTo("Broken Two")
            assertThat(sessions[0].sessionId).isEqualTo("be9979c4-18bc-52bb-9480-2c0ac2782c02")
        }
    }

    private fun createSession(
            sessionId: String,
            roomName: String,
            roomIndex: Int,
            dateUTC: Long = 0
    ): Session {
        return Session(sessionId).apply {
            this.room = roomName
            this.roomIndex = roomIndex
            this.dateUTC = dateUTC
        }
    }
}
