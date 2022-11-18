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

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
final class DateTimeFormatParser {

    // internal calculated unique key -> date-time format
    private static final Map<Integer, DateTimeFormatter> dateTimeFormatters = new ConcurrentHashMap<>();

    static DateTimeFormatter getFor(String dateTime) {
        return (dateTime.length() == 10) ?
            getForDate(dateTime) :
            getForDateTime(dateTime);
    }

    static DateTimeFormatter getForDate(String date) {
        boolean isYearAtFirst;
        char dateSplitter;
        @SuppressWarnings("DuplicatedCode")
        char ch = date.charAt(date.length() - 5);
        if (!Character.isDigit(ch)) {
            // date format is DD MM YYYY
            isYearAtFirst = false;
            dateSplitter = ch;
        } else {
            // date format is YYYY MM DD
            isYearAtFirst = true;
            dateSplitter = date.charAt(date.length() - 3);
        }
        return getDateFormatter(isYearAtFirst, dateSplitter);
    }

    static DateTimeFormatter getForDateTime(String dateTime) {
        boolean isDateAtFirst;
        boolean isYearAtFirst;
        char dateSplitter;
        if (dateTime.charAt(2) == ':') {
            // format is <time> <date>
            isDateAtFirst = false;
            char ch = dateTime.charAt(dateTime.length() - 5);
            if (!Character.isDigit(ch)) {
                // date format is DD MM YYYY
                isYearAtFirst = false;
                dateSplitter = ch;
            } else {
                // date format is YYYY MM DD
                isYearAtFirst = true;
                dateSplitter = dateTime.charAt(dateTime.length() - 3);
            }
        } else {
            // format is <date> <time>
            isDateAtFirst = true;
            if (!Character.isDigit(dateTime.charAt(2))) {
                // date format is DD MM YYYY
                isYearAtFirst = false;
                dateSplitter = dateTime.charAt(2);
            } else {
                // date format is YYYY MM DD
                isYearAtFirst = true;
                dateSplitter = dateTime.charAt(4);
            }
        }
        return getDateTimeFormatter(isDateAtFirst, isYearAtFirst, dateSplitter);
    }

    private static DateTimeFormatter getDateFormatter(boolean isYearAtFirst, char dateSplitter) {
        Integer key = dateSplitter + 0x40000 + (isYearAtFirst ? 0x20000 : 0);
        @Nullable DateTimeFormatter result = dateTimeFormatters.get(key);
        if (result == null) {
            StringBuilder format = new StringBuilder();
            appendDate(isYearAtFirst, dateSplitter, format);
            result = DateTimeFormatter.ofPattern(format.toString());
            dateTimeFormatters.putIfAbsent(key, result);
        }
        return result;
    }

    /**
     * @param isDateAtFirst true if date-time format is '[date] [time]', false if '[time] [date]'
     * @param isYearAtFirst true if YYYY in [date] template at first, for example YYYY-MM-DD, false otherwise
     * @param dateSplitter date splitter char in date, for example for YYYY-MM-DD, should be '-'
     */
    private static DateTimeFormatter getDateTimeFormatter(boolean isDateAtFirst, boolean isYearAtFirst, char dateSplitter) {
        Integer key = dateSplitter + (isDateAtFirst ? 0x10000 : 0) + (isYearAtFirst ? 0x20000 : 0);
        @Nullable DateTimeFormatter result = dateTimeFormatters.get(key);
        if (result == null) {
            StringBuilder format = new StringBuilder();
            if (isDateAtFirst) {
                appendDate(isYearAtFirst, dateSplitter, format);
                format.append(" ");
                appendTime(format);
            } else {
                appendTime(format);
                format.append(" ");
                appendDate(isYearAtFirst, dateSplitter, format);
            }
            result = DateTimeFormatter.ofPattern(format.toString());
            dateTimeFormatters.putIfAbsent(key, result);
        }
        return result;
    }

    private static void appendDate(boolean isYearAtFirst, char dateSplitter, StringBuilder format) {
        if (isYearAtFirst) {
            format.append("yyyy").append(dateSplitter).append("MM").append(dateSplitter).append("dd");
        } else {
            format.append("dd").append(dateSplitter).append("MM").append(dateSplitter).append("yyyy");
        }
    }

    private static void appendTime(StringBuilder format) {
        format.append("HH:mm:ss");
    }
}
