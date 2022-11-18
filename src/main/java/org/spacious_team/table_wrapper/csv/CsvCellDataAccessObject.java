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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spacious_team.table_wrapper.api.CellDataAccessObject;
import org.spacious_team.table_wrapper.csv.CsvTableCell.RowAndIndex;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class CsvCellDataAccessObject implements CellDataAccessObject<RowAndIndex, CsvTableRow> {
    public static final CsvCellDataAccessObject INSTANCE = new CsvCellDataAccessObject();
    /**
     * If null, date time format is derived from value
     */
    private final @Nullable DateTimeFormatter dateTimeFormatter;
    private final @Nullable ZoneId defaultZone;

    public CsvCellDataAccessObject() {
        this(null, null);
    }

    public CsvCellDataAccessObject(@Nullable DateTimeFormatter dateTimeFormatter,
                                   @Nullable ZoneId defaultZone) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.defaultZone = defaultZone;
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

    @Override
    public Instant getInstantValue(RowAndIndex cell) {
        @Nullable String value = getValue(cell);
        Objects.requireNonNull(value, "Not an instant");
        DateTimeFormatter formatter = (dateTimeFormatter != null) ?
                dateTimeFormatter :
                DateTimeFormatParser.getFor(value);
        LocalDateTime dateTime = (value.length() == 10) ?
                LocalDate.parse(value, formatter).atTime(12, 0) : //TODO formatter can parse time and zone
                LocalDateTime.parse(value, formatter);
        return dateTime
                .atZone(defaultZone == null ? ZoneId.systemDefault() : defaultZone)
                .toInstant();
    }
}
