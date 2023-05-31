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
import org.spacious_team.table_wrapper.api.InstantParser;

import java.time.Instant;
import java.time.LocalTime;

import static java.util.Objects.requireNonNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public class CsvCellDataAccessObject implements CellDataAccessObject<String, CsvTableRow> {
    public static final CsvCellDataAccessObject INSTANCE = CsvCellDataAccessObject.of(
            InstantParser.builder().defaultTime(LocalTime.NOON).build());
    private final InstantParser instantParser;

    @Override
    public @Nullable String getCell(CsvTableRow row, Integer cellIndex) {
        //noinspection ConstantConditions
        return (cellIndex == null) ? null : row.getCellValue(cellIndex);
    }

    @Override
    public @Nullable String getValue(@Nullable String cell) {
        return cell;
    }

    @Override
    public Instant getInstantValue(@Nullable String cell) {
        @Nullable String value = getValue(cell);
        @SuppressWarnings("nullness")
        String nonNullValue = requireNonNull(value, "Not an instant");
        return instantParser.parseInstant(nonNullValue);
    }
}
