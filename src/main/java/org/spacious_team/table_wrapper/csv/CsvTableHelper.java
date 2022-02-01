/*
 * Table Wrapper Xml SpreadsheetML Impl
 * Copyright (C) 2020  Vitalii Ananev <spacious-team@ya.ru>
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

import org.spacious_team.table_wrapper.api.TableCellAddress;

import java.util.function.BiPredicate;

import static org.spacious_team.table_wrapper.api.TableCellAddress.NOT_FOUND;

class CsvTableHelper {

    static TableCellAddress find(String[][] table, Object value, int startRow, int endRow, int startColumn, int endColumn,
                                 BiPredicate<String, Object> stringPredicate) {
        startRow = Math.max(0, startRow);
        endRow = Math.min(endRow, table.length);
        for(int rowNum = startRow; rowNum < endRow; rowNum++) {
            String[] row = table[rowNum];
            TableCellAddress address = find(row, rowNum, value, startColumn, endColumn, stringPredicate);
            if (address != NOT_FOUND) {
                return address;
            }
        }
        return NOT_FOUND;
    }

    static TableCellAddress find(String[] row, int rowNum, Object value, int startColumn, int endColumn,
                                 BiPredicate<String, Object> stringPredicate) {
        startColumn = Math.max(0, startColumn);
        endColumn = Math.min(endColumn, row.length);
        for (int i = startColumn; i < endColumn; i++) {
                String cell = row[i];
                if (cell != null) {
                    if (stringPredicate.test(cell, value)) {
                        return new TableCellAddress(rowNum, i);
                    }
                }
            }
        return NOT_FOUND;
    }
}
