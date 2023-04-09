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

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spacious_team.table_wrapper.api.TableCellAddress;

import java.util.Objects;
import java.util.function.Predicate;

import static lombok.AccessLevel.PRIVATE;
import static org.spacious_team.table_wrapper.api.TableCellAddress.NOT_FOUND;

@NoArgsConstructor(access = PRIVATE)
final class CsvTableHelper {

    static TableCellAddress find(String[][] table, @Nullable Object expected,
                                 int startRow, int endRow,
                                 int startColumn, int endColumn) {
        return find(table, startRow, endRow, startColumn, endColumn, equalsPredicate(expected));
    }

    static TableCellAddress find(String[][] table, int startRow, int endRow, int startColumn, int endColumn,
                                 Predicate<@Nullable String> predicate) {
        startRow = Math.max(0, startRow);
        endRow = Math.min(endRow, table.length);
        for (int rowNum = startRow; rowNum < endRow; rowNum++) {
            String[] row = table[rowNum];
            TableCellAddress address = find(row, rowNum, startColumn, endColumn, predicate);
            if (address != NOT_FOUND) {
                return address;
            }
        }
        return NOT_FOUND;
    }

    static TableCellAddress find(String[] row, int rowNum, int startColumn, int endColumn,
                                 Predicate<@Nullable String> predicate) {
        startColumn = Math.max(0, startColumn);
        endColumn = Math.min(endColumn, row.length);
        for (int i = startColumn; i < endColumn; i++) {
            String cell = row[i];
            if (predicate.test(cell)) {
                return TableCellAddress.of(rowNum, i);
            }
        }
        return NOT_FOUND;
    }

    static Predicate<@Nullable String> equalsPredicate(@Nullable Object expected) {
        if (expected == null) {
            return Objects::isNull;
        }
        String expectedString = expected.toString();
        return expectedString::equals;
    }
}
