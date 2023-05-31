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

import lombok.ToString;
import org.spacious_team.table_wrapper.api.AbstractTableFactory;
import org.spacious_team.table_wrapper.api.ReportPage;
import org.spacious_team.table_wrapper.api.Table;
import org.spacious_team.table_wrapper.api.TableCellRange;
import org.spacious_team.table_wrapper.api.TableHeaderColumn;

@SuppressWarnings("unused")
@ToString(callSuper = true)
public class CsvTableFactory extends AbstractTableFactory<CsvReportPage> {

    public CsvTableFactory() {
        super(CsvReportPage.class);
    }

    @Override
    public <T extends Enum<T> & TableHeaderColumn>
    Table create(ReportPage reportPage,
                 String tableName,
                 TableCellRange tableRange,
                 Class<T> headerDescription,
                 int headersRowCount) {
        return new CsvTable(
                cast(reportPage),
                tableName,
                tableRange,
                headerDescription,
                headersRowCount);
    }
}
