/*
 * Table Wrapper Xml SpreadsheetML Impl
 * Copyright (C) 2022  Vitalii Ananev <spacious-team@ya.ru>
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
import lombok.RequiredArgsConstructor;
import org.spacious_team.table_wrapper.api.AbstractTableCell;

public class CsvTableCell extends AbstractTableCell<CsvTableCell.RowAndIndex> {

    @Getter
    private final RowAndIndex rowAndIndex;

    public static CsvTableCell of(String[] row, int columnIndex) {
        return new CsvTableCell(new RowAndIndex(row, columnIndex));
    }

    public CsvTableCell(RowAndIndex rowAndIndex) {
        super(rowAndIndex, CsvCellDataAccessObject.INSTANCE);
        this.rowAndIndex = rowAndIndex;
    }

    @Override
    public int getColumnIndex() {
        return rowAndIndex.getColumnIndex();
    }

    @Getter
    @RequiredArgsConstructor
    static class RowAndIndex {
        final String[] row;
        final int columnIndex;
    }
}
