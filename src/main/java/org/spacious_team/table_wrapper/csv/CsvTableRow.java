package org.spacious_team.table_wrapper.csv;

import com.univocity.parsers.common.record.Record;
import lombok.Getter;
import org.spacious_team.table_wrapper.api.AbstractReportPageRow;
import org.spacious_team.table_wrapper.api.TableCell;
import org.spacious_team.table_wrapper.api.TableCellAddress;

import java.util.Iterator;

import static org.spacious_team.table_wrapper.api.TableCellAddress.NOT_FOUND;
import static org.spacious_team.table_wrapper.csv.CsvTableHelper.equalsPredicate;

public class CsvTableRow extends AbstractReportPageRow {

    private final String[] row;
    @Getter
    private final int rowNum;
    private final CsvTableCell[] cellsCache;

    public CsvTableRow(String[] row, int rowNum) {
        this.row = row;
        this.rowNum = rowNum;
        this.cellsCache = new CsvTableCell[row.length];
    }

    @Override
    public CsvTableCell getCell(int i) {
        if (i >= row.length) {
            return null;
        }
        CsvTableCell cell = cellsCache[i];
        if (cell == null) {
            cell = CsvTableCell.of(row, i);
            cellsCache[i] = cell;
        }
        return cell;
    }

    @Override
    public int getFirstCellNum() {
        return (row.length > 0) ? 0 : -1;
    }

    @Override
    public int getLastCellNum() {
        return row.length - 1;
    }

    @Override
    public boolean rowContains(Object value) {
        return CsvTableHelper.find(row, rowNum, 0, row.length, equalsPredicate(value)) != NOT_FOUND;
    }

    @Override
    public Iterator<TableCell> iterator() {
        return new Iterator<>() {
            private int cellIndex = 0;

            @Override
            public boolean hasNext() {
                return cellIndex < row.length;
            }

            @Override
            public TableCell next() {
                return getCell(cellIndex++);
            }
        };
    }
}
