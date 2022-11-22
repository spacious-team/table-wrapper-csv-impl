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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.spacious_team.table_wrapper.api.TableCellAddress;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CsvReportPageTest {

    @ParameterizedTest
    @ValueSource(strings = {"UTF-8", "Windows-1251"})
    void testInputDataCharset(String charsetName) throws IOException {
        String expected = "Текст на UTF-8";
        Charset charset = Charset.forName(charsetName);
        byte[] bytes = (expected + ",").getBytes(charset);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        CsvReportPage reportPage = new CsvReportPage(is, charset, CsvReportPage.getDefaultCsvParserSettings());

        //noinspection ConstantConditions
        @Nullable Object actual = reportPage.getRow(0)
                .getCell(0)
                .getValue();

        assertEquals(expected, actual);
    }

    @Test
    void getRow() {
        String[][] rows = new String[][] {
                {"11", "12"},
                {"21", "22"}
        };
        CsvReportPage reportPage = new CsvReportPage(rows);

        assertEquals(CsvTableRow.of(rows[0], 0), reportPage.getRow(0));
        assertEquals(CsvTableRow.of(rows[1], 1), reportPage.getRow(1));
        assertNull(reportPage.getRow(2));
        assertNull(reportPage.getRow(-1));
    }

    @ParameterizedTest
    @MethodSource("getRowsAndCount")
    void getLastRowNum(String rows, int expectedRowCount) throws IOException {
        InputStream is = new ByteArrayInputStream(rows.getBytes());
        CsvReportPage reportPage = new CsvReportPage(is);

        assertEquals(expectedRowCount, reportPage.getLastRowNum());
    }

    static Object[][] getRowsAndCount() {
        return new Object[][] {
                {"", -1},
                {"1", 0},
                {"1\n2", 1},
                {"1\r2", 1},
                {"1\r\n2", 1},
                {"1\n2\n", 1},
                {"1\r2\r", 1},
                {"1\r\n2\r\n", 1},
        };
    }

    @Test
    void findByValue() {
        String[][] rows = new String[][] {
                {"11", "12"},
                {"21", "22"}
        };
        CsvReportPage reportPage = new CsvReportPage(rows);

        assertEquals(TableCellAddress.of(0, 1),
                reportPage.find("12", 0, 2, 0, 2));
        assertEquals(TableCellAddress.NOT_FOUND,
                reportPage.find("12", 1, 2, 0, 2));
        assertEquals(TableCellAddress.NOT_FOUND,
                reportPage.find("12", 0, 2, 2, 3));
        assertEquals(TableCellAddress.NOT_FOUND,
                reportPage.find("xyz", 0, 2, 0, 2));
    }

    @Test
    void findByPrefix() {
        String[][] rows = new String[][] {
                {"11", "12"},
                {"21", "22"}
        };
        CsvReportPage reportPage = new CsvReportPage(rows);

        assertEquals(TableCellAddress.of(0, 1),
                reportPage.find(0, 2, 0, 2, "12"::equals));
        assertEquals(TableCellAddress.NOT_FOUND,
                reportPage.find(1, 2, 0, 2, "12"::equals));
        assertEquals(TableCellAddress.NOT_FOUND,
                reportPage.find(0, 2, 2, 3, "12"::equals));
        assertEquals(TableCellAddress.NOT_FOUND,
                reportPage.find(0, 2, 0, 2, "xyz"::equals));
    }
}