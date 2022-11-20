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
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;

import static java.time.LocalTime.MIDNIGHT;
import static java.time.ZoneOffset.UTC;
import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class InstantParserTest {

    InstantParser parser;

    @BeforeEach
    void setUp() {
        parser = spy(InstantParser.INSTANCE);
    }

    @Test
    void parseInstantThrowable() {
        assertThrows(DateTimeParseException.class, () -> parser.parseInstant("abc"));
        assertThrows(DateTimeParseException.class, () -> parser.parseInstant("2020.02-01"));
    }

    @ParameterizedTest
    @MethodSource("getInstantExamples")
    void parseInstant(String actual, Temporal expected) {
        Instant expectedInstant = toInstant(expected, LocalDate.now(), MIDNIGHT, ZoneId.systemDefault());
        assertEquals(expectedInstant, parser.parseInstant(actual));
    }

    @ParameterizedTest
    @MethodSource("getInstantExamples")
    void getInstantWithSpecifiedZone(String actual, Temporal expected) {
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        InstantParser parser = InstantParser.builder()
                .defaultZoneId(zoneId)
                .build();
        Instant expectedInstant = toInstant(expected, LocalDate.now(), MIDNIGHT, zoneId);

        assertEquals(expectedInstant, parser.parseInstant(actual));
    }

    @ParameterizedTest
    @MethodSource("getInstantExamples")
    void getInstantWithSpecifiedLocalTime(String actual, Temporal expected) {
        LocalTime time = LocalTime.of(1, 45);
        InstantParser parser = InstantParser.builder()
                .defaultTime(time)
                .build();
        Instant expectedInstant = toInstant(expected, LocalDate.now(), time, ZoneId.systemDefault());

        assertEquals(expectedInstant, parser.parseInstant(actual));
    }

    @ParameterizedTest
    @MethodSource("getInstantExamples")
    void getInstantWithSpecifiedLocalDate(String actual, Temporal expected) {
        LocalDate date = LocalDate.of(2000, 2, 1);
        InstantParser parser = InstantParser.builder()
                .defaultDate(date)
                .build();
        Instant expectedInstant = toInstant(expected, date, MIDNIGHT, ZoneId.systemDefault());

        assertEquals(expectedInstant, parser.parseInstant(actual));
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
        InstantParser parser = InstantParser.builder()
                .dateTimeFormatter(dtf)
                .build();
        String data = "01-02 / 2020";
        Instant expected = LocalDate.of(2020, 2, 1)
                .atTime(MIDNIGHT)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        assertEquals(expected, parser.parseInstant(data));
        assertThrows(DateTimeParseException.class, () -> parser.parseInstant("01.02.2020"));
    }

    @Test
    void getInstantWithSpecifiedDateTimePatternOnlyTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("+HH-mm.ss");
        InstantParser parser = InstantParser.builder()
                .dateTimeFormatter(dtf)
                .build();
        String data = "+01-30.45";
        Instant expected = LocalTime.of(1, 30, 45)
                .atDate(LocalDate.now())
                .atZone(ZoneId.systemDefault())
                .toInstant();

        assertEquals(expected, parser.parseInstant(data));
        assertThrows(DateTimeParseException.class, () -> parser.parseInstant("01.02.2020"));
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
        assertEquals("InstantParser(dateTimeFormatter=null, defaultDate=null, defaultTime=00:00, defaultZoneId=Z)",
                InstantParser.builder()
                        .defaultZoneId(UTC)
                        .build()
                        .toString());
    }
}