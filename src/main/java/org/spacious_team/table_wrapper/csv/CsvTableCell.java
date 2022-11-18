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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spacious_team.table_wrapper.api.AbstractTableCell;

import java.util.Objects;

import static lombok.AccessLevel.PACKAGE;

@EqualsAndHashCode(of = {"rowAndIndex"}, callSuper = false)
public class CsvTableCell extends AbstractTableCell<CsvTableCell.RowAndIndex> {

    @Getter(PACKAGE)
    private final RowAndIndex rowAndIndex;

    public static CsvTableCell of(String[] row, int columnIndex) {
        return new CsvTableCell(new RowAndIndex(row, columnIndex));
    }

    public static CsvTableCell of(String[] row, int columnIndex, CsvCellDataAccessObject dao) {
        return new CsvTableCell(new RowAndIndex(row, columnIndex), dao);
    }

    public CsvTableCell(RowAndIndex rowAndIndex) {
        this(rowAndIndex, CsvCellDataAccessObject.INSTANCE);
    }

    public CsvTableCell(RowAndIndex rowAndIndex, CsvCellDataAccessObject dao) {
        super(rowAndIndex, dao);
        this.rowAndIndex = rowAndIndex;
    }

    @Override
    public int getColumnIndex() {
        return rowAndIndex.getColumnIndex();
    }

    @RequiredArgsConstructor
    static final class RowAndIndex {
        private final String[] row;
        @Getter
        private final int columnIndex;

        @Nullable
        String getValue() {
            return checkIndex() ? row[columnIndex] : null;
        }

        private boolean checkIndex() {
            return columnIndex >= 0 && columnIndex < row.length;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            } else if (!(obj instanceof RowAndIndex)) {
                return false;
            }
            RowAndIndex other = (RowAndIndex) obj;
            return checkIndex() &&
                    other.checkIndex() &&
                    Objects.equals(getValue(), other.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getValue());
        }
    }
}
