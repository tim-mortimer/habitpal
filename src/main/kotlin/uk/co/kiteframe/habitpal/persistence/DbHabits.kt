package uk.co.kiteframe.habitpal.persistence

import org.jooq.DSLContext
import org.jooq.Record5
import uk.co.kiteframe.habitpal.*
import uk.co.kiteframe.habitpal.db.tables.references.HABITS
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class DbHabits(private val dslContext: DSLContext) : Habits {
    override fun save(habit: Habit) {
        dslContext.insertInto(HABITS)
            .set(HABITS.ID, habit.id.value)
            .set(HABITS.NAME, habit.name.toString())
            .set(HABITS.TYPE, habit.type.toDatabaseForm())
            .set(HABITS.STARTED_ON, habit.startedOn.atTime(0, 0).atOffset(ZoneOffset.UTC))
            .set(HABITS.IS_ARCHIVED, habit.isArchived)
            .onDuplicateKeyUpdate()
            .set(HABITS.IS_ARCHIVED, habit.isArchived)
            .execute()
    }

    override fun findById(id: HabitId): Habit? {
        val records =
            dslContext.select(HABITS.ID, HABITS.NAME, HABITS.TYPE, HABITS.STARTED_ON, HABITS.IS_ARCHIVED)
                .from(HABITS)
                .where(HABITS.ID.eq(id.value))
                .fetch()

        return when {
            records.isEmpty() -> return null
            else -> records.first().toHabit()
        }
    }

    override fun findAll(): List<Habit> =
        dslContext.select(HABITS.ID, HABITS.NAME, HABITS.TYPE, HABITS.STARTED_ON, HABITS.IS_ARCHIVED)
            .from(HABITS)
            .where(HABITS.IS_ARCHIVED.isFalse)
            .fetch()
            .map { it.toHabit() }

    private fun HabitTypeConfiguration.toDatabaseForm(): String = when (this) {
        is Daily -> "DAILY"
        is MultipleTimesADay -> "${this.multiple.value}#DAILY"
    }

    private fun Record5<UUID?, String?, String?, OffsetDateTime?, Boolean?>.toHabit() = Habit(
        this[HABITS.ID]?.let { HabitId(it) } ?: error("Invalid habit ID"),
        this[HABITS.NAME]?.let { NonBlankString(it) } ?: error("Invalid habit name"),
        this[HABITS.TYPE]?.toHabitTypeConfiguration() ?: error("Invalid habit type configuration"),
        this[HABITS.STARTED_ON]?.toLocalDate() ?: error("Invalid started on date"),
        this[HABITS.IS_ARCHIVED] ?: error("Invalid isArchived value"))

    private fun String.toHabitTypeConfiguration() =
        if (this == "DAILY") Daily
        else this.substringBefore("#")
            .toInt()
            .let { Multiple(it) }
            ?.let { MultipleTimesADay(it) }
}
