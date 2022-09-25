package org.stypox.tridenta.db

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object DbTypeConverters {
    /*@TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let { DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(it, OffsetDateTime::from) }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }*/
    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String): OffsetDateTime {
        return value.let { DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(it, OffsetDateTime::from) }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime): String {
        return date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}