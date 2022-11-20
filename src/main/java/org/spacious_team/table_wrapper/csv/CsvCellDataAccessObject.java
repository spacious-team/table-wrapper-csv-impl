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
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spacious_team.table_wrapper.api.CellDataAccessObject;
import org.spacious_team.table_wrapper.csv.CsvTableCell.RowAndIndex;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public class CsvCellDataAccessObject implements CellDataAccessObject<RowAndIndex, CsvTableRow> {
    public static final CsvCellDataAccessObject INSTANCE = CsvCellDataAccessObject.of(
            InstantParser.builder().defaultTime(LocalTime.NOON).build());
    private final InstantParser instantParser;

    @Override
    public @Nullable RowAndIndex getCell(CsvTableRow row, Integer cellIndex) {
        @Nullable CsvTableCell cell = row.getCell(cellIndex);
        return (cell == null) ? null : cell.getRowAndIndex();
    }

    @Override
    public @Nullable String getValue(RowAndIndex cell) {
        return cell.getValue();
    }

    @Override
    public Instant getInstantValue(RowAndIndex cell) {
        @Nullable String value = getValue(cell);
        Objects.requireNonNull(value, "Not an instant");
        return instantParser.parseInstant(value);
    }
}
