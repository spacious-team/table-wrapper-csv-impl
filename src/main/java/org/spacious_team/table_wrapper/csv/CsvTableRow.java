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
import org.spacious_team.table_wrapper.api.AbstractReportPageRow;
import org.spacious_team.table_wrapper.api.TableCell;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.spacious_team.table_wrapper.api.TableCellAddress.NOT_FOUND;
import static org.spacious_team.table_wrapper.csv.CsvTableHelper.equalsPredicate;

@ToString(of = "rowNum")
@EqualsAndHashCode(of = {"rowNum", "row"}, callSuper = false)
public class CsvTableRow extends AbstractReportPageRow {

    private final String[] row;
    @Getter
    private final int rowNum;
    private final TableCell[] cellsCache;

    public static CsvTableRow of(String[] row, int rowNum) {
        return new CsvTableRow(row, rowNum);
    }

    private CsvTableRow(String[] row, int rowNum) {
        this.row = row;
        this.rowNum = rowNum;
        this.cellsCache = new TableCell[row.length];
    }

    @Override
    public @Nullable TableCell getCell(int i) {
        if (i < 0 || i >= row.length) {
            return null;
        }
        TableCell cell = cellsCache[i];
        if (cell == null) {
            cell = CsvTableCell.of(row, i);
            cellsCache[i] = cell;
        }
        return cell;
    }

    @Nullable String getCellValue(int i) {
        return (i < 0 || i >= row.length) ? null : row[i];
    }

    @Override
    public int getFirstCellNum() {
        return (row.length > 0) ? 0 : -1;
    }

    @Override
    public int getLastCellNum() {
        return row.length - 1;
    }

    @Override
    public boolean rowContains(@Nullable Object value) {
        return CsvTableHelper.find(row, rowNum, 0, row.length, equalsPredicate(value)) != NOT_FOUND;
    }

    @Override
    public Iterator<@Nullable TableCell> iterator() {
        return new Iterator<@Nullable TableCell>() {
            private int cellIndex = 0;

            @Override
            public boolean hasNext() {
                return cellIndex < row.length;
            }

            @Override
            public @Nullable TableCell next() {
                if (hasNext()) {
                    return getCell(cellIndex++);
                }
                throw new NoSuchElementException();
            }
        };
    }
}
