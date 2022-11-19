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

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spacious_team.table_wrapper.api.CellDataAccessObject;
import org.spacious_team.table_wrapper.csv.CsvTableCell.RowAndIndex;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Objects;

@Builder
@ToString
@EqualsAndHashCode
public class CsvCellDataAccessObject implements CellDataAccessObject<RowAndIndex, CsvTableRow> {
    public static final CsvCellDataAccessObject INSTANCE = CsvCellDataAccessObject.create();
    /**
     * If null, date time format is derived from value
     */
    private final @Nullable DateTimeFormatter dateTimeFormatter;
    private final @Nullable LocalDate defaultDate;
    @Builder.Default
    private final LocalTime defaultTime = LocalTime.NOON;
    @Builder.Default
    private final ZoneId defaultZoneId = ZoneId.systemDefault();

    /**
     * Returns impl with default configuration.
     */
    public static CsvCellDataAccessObject create() {
        return CsvCellDataAccessObject.builder().build();
    }

    @Override
    public @Nullable RowAndIndex getCell(CsvTableRow row, Integer cellIndex) {
        @Nullable CsvTableCell cell = row.getCell(cellIndex);
        return (cell == null) ? null : cell.getRowAndIndex();
    }

    @Override
    public @Nullable String getValue(RowAndIndex cell) {
        return cell.getValue();
    }

    /**
     * Parses Instant. Date time format can be derived from cell value or can be configured.
     * Date time value examples witch format can be correctly derived:
     * <pre>
     * 2000-02-01
     * 01.02.2000
     * 2000.02.01
     * 01/02/2000
     * 2000/02/01
     * 20:10:02
     * 20:10:02.000
     * 2000-02-01T20:10:02
     * 01.02.2000 20:10:02
     * 01.02.2000 20:10:02.000
     * 20:10:02 2000/02/01
     * 20:10:02.000 2000/02/01
     * 2000-02-01T20:10:02Z
     * 01.02.2000 20:10:02Z
     * 01.02.2000 20:10:02.000Z
     * 20:10:02 2000/02/01Z
     * 01.02.2000 20:10:02UTC
     * 01.02.2000 20:10:02GMT
     * 01.02.2000 20:10:02Europe/Moscow
     * 01.02.2000 20:10:02.000Europe/Moscow
     * 01.02.2000 20:10:02MSK
     * 2000-02-01T20:10:02+0300
     * 01.02.2000 20:10:02+0300
     * 2000-02-01T20:10:02+03:00
     * 01.02.2000 20:10:02+03:00
     * 01.02.2000 20:10:02+03:00:00
     * 01.02.2000 20:10:02GMT+3
     * 01.02.2000 20:10:02GMT+3:00
     * 01.02.2000 20:10:02GMT+03:00
     * 01.02.2000 20:10:02GMT+03:00:00
     * 01.02.2000 20:10:02.000GMT+03:00:00
     * </pre>
     *
     * @implSpec If cell value doesn't contain zone name, when configured value used.
     * If cell value doesn't contain date, when configured value used (default configuration value is current date).
     * If cell value doesn't contain time, when configured value used (default configuration value is 12:00:00).
     */
    @Override
    public Instant getInstantValue(RowAndIndex cell) {
        @Nullable String value = getValue(cell);
        Objects.requireNonNull(value, "Not an instant");
        if (isCustomDateTimeFormat()) {
            return parseCustomFormatInstant(value);
        }
        int length = value.length();
        if (length == 8 || length == 12) { // without and with millis
            DateTimeFormatter formatter = getConfiguredOrParsedDateTimeFormatter(value);
            return LocalTime.parse(value, formatter)
                    .atDate(getDefaultDateOrNow())
                    .atZone(defaultZoneId)
                    .toInstant();
        } else if (length == 10) {
            DateTimeFormatter formatter = getConfiguredOrParsedDateTimeFormatter(value);
            return LocalDate.parse(value, formatter)
                    .atTime(defaultTime)
                    .atZone(defaultZoneId)
                    .toInstant();
        } else if (length == 19 || length == 23) { // without and with millis
            DateTimeFormatter formatter = getConfiguredOrParsedDateTimeFormatter(value);
            return LocalDateTime.parse(value, formatter)
                    .atZone(defaultZoneId)
                    .toInstant();
        } else if (length > 19) {
            DateTimeFormatter formatter = getConfiguredOrParsedDateTimeFormatter(value);
            return ZonedDateTime.parse(value, formatter)
                    .toInstant();
        }
        throw new IllegalArgumentException("Not an instant: " + value);
    }

    private boolean isCustomDateTimeFormat() {
        return dateTimeFormatter != null;
    }

    private Instant parseCustomFormatInstant(String value) {
        DateTimeFormatter formatter = getConfiguredOrParsedDateTimeFormatter(value);
        TemporalAccessor ta = formatter.parse(value);
        LocalDate localDate = ta.query(TemporalQueries.localDate());
        if (localDate == null) {
            localDate = getDefaultDateOrNow();
        }
        LocalTime localTime = ta.query(TemporalQueries.localTime());
        ZoneId zoneId = ta.query(TemporalQueries.zone());
        return localDate
                .atTime((localTime == null) ? defaultTime : localTime)
                .atZone((zoneId == null) ? defaultZoneId : zoneId)
                .toInstant();
    }

    private DateTimeFormatter getConfiguredOrParsedDateTimeFormatter(String value) {
        if (dateTimeFormatter == null) {
            return DateTimeFormatParser.getDateTimeFormatter(value);
        }
        return dateTimeFormatter;
    }

    private LocalDate getDefaultDateOrNow() {
        if (defaultDate == null) {
            return LocalDate.now();
        }
        return defaultDate;
    }
}
