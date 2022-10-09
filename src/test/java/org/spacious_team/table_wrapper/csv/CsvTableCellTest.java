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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvTableCellTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void getColumnIndex(int colNum) {
        String[] row = new String[5];
        CsvTableCell.RowAndIndex rowAndIndex = new CsvTableCell.RowAndIndex(row, colNum);
        CsvTableCell csv = new CsvTableCell(rowAndIndex);
        assertEquals(colNum, csv.getColumnIndex());
    }
}
