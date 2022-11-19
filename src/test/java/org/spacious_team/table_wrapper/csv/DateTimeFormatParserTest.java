/*
 * Table Wrapper CSV Impl
 * Copyright (C) 2022  Spacious Team <spacious-team@ya.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.spacious_team.table_wrapper.csv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;

class DateTimeFormatParserTest {

    @Test
    void getDateTimeFormatterThrowable() {
        assertThrows(IllegalArgumentException.class,
                () -> DateTimeFormatParser.getDateTimeFormatter("illegal"));
    }

    @Test
    void cache() {
        DateTimeFormatter expected = DateTimeFormatParser.getDateTimeFormatter("01.02.2020");
        DateTimeFormatter actual = DateTimeFormatParser.getDateTimeFormatter("31.12.2020");
        assertSame(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getInstantExamples")
    void getDateTimeFormatter(String actual, Temporal expected) {
        DateTimeFormatter dtf = DateTimeFormatParser.getDateTimeFormatter(actual);
        int length = actual.length();
        if (length == 8 || length == 12) { // without and with millis
            assertEquals(expected, LocalTime.parse(actual, dtf));
        } else if (length == 10) {
            assertEquals(expected, LocalDate.parse(actual, dtf));
        } else if (length == 19 || length == 23) {
            assertEquals(expected, LocalDateTime.parse(actual, dtf));
        } else {
            assertEquals(Instant.from(expected), ZonedDateTime.parse(actual, dtf).toInstant());
        }
    }

    static Object[][] getInstantExamples() {
        LocalDate date = LocalDate.of(2000, 2, 1);
        LocalTime time = LocalTime.of(20, 10, 2);
        ZoneId zoneID = ZoneId.of("Europe/Moscow");
        ZoneOffset zoneOffset = ZoneOffset.ofHours(3);
        return new Object[][]{
                {"2000-02-01", date},
                {"01.02.2000", date},
                {"2000.02.01", date},
                {"01/02/2000", date},
                {"2000/02/01", date},
                {"20:10:02", time},
                {"20:10:02.000", time},
                {"2000-02-01T20:10:02", date.atTime(time)},
                {"01.02.2000 20:10:02", date.atTime(time)},
                {"01.02.2000 20:10:02.000", date.atTime(time)},
                {"20:10:02 2000/02/01", date.atTime(time)},
                {"20:10:02.000 2000/02/01", date.atTime(time)},
                {"2000-02-01T20:10:02Z", date.atTime(time).atZone(UTC)},
                {"01.02.2000 20:10:02Z", date.atTime(time).atZone(UTC)},
                {"01.02.2000 20:10:02.000Z", date.atTime(time).atZone(UTC)},
                {"20:10:02 2000/02/01Z", date.atTime(time).atZone(UTC)},
                {"01.02.2000 20:10:02UTC", date.atTime(time).atZone(UTC)},
                {"01.02.2000 20:10:02GMT", date.atTime(time).atZone(UTC)},
                {"01.02.2000 20:10:02Europe/Moscow", date.atTime(time).atZone(zoneID)},
                {"01.02.2000 20:10:02.000Europe/Moscow", date.atTime(time).atZone(zoneID)},
                {"01.02.2000 20:10:02MSK", date.atTime(time).atZone(zoneID)},
                {"2000-02-01T20:10:02+0300", date.atTime(time).atZone(zoneOffset)},
                {"01.02.2000 20:10:02+0300", date.atTime(time).atZone(zoneOffset)},
                {"2000-02-01T20:10:02+03:00", date.atTime(time).atZone(zoneOffset)},
                {"01.02.2000 20:10:02+03:00", date.atTime(time).atZone(zoneOffset)},
                {"01.02.2000 20:10:02+03:00:00", date.atTime(time).atZone(zoneOffset)},
                {"01.02.2000 20:10:02GMT+3", date.atTime(time).atZone(zoneOffset)},
                {"01.02.2000 20:10:02GMT+3:00", date.atTime(time).atZone(zoneOffset)},
                {"01.02.2000 20:10:02GMT+03:00", date.atTime(time).atZone(zoneOffset)},
                {"01.02.2000 20:10:02GMT+03:00:00", date.atTime(time).atZone(zoneOffset)},
                {"01.02.2000 20:10:02.000GMT+03:00:00", date.atTime(time).atZone(zoneOffset)},
        };
    }
}