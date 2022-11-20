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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spacious_team.table_wrapper.csv.CsvTableCell.RowAndIndex;

import static java.time.ZoneOffset.UTC;
import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvCellDataAccessObjectTest {

    CsvCellDataAccessObject dao;
    InstantParser instantParser;
    @Mock
    CsvTableRow row;
    @Mock
    CsvTableCell cell;

    @BeforeEach
    void setUp() {
        instantParser = mock(InstantParser.class);
        dao = spy(CsvCellDataAccessObject.of(instantParser));
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
    void getInstant() {
        String actual = "test";
        RowAndIndex rowAndIndex = new RowAndIndex(new String[]{actual}, 0);

        dao.getInstantValue(rowAndIndex);

        verify(dao).getValue(rowAndIndex);
        verify(instantParser).parseInstant(actual);
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
        InstantParser instantParser = InstantParser.builder().defaultZoneId(UTC).build();
        assertEquals("CsvCellDataAccessObject(instantParser=InstantParser(dateTimeFormatter=null, defaultDate=null, defaultTime=00:00, defaultZoneId=Z))",
                CsvCellDataAccessObject.of(instantParser).toString());
    }
}