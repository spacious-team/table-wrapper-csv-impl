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
import lombok.Getter;
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

    static DateTimeFormatter getDateTimeFormatter(String dateTimeOffset) {
        Pattern pattern = getPattern(dateTimeOffset);
        return getDateTimeFormatter(pattern);
    }

    private static Pattern getPattern(String dateTimeOffset) {
        @Nullable Pattern pattern = null;
        int length = dateTimeOffset.length();
        if (length == 8 || length == 12) { // without and with millis
            pattern = getForTime(dateTimeOffset, 0);
        } else if (length == 10) {
            pattern = getForDate(dateTimeOffset, 0);
        } else if (length == 19 || length == 23) { // without and with millis
            pattern = getForDateTime(dateTimeOffset);
        } else if (length > 19) {
            pattern = getForDateTimeZone(dateTimeOffset);
        }
        if (pattern == null) {
            throw new IllegalArgumentException("Unknown date time format for: " + dateTimeOffset);
        }
        return pattern;
    }

    private static TimePattern getForTime(String time, int offset) {
        boolean hasMillis = (time.length() > (offset + 8)) && (time.charAt(offset + 8) == '.');
        return TimePattern.of(hasMillis);
    }

    private static DatePattern getForDate(String date, int dateOffset) {
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

    private static DateTimePattern getForDateTime(String dateTime) {
        boolean isDateAtFirst;
        DatePattern datePattern;
        TimePattern timePattern;
        char dateTimeSeparator;
        if (dateTime.charAt(2) == ':') {
            // format is <time> <date>
            isDateAtFirst = false;
            timePattern = getForTime(dateTime, 0);
            boolean hasMillis = timePattern.isHasMillis();
            datePattern = getForDate(dateTime, hasMillis ? 13 : 9);
            dateTimeSeparator = dateTime.charAt(hasMillis ? 12 : 8);
        } else {
            // format is <date> <time>
            isDateAtFirst = true;
            datePattern = getForDate(dateTime, 0);
            timePattern = getForTime(dateTime, 11);
            dateTimeSeparator = dateTime.charAt(10);
        }
        return DateTimePattern.of(isDateAtFirst, datePattern, timePattern, dateTimeSeparator);
    }

    private static ZonedDateTimePattern getForDateTimeZone(String dateTimeOffset) {
        String zonePattern;
        int length = dateTimeOffset.length();
        char char19 = dateTimeOffset.charAt(19);
        if (length == 20) {
            // Z timezone
            zonePattern = "VV";
        } else if (length == 22) {
            // MSK, UTC, GMT
            zonePattern = "z";
        } else if (char19 == 'G') {
            if (length >= 23 && dateTimeOffset.charAt(23) == '0') {
                // GMT+03:00 / GMT+03:00:00
                zonePattern = "OOOO";
            } else {
                // GMT / GMT+3 / GMT+3:30
                zonePattern = "O";
            }
        } else if (char19 == '+' || char19 == '-') {
            if (length > 23 && dateTimeOffset.indexOf('[', 23) != -1) {
                // +01:00[Europe/Paris]'
                zonePattern = "xxxxx[VV]";
            } else if (length > 22 && dateTimeOffset.charAt(22) == ':') {
                // +03:00
                zonePattern = "xxxxx";
            } else {
                // +0300
                zonePattern = "xxxx";
            }
        } else {
            // fallback: Europe/Paris
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

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor(staticName = "of")
    private static class TimePattern implements Pattern {
        private final boolean hasMillis;

        public void build(StringBuilder format) {
            if (hasMillis) {
                format.append("HH:mm:ss.SSS");
            } else {
                format.append("HH:mm:ss");
            }
        }
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor(staticName = "of")
    private static class DateTimePattern implements Pattern {
        private final boolean isDateAtFirst;
        private final DatePattern datePattern;
        private final TimePattern timePattern;
        private final char dateTimeSeparator;

        public void build(StringBuilder format) {
            if (isDateAtFirst) {
                datePattern.build(format);
                format.append('\'');
                format.append(dateTimeSeparator);
                format.append('\'');
                timePattern.build(format);
            } else {
                timePattern.build(format);
                format.append('\'');
                format.append(dateTimeSeparator);
                format.append('\'');
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
