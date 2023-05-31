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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.spacious_team.table_wrapper.api.TableCell;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static nl.jqno.equalsverifier.Warning.ALL_FIELDS_SHOULD_BE_USED;
import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.Assertions.*;

class CsvTableRowTest {

    static Stream<Arguments> getRowNumRowColNum() {
        String[] row = new String[]{"1", "2.1", "abc", "2020-01-01 01:02:03"};
        return Stream.of(
                Arguments.of(1, row, -1),
                Arguments.of(1, row, 10),
                Arguments.of(5, row, 2),
                Arguments.of(8, new String[]{}, 11)
        );
    }

    @ParameterizedTest
    @MethodSource("getRowNumRowColNum")
    void getCell(int rowNum, String[] row, int colNum) {
        CsvTableRow csvTableRow = CsvTableRow.of(row, rowNum);
        if (colNum < 0 || colNum >= row.length) {
            assertNull(csvTableRow.getCell(colNum));
        } else {
            TableCell expected = CsvTableCell.of(row, colNum);
            assertEquals(expected, csvTableRow.getCell(colNum));
            assertSame(csvTableRow.getCell(colNum), csvTableRow.getCell(colNum)); // next call returns same instant
        }
    }

    @ParameterizedTest
    @MethodSource("getRowNumRowColNum")
    void getCellValue(int rowNum, String[] row, int colNum) {
        CsvTableRow csvTableRow = CsvTableRow.of(row, rowNum);
        if (colNum < 0 || colNum >= row.length) {
            assertNull(csvTableRow.getCellValue(colNum));
        } else {
            assertEquals(row[colNum], csvTableRow.getCellValue(colNum));
        }
    }

    @ParameterizedTest
    @MethodSource("getRowNumRowColNum")
    void getRowNum(int rowNum, String[] row) {
        CsvTableRow csvTableRow = CsvTableRow.of(row, rowNum);
        assertEquals(rowNum, csvTableRow.getRowNum());
    }

    @ParameterizedTest
    @MethodSource("getRowNumRowColNum")
    void getFirstCellNum(int rowNum, String[] row) {
        CsvTableRow csv = CsvTableRow.of(row, rowNum);
        if (row.length == 0) {
            assertEquals(-1, csv.getFirstCellNum());
        } else {
            assertEquals(0, csv.getFirstCellNum());
        }
    }

    @ParameterizedTest
    @MethodSource("getRowNumRowColNum")
    void getLastCellNum(int rowNum, String[] row) {
        CsvTableRow csv = CsvTableRow.of(row, rowNum);
        assertEquals(row.length - 1, csv.getLastCellNum());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void rowContains() {
        String[] row = new String[]{null, "1", "2.1", "2.20", "abc", "2020-01-01T01:02:03"};
        CsvTableRow csvTableRow = CsvTableRow.of(row, 0);
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
        @SuppressWarnings("ConstantConditions")
        String[] row = new String[]{null, "1", "2.1", "2.20", "abc", "2020-01-01T01:02:03"};
        CsvTableRow csvTableRow = CsvTableRow.of(row, 0);
        List<TableCell> expected = IntStream.range(0, row.length)
                .mapToObj(colNum -> CsvTableCell.of(row, colNum))
                .collect(Collectors.toList());
        List<TableCell> actual = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(csvTableRow.iterator(), 0),
                        false)
                .collect(Collectors.toList());
        assertEquals(expected, actual);
    }

    @Test
    void iteratorThrows() {
        @SuppressWarnings("ConstantConditions")
        String[] row = new String[]{null, "1"};
        CsvTableRow csvTableRow = CsvTableRow.of(row, 0);
        Iterator<@Nullable TableCell> it = csvTableRow.iterator();
        //noinspection ConstantConditions
        assertNull(it.next().getValue());
        //noinspection ConstantConditions
        assertEquals("1", it.next().getValue());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(CsvTableRow.class)
                .suppress(STRICT_INHERITANCE) // no subclass for test
                .suppress(ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    void testToString() {
        assertEquals("CsvTableRow(rowNum=0)", CsvTableRow.of(new String[]{"1", "2"}, 0).toString());
    }
}
