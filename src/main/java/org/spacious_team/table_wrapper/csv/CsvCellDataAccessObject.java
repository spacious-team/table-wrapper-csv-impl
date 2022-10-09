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

import lombok.Getter;
import lombok.Setter;
import org.spacious_team.table_wrapper.api.CellDataAccessObject;
import org.spacious_team.table_wrapper.csv.CsvTableCell.RowAndIndex;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CsvCellDataAccessObject implements CellDataAccessObject<RowAndIndex, CsvTableRow> {
    public static final CsvCellDataAccessObject INSTANCE = new CsvCellDataAccessObject();
    @Setter
    @Getter
    public static DateTimeFormatter dateTimeFormatter = null;

    @Override
    public RowAndIndex getCell(CsvTableRow row, Integer cellIndex) {
        return row.getCell(cellIndex).getRowAndIndex();
    }

    @Override
    public String getValue(RowAndIndex cell) {
        return cell.getValue();
    }

    @Override
    public Instant getInstantValue(RowAndIndex cell) {
        String value = getValue(cell);
        DateTimeFormatter formatter = (dateTimeFormatter != null) ?
                dateTimeFormatter :
                DateTimeFormatParser.getFor(value);
        LocalDateTime dateTime = (value.length() == 10) ?
                LocalDate.parse(value, formatter).atTime(12, 0) :
                LocalDateTime.parse(value, formatter);
        return dateTime
                .atZone(ZoneOffset.systemDefault())
                .toInstant();
    }
}
