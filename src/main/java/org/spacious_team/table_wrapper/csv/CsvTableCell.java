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
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spacious_team.table_wrapper.api.AbstractTableCell;
import org.spacious_team.table_wrapper.api.EmptyTableCell;
import org.spacious_team.table_wrapper.api.TableCell;

@ToString
@EqualsAndHashCode(callSuper = false)
public class CsvTableCell extends AbstractTableCell<String> {

    @Getter
    private final int columnIndex;
    private final String value;

    public static TableCell of(String[] row, int columnIndex) {
        return of(row, columnIndex, CsvCellDataAccessObject.INSTANCE);
    }

    public static TableCell of(String[] row, int columnIndex, CsvCellDataAccessObject dao) {
        @Nullable String cellValue = getCellValue(row, columnIndex);
        return cellValue == null ?
                EmptyTableCell.of(columnIndex) :
                new CsvTableCell(cellValue, columnIndex, dao);
    }

    private static @Nullable String getCellValue(String[] row, int columnIndex) {
        return (columnIndex >= 0) && (columnIndex < row.length) ?
                row[columnIndex] :
                null;
    }

    private CsvTableCell(String cellValue, int columnIndex, CsvCellDataAccessObject dao) {
        super(cellValue, dao);
        this.value = cellValue;
        this.columnIndex = columnIndex;
    }
}
