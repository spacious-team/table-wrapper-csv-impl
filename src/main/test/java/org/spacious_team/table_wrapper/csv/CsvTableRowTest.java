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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CsvTableRowTest {

    @ParameterizedTest
    @MethodSource("indexAndRow")
    void getCell(int i, String[] row) {
        CsvTableCell cell;
        CsvTableRow csvRow = new CsvTableRow(row, i);
        if (i >= row.length) {
            assertNull(csvRow.getCell(i));
            return;
        }
        cell = csvRow.cellsCache[i];
        if (cell == null) {
            cell = CsvTableCell.of(row, i);
            csvRow.cellsCache[i] = cell;
        }
        assertEquals(csvRow.getCell(i), cell);
    }

    @ParameterizedTest
    @MethodSource("indexAndRow")
    void getFirstCellNum(int i, String[] row) {
        CsvTableRow csv = new CsvTableRow(row, i);
        if (row.length < 0) {
            assertEquals(-1, csv.getFirstCellNum());
        } else {
            assertEquals(0, csv.getFirstCellNum());
        }
    }

    @ParameterizedTest
    @MethodSource("indexAndRow")
    void getLastCellNum(int i, String[] row) {
        CsvTableRow csv = new CsvTableRow(row, i);
        assertEquals(row.length - 1, csv.getLastCellNum());
    }

    private static Stream<Arguments> indexAndRow() {
        String[] row = new String[]{"1", "2", "3"};
        return Stream.of(
                Arguments.of(1, row),
                Arguments.of(5, row),
                Arguments.of(7, row),
                Arguments.of(2, row)
        );
    }
}