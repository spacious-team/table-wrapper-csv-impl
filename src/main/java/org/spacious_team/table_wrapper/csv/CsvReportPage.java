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

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spacious_team.table_wrapper.api.AbstractReportPage;
import org.spacious_team.table_wrapper.api.TableCellAddress;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Predicate;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CsvReportPage extends AbstractReportPage<CsvTableRow> {

    private final String[][] rows;

    /**
     * Field and line delimiter detected automatically. UTF-8 encoded file expected.
     */
    public CsvReportPage(Path path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
            this.rows = readRows(inputStream, UTF_8, getDefaultCsvParserSettings());
        }
    }

    /**
     * Field and line delimiter detected automatically. UTF-8 encoded file expected.
     *
     * @implSpec Does not close inputStream
     */
    public CsvReportPage(InputStream inputStream) throws IOException {
        this(inputStream, UTF_8, getDefaultCsvParserSettings());
    }

    /**
     * @implSpec Does not close inputStream
     */
    public CsvReportPage(InputStream inputStream, Charset charset, CsvParserSettings csvParserSettings) throws IOException {
        CloseIgnoringInputStream closeIgnoringInputStream = new CloseIgnoringInputStream(inputStream);
        this.rows = readRows(closeIgnoringInputStream, charset, csvParserSettings);
    }

    /**
     * @implSpec Closes inputStream
     */
    private static String[] @NonNull [] readRows(InputStream inputStream,
                                                 Charset charset,
                                                 CsvParserSettings csvParserSettings) throws IOException {
        try (Reader inputReader = new InputStreamReader(inputStream, charset)) {
            CsvParser parser = new CsvParser(csvParserSettings);
            return parser.parseAll(inputReader)
                    .toArray(new String[0][]);
        }
    }

    public CsvReportPage(String[][] cells) {
        this.rows = cells;
    }

    public static CsvParserSettings getDefaultCsvParserSettings() {
        CsvParserSettings settings = new CsvParserSettings();
        settings.detectFormatAutomatically();
        return settings;
    }

    @Override
    public TableCellAddress find(Object value, int startRow, int endRow, int startColumn, int endColumn) {
        return CsvTableHelper.find(rows, value, startRow, endRow, startColumn, endColumn);
    }

    @Override
    public TableCellAddress find(int startRow, int endRow, int startColumn, int endColumn,
                                 Predicate<@Nullable Object> cellValuePredicate) {
        return CsvTableHelper.find(rows, startRow, endRow, startColumn, endColumn, cellValuePredicate::test);
    }

    @Override
    public @Nullable CsvTableRow getRow(int i) {
        return (i < 0 || i >= rows.length) ? null : CsvTableRow.of(rows[i], i);
    }

    @Override
    public int getLastRowNum() {
        return rows.length - 1;
    }
}
