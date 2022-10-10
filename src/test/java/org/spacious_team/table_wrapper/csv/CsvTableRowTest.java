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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.spacious_team.table_wrapper.api.TableCell;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class CsvTableRowTest {

    static Stream<Arguments> getRowNumRowColNum() {
        String[] row = new String[]{"1", "2.1", "abc", "2020-01-01 01:02:03"};
        return Stream.of(
                Arguments.of(1, row, 10),
                Arguments.of(5, row, 2),
                Arguments.of(8, new String[]{}, 11)
        );
    }

    @ParameterizedTest
    @MethodSource("getRowNumRowColNum")
    void getCell(int rowNum, String[] row, int colNum) {
        CsvTableRow csvTableRow = new CsvTableRow(row, rowNum);
        if (colNum >= row.length) {
            assertNull(csvTableRow.getCell(colNum));
        } else {
            CsvTableCell cell = CsvTableCell.of(row, colNum);
            assertEquals(cell, csvTableRow.getCell(colNum));
        }
    }

    @ParameterizedTest
    @MethodSource("getRowNumRowColNum")
    void getRowNum(int rowNum, String[] row) {
        CsvTableRow csvTableRow = new CsvTableRow(row, rowNum);
        assertEquals(rowNum, csvTableRow.getRowNum());
    }

    @ParameterizedTest
    @MethodSource("getRowNumRowColNum")
    void getFirstCellNum(int rowNum, String[] row) {
        CsvTableRow csv = new CsvTableRow(row, rowNum);
        if (row.length == 0) {
            assertEquals(-1, csv.getFirstCellNum());
        } else {
            assertEquals(0, csv.getFirstCellNum());
        }
    }

    @ParameterizedTest
    @MethodSource("getRowNumRowColNum")
    void getLastCellNum(int rowNum, String[] row) {
        CsvTableRow csv = new CsvTableRow(row, rowNum);
        assertEquals(row.length - 1, csv.getLastCellNum());
    }

    @Test
    void rowContains() {
        String[] row = new String[]{null, "1", "2.1", "2.20", "abc", "2020-01-01T01:02:03"};
        CsvTableRow csvTableRow = new CsvTableRow(row, 0);
        assertTrue(csvTableRow.rowContains(null));
        assertTrue(csvTableRow.rowContains(1));
        assertTrue(csvTableRow.rowContains(2.1D));
        assertFalse(csvTableRow.rowContains(2.2D)); // "2.2" != "2.20"
        assertTrue(csvTableRow.rowContains("abc"));
        LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 1, 2, 3);
        assertTrue(csvTableRow.rowContains(localDateTime));
    }

    @Test
    void iterator() {
        String[] row = new String[]{null, "1", "2.1", "2.20", "abc", "2020-01-01T01:02:03"};
        CsvTableRow csvTableRow = new CsvTableRow(row, 0);
        List<CsvTableCell> expected = IntStream.range(0, row.length)
                .mapToObj(colNum -> CsvTableCell.of(row, colNum))
                .collect(Collectors.toList());
        List<TableCell> actual = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(csvTableRow.iterator(), 0),
                        false)
                .collect(Collectors.toList());
        assertEquals(expected, actual);
    }

    @Test
    void equals() {
        String[] row1 = new String[]{"1", "2"};
        String[] row2 = new String[]{"1", "2"};
        assertEquals(new CsvTableRow(row1, 0), new CsvTableRow(row2, 0));
        assertNotEquals(new CsvTableRow(row1, 0), new CsvTableRow(row1, 1));
    }

    @Test
    void testHashCode() {
        String[] row1 = new String[]{"1", "2"};
        String[] row2 = new String[]{"1", "2"};
        assertEquals(new CsvTableRow(row1, 0).hashCode(), new CsvTableRow(row2, 0).hashCode());
    }
}
