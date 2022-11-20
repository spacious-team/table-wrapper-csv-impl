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
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CsvTableCellTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void getColumnIndex(int colNum) {
        String[] row = new String[2];
        CsvTableCell cell = CsvTableCell.of(row, colNum);
        assertEquals(colNum, cell.getColumnIndex());
    }

    @Test
    void getValue() {
        String[] row = new String[]{"object1", "object2"};
        CsvTableCell cell = CsvTableCell.of(row, 0);
        assertEquals("object1", cell.getValue());

        CsvTableCell notEqualsCell = CsvTableCell.of(row, 1);
        assertNotEquals(notEqualsCell.getValue(), cell.getValue());
    }

    @Test
    void getIntValue() {
        String[] row = new String[]{"1024", "1025"};
        CsvTableCell cell = CsvTableCell.of(row, 0);
        assertEquals(1024, cell.getIntValue());

        CsvTableCell notEqualsCell = CsvTableCell.of(row, 1);
        assertNotEquals(notEqualsCell.getIntValue(), cell.getIntValue());
    }

    @Test
    void getLongValue() {
        String[] row = new String[]{"1024", "1025"};
        CsvTableCell cell = CsvTableCell.of(row, 0);
        assertEquals(1024L, cell.getLongValue());

        CsvTableCell notEqualsCell = CsvTableCell.of(row, 1);
        assertNotEquals(notEqualsCell.getLongValue(), cell.getLongValue());
    }

    @Test
    void getDoubleValue() {
        String[] row = new String[]{"10.24", "10.24000", "10.2400000000000000000000000000000000001", "10.24001"};
        CsvTableCell cell0 = CsvTableCell.of(row, 0);
        assertEquals(10.24D, cell0.getDoubleValue());

        CsvTableCell cell1 = CsvTableCell.of(row, 1);
        assertEquals(10.24D, cell1.getDoubleValue());

        CsvTableCell cell2 = CsvTableCell.of(row, 2);
        assertEquals(10.24D, cell2.getDoubleValue());

        CsvTableCell cell3 = CsvTableCell.of(row, 3);
        assertNotEquals(cell2.getDoubleValue(), cell3.getDoubleValue());
    }

    /**
     * @see <a href="https://stackoverflow.com/questions/6787142/bigdecimal-equals-versus-compareto">Stack overflow</a>
     */
    @Test
    void getBigDecimalValue() {
        BigDecimal expected = new BigDecimal("10.24");
        String[] row = new String[]{"10.24", "10.24000", "10.2400000000000000000000000000000000001"};

        CsvTableCell cell0 = CsvTableCell.of(row, 0);
        CsvTableCell cell1 = CsvTableCell.of(row, 1);
        CsvTableCell cell2 = CsvTableCell.of(row, 2);

        assertEquals(expected, cell0.getBigDecimalValue());
        assertNotEquals(expected, cell1.getBigDecimalValue());
        assertNotEquals(expected, cell2.getBigDecimalValue());

        assertEquals(0, cell0.getBigDecimalValue().compareTo(cell1.getBigDecimalValue()));
        assertEquals(-1, cell0.getBigDecimalValue().compareTo(cell2.getBigDecimalValue()));
        assertEquals(-1, cell1.getBigDecimalValue().compareTo(cell2.getBigDecimalValue()));
    }

    @Test
    void getStringValue() {
        String[] row = new String[]{"object1", "object2"};
        CsvTableCell cell = CsvTableCell.of(row, 0);
        assertEquals("object1", cell.getStringValue());

        CsvTableCell notEqualsCell = CsvTableCell.of(row, 1);
        assertNotEquals(notEqualsCell.getStringValue(), cell.getStringValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2022-10-11", "11-10-2022", "11 10 2022", "2022/10/11", "11.10.2022"})
    void getInstantValueWithDate(String date) {
        Instant expected = LocalDate.of(2022, 10, 11)
                .atTime(12, 0)
                .atZone(ZoneOffset.systemDefault())
                .toInstant();
        String[] row = new String[]{date};
        CsvTableCell cell = CsvTableCell.of(row, 0);
        assertEquals(expected, cell.getInstantValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2022-10-11 03:01:00", "03:01:00 11-10-2022", "11 10 2022 03:01:00",
            "03:01:00 2022/10/11", "11.10.2022 03:01:00"})
    void getInstantValueWithDateTime(String dateTime) {
        Instant expected = LocalDate.of(2022, 10, 11)
                .atTime(3, 1)
                .atZone(ZoneOffset.systemDefault())
                .toInstant();
        String[] row = new String[]{dateTime};
        CsvTableCell cell = CsvTableCell.of(row, 0);
        assertEquals(expected, cell.getInstantValue());
    }

    @ParameterizedTest
    @ValueSource(strings = "2022-10-11T03:01:00+03:00")
    void getInstantValueWithFormat(String dateTime) {
        ZoneId zoneId = ZoneOffset.systemDefault();
        InstantParser instantParser = InstantParser.builder()
                .dateTimeFormatter(DateTimeFormatter.ISO_DATE_TIME)
                .defaultZoneId(zoneId)
                .build();
        CsvCellDataAccessObject dao = CsvCellDataAccessObject.of(instantParser);
        Instant expected = LocalDate.of(2022, 10, 11)
                .atTime(3, 1)
                .atZone(zoneId)
                .toInstant();
        String[] row = new String[]{dateTime};
        CsvTableCell cell = CsvTableCell.of(row, 0, dao);
        assertEquals(expected, cell.getInstantValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2022-10-11 03:01:00", "03:01:00 11-10-2022", "11 10 2022 03:01:00",
            "03:01:00 2022/10/11", "11.10.2022 03:01:00"})
    void getLocalDateTimeValue(String dateTime) {
        LocalDateTime expected = LocalDate.of(2022, 10, 11)
                .atTime(3, 1);
        String[] row = new String[]{dateTime};
        CsvTableCell cell = CsvTableCell.of(row, 0);
        assertEquals(expected, cell.getLocalDateTimeValue());
    }

    @Test
    void equals() {
        String[] row = new String[]{"abc", "abc"};
        assertEquals(
                CsvTableCell.of(row, 0),
                CsvTableCell.of(row, 1));
    }

    @Test
    void testHashCode() {
        String[] row = new String[]{"abc", "abc"};
        assertEquals(
                CsvTableCell.of(row, 0).hashCode(),
                CsvTableCell.of(row, 1).hashCode());
    }
}
