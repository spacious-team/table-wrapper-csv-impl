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

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
final class DateTimeFormatParser {

    private static final Map<Pattern, DateTimeFormatter> dateTimeFormatters = new ConcurrentHashMap<>();

    static DateTimeFormatter getFor(String dateTimeOffset) {
        @Nullable Pattern pattern = null;
        int length = dateTimeOffset.length();
        if (length == 10) {
            pattern = getForDate(dateTimeOffset, 0);
        } else if (length == 18) {
            pattern = getForDateTime(dateTimeOffset);
        } else if (length > 18) {
            pattern = getForDateTimeZone(dateTimeOffset);
        }
        if (pattern == null) {
            throw new IllegalArgumentException("Unknown date time format for: " + dateTimeOffset);
        }
        return getDateTimeFormatter(pattern);
    }

    static DatePattern getForDate(String date, int dateOffset) {
        boolean isYearAtFirst;
        char dateSplitter;
        char ch = date.charAt(dateOffset + 2);
        if (!Character.isDigit(ch)) {
            // date format is DD MM YYYY
            isYearAtFirst = false;
            dateSplitter = ch;
        } else {
            // date format is YYYY MM DD
            isYearAtFirst = true;
            dateSplitter = date.charAt(dateOffset + 4);
        }
        return DatePattern.of(isYearAtFirst, dateSplitter);
    }

    static DateTimePattern getForDateTime(String dateTime) {
        boolean isDateAtFirst;
        DatePattern datePattern;
        if (dateTime.charAt(2) == ':') {
            // format is <time> <date>
            isDateAtFirst = false;
            datePattern = getForDate(dateTime, 9);
        } else {
            // format is <date> <time>
            isDateAtFirst = true;
            datePattern = getForDate(dateTime, 0);
        }
        return DateTimePattern.of(isDateAtFirst, datePattern);
    }

    static ZonedDateTimePattern getForDateTimeZone(String dateTimeOffset) {
        String zonePattern;
        int length = dateTimeOffset.length();
        if (length == 19) {
            // Z timezone
            zonePattern = "VV";
        } else if (length == 21) {
            // MSK
            zonePattern = "z";
        } else if (dateTimeOffset.indexOf("/", 19) != -1) {
            // "Europe/Paris"
            zonePattern = "VV";
        } else if (dateTimeOffset.indexOf("+", 19) != -1) {
            if (dateTimeOffset.charAt(21) == ':') {
                // +03:00
                zonePattern = "XXX";
            } else {
                // +0300
                zonePattern = "XX";
            }
        } else if (dateTimeOffset.charAt(18) == 'G' || dateTimeOffset.charAt(18) == 'U') {
            if (length >= 23 && dateTimeOffset.charAt(22) == '0') {
                // GMT+03:00 / UTC+03:00:00
                zonePattern = "OOOO";
            } else {
                // GMT / GMT+3 / UTC / UTC+330
                zonePattern = "O";
            }
        } else {
            // fallback
            zonePattern = "VV";
        }
        DateTimePattern dateTimePattern = getForDateTime(dateTimeOffset);
        return ZonedDateTimePattern.of(zonePattern, dateTimePattern);
    }

    private static DateTimeFormatter getDateTimeFormatter(Pattern pattern) {
        return dateTimeFormatters.computeIfAbsent(pattern, DateTimeFormatParser::buildDateTimePattern);
    }

    private static DateTimeFormatter buildDateTimePattern(Pattern pattern) {
        StringBuilder builder = new StringBuilder();
        pattern.build(builder);
        return DateTimeFormatter.ofPattern(builder.toString());
    }

    private interface Pattern {
        void build(StringBuilder format);
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor(staticName = "of")
    private static class DatePattern implements Pattern {
        private final boolean isYearAtFirst;
        private final char dateSplitter;

        public void build(StringBuilder format) {
            if (isYearAtFirst) {
                format.append("yyyy").append(dateSplitter).append("MM").append(dateSplitter).append("dd");
            } else {
                format.append("dd").append(dateSplitter).append("MM").append(dateSplitter).append("yyyy");
            }
        }
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor(staticName = "of")
    private static class DateTimePattern implements Pattern {
        private final boolean isDateAtFirst;
        private final DatePattern datePattern;

        public void build(StringBuilder format) {
            if (isDateAtFirst) {
                datePattern.build(format);
                format.append(" HH:mm:ss");
            } else {
                format.append("HH:mm:ss ");
                datePattern.build(format);
            }
        }
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor(staticName = "of")
    private static class ZonedDateTimePattern implements Pattern {
        private final String zoneFormatter;
        private final DateTimePattern dateTimePattern;

        public void build(StringBuilder format) {
            dateTimePattern.build(format);
            format.append(zoneFormatter);
        }
    }
}
