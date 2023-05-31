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
import org.spacious_team.table_wrapper.api.TableCellAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class CsvTableHelperTest {

    @Test
    void find() {
        @SuppressWarnings("ConstantConditions")
        String[][] table = new String[][]{
                {"00", "01"},
                {"11", "12"},
                {null, "22"}
        };
        assertEquals(TableCellAddress.of(1, 0),
                CsvTableHelper.find(table, "11", 0, 3, 0, 2));
        assertEquals(TableCellAddress.of(1, 0),
                CsvTableHelper.find(table, "11",
                        Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE));
        assertEquals(TableCellAddress.of(1, 0),
                CsvTableHelper.find(table, 11, 0, 3, 0, 2));
        assertEquals(TableCellAddress.of(2, 0),
                CsvTableHelper.find(table, null, 0, 3, 0, 2));
        assertSame(TableCellAddress.NOT_FOUND,
                CsvTableHelper.find(table, "00", 1, 3, 0, 2));
        assertSame(TableCellAddress.NOT_FOUND,
                CsvTableHelper.find(table, "00", 0, 3, 1, 2));
        assertSame(TableCellAddress.NOT_FOUND,
                CsvTableHelper.find(table, "00", -1, 0, 1, 2));
        assertSame(TableCellAddress.NOT_FOUND,
                CsvTableHelper.find(table, "00", 0, 3, -1, 0));
    }
}