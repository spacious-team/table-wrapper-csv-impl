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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CsvTableRowTest {

    @ParameterizedTest
    @MethodSource("indexAndRow")
    void getCell(int rowNum, String[] row, int colNum) {
        CsvTableCell cell;
        CsvTableRow csvRow = new CsvTableRow(row, rowNum);
        if (colNum >= row.length) {
            assertNull(csvRow.getCell(colNum));
        } else {
            cell = CsvTableCell.of(row, colNum);
            assertEquals(cell, csvRow.getCell(colNum));
        }
    }

    @ParameterizedTest
    @MethodSource("indexAndRow")
    void getFirstCellNum(int rowNum, String[] row) {
        CsvTableRow csv = new CsvTableRow(row, rowNum);
        if (row.length == 0) {
            assertEquals(-1, csv.getFirstCellNum());
        } else {
            assertEquals(0, csv.getFirstCellNum());
        }
    }

    @ParameterizedTest
    @MethodSource("indexAndRow")
    void getLastCellNum(int rowNum, String[] row) {
        CsvTableRow csv = new CsvTableRow(row, rowNum);
        assertEquals(row.length - 1, csv.getLastCellNum());
    }

    private static Stream<Arguments> indexAndRow() {
        String[] row = new String[]{"1", "2.1", "abc", "2020-01-01 01:02:03"};
        return Stream.of(
                Arguments.of(1, row, 10),
                Arguments.of(5, row, 2),
                Arguments.of(8, new String[]{}, 11)
        );
    }
}
