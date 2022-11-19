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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spacious_team.table_wrapper.csv.CsvTableCell.RowAndIndex;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;

import static java.time.LocalTime.NOON;
import static java.time.ZoneOffset.UTC;
import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvCellDataAccessObjectTest {

    CsvCellDataAccessObject dao;
    @Mock
    CsvTableRow row;
    @Mock
    CsvTableCell cell;

    @BeforeEach
    void setUp() {
        dao = spy(CsvCellDataAccessObject.INSTANCE);
    }

    @Test
    void getCellNull() {
        assertNull(dao.getCell(row, 1));
    }

    @Test
    void getCellNonNull() {
        //noinspection ConstantConditions
        when(row.getCell(1)).thenReturn(cell);
        dao.getCell(row, 1);
        verify(cell).getRowAndIndex();
    }

    @Test
    void getValue() {
        RowAndIndex rowAndIndex = new RowAndIndex(new String[]{"test"}, 0);
        assertEquals("test", dao.getValue(rowAndIndex));
    }

    @Test
    void getInstantValueNull() {
        RowAndIndex rowAndIndex = new RowAndIndex(new String[0], 1);
        assertThrows(NullPointerException.class, () -> dao.getInstantValue(rowAndIndex));
    }

    @Test
    void getInstantValueThrowable() {
        RowAndIndex rowAndIndex1 = new RowAndIndex(new String[]{"test"}, 0);
        assertThrows(IllegalArgumentException.class, () -> dao.getInstantValue(rowAndIndex1));

        RowAndIndex rowAndIndex2 = new RowAndIndex(new String[]{"01234567890"}, 0);
        assertThrows(IllegalArgumentException.class, () -> dao.getInstantValue(rowAndIndex2));
    }

    @ParameterizedTest
    @MethodSource("getInstantExamples")
    void getInstant(String actual, Temporal expected) {
        CsvCellDataAccessObject dao = CsvCellDataAccessObject.create();
        Instant expectedInstant = toInstant(expected, LocalDate.now(), NOON, ZoneId.systemDefault());
        RowAndIndex rowAndIndex = new RowAndIndex(new String[]{actual}, 0);

        assertEquals(expectedInstant, dao.getInstantValue(rowAndIndex));
    }

    @ParameterizedTest
    @MethodSource("getInstantExamples")
    void getInstantWithSpecifiedZone(String actual, Temporal expected) {
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        CsvCellDataAccessObject dao = CsvCellDataAccessObject.builder()
                .defaultZoneId(zoneId)
                .build();
        Instant expectedInstant = toInstant(expected, LocalDate.now(), NOON, zoneId);
        RowAndIndex rowAndIndex = new RowAndIndex(new String[]{actual}, 0);

        assertEquals(expectedInstant, dao.getInstantValue(rowAndIndex));
    }

    @ParameterizedTest
    @MethodSource("getInstantExamples")
    void getInstantWithSpecifiedLocalTime(String actual, Temporal expected) {
        LocalTime time = LocalTime.of(1, 45);
        CsvCellDataAccessObject dao = CsvCellDataAccessObject.builder()
                .defaultTime(time)
                .build();
        Instant expectedInstant = toInstant(expected, LocalDate.now(), time, ZoneId.systemDefault());
        RowAndIndex rowAndIndex = new RowAndIndex(new String[]{actual}, 0);

        assertEquals(expectedInstant, dao.getInstantValue(rowAndIndex));
    }

    @ParameterizedTest
    @MethodSource("getInstantExamples")
    void getInstantWithSpecifiedLocalDate(String actual, Temporal expected) {
        LocalDate date = LocalDate.of(2000, 2, 1);
        CsvCellDataAccessObject dao = CsvCellDataAccessObject.builder()
                .defaultDate(date)
                .build();
        Instant expectedInstant = toInstant(expected, date, NOON, ZoneId.systemDefault());
        RowAndIndex rowAndIndex = new RowAndIndex(new String[]{actual}, 0);

        assertEquals(expectedInstant, dao.getInstantValue(rowAndIndex));
    }

    static Object[][] getInstantExamples() {
        return DateTimeFormatParserTest.getInstantExamples();
    }

    @NonNull
    private static Instant toInstant(Temporal expected,
                                     LocalDate defaultDate,
                                     LocalTime defaultTime,
                                     ZoneId defaultZone) {
        if (expected instanceof LocalTime) {
            expected = LocalTime.from(expected).atDate(defaultDate);
        }
        if (expected instanceof LocalDate) {
            expected = LocalDate.from(expected).atTime(defaultTime);
        }
        if (expected instanceof LocalDateTime) {
            expected = LocalDateTime.from(expected).atZone(defaultZone);
        }
        return Instant.from(expected);
    }

    @Test
    void getInstantWithSpecifiedDateTimePatternOnlyDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM / yyyy");
        CsvCellDataAccessObject dao = CsvCellDataAccessObject.builder()
                .dateTimeFormatter(dtf)
                .build();
        String data = "01-02 / 2020";
        Instant expected = LocalDate.of(2020, 2, 1)
                .atTime(NOON)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        RowAndIndex rowAndIndex1 = new RowAndIndex(new String[]{data}, 0);

        assertEquals(expected, dao.getInstantValue(rowAndIndex1));

        RowAndIndex rowAndIndex2 = new RowAndIndex(new String[]{"01.02.2020"}, 0);
        assertThrows(DateTimeParseException.class, () -> dao.getInstantValue(rowAndIndex2));
    }

    @Test
    void getInstantWithSpecifiedDateTimePatternOnlyTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("+HH-mm.ss");
        CsvCellDataAccessObject dao = CsvCellDataAccessObject.builder()
                .dateTimeFormatter(dtf)
                .build();
        String data = "+01-30.45";
        Instant expected = LocalTime.of(1, 30, 45)
                .atDate(LocalDate.now())
                .atZone(ZoneId.systemDefault())
                .toInstant();
        RowAndIndex rowAndIndex1 = new RowAndIndex(new String[]{data}, 0);

        assertEquals(expected, dao.getInstantValue(rowAndIndex1));

        RowAndIndex rowAndIndex2 = new RowAndIndex(new String[]{"01.02.2020"}, 0);
        assertThrows(DateTimeParseException.class, () -> dao.getInstantValue(rowAndIndex2));
    }

    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(CsvCellDataAccessObject.class)
                .suppress(STRICT_INHERITANCE) // no subclass for test
                .verify();
    }

    @Test
    void testToString() {
        assertEquals("CsvCellDataAccessObject(dateTimeFormatter=null, defaultDate=null, defaultTime=12:00, defaultZoneId=Z)",
                CsvCellDataAccessObject.builder()
                        .defaultZoneId(UTC)
                        .build()
                        .toString());
    }
}