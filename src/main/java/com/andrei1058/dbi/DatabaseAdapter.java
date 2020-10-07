package com.andrei1058.dbi;

import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.ColumnValue;
import com.andrei1058.dbi.operator.Operator;
import com.andrei1058.dbi.table.Table;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DatabaseAdapter {
    <T> T select(Column<T> from, Table table, Operator<?> where);

    default <T> List<T> select(Column<T> from, Table table, Operator<?> where, int limit) {
        return select(from, table, where, 0, limit);
    }

    <T> List<T> select(Column<T> from, Table table, Operator<?> where, int start, int limit);

    List<List<?>> selectAll(Column<?> from, Table table, Operator<?> where);

    default List<List<?>> selectAll(Column<?> from, Table table, Operator<?> where, int limit) {
        return selectAll(from, table, where, 0, limit);
    }

    List<List<?>> selectAll(Column<?> from, Table table, Operator<?> where, int start, int limit);

    default void insert(Table table, List<ColumnValue<?>> values) {
        insert(table, values, null);
    }

    void insert(Table table, List<ColumnValue<?>> values, @Nullable InsertFallback onFail);

    void createTable(Table table, boolean drop);

    /**
     * What to do on duplicate primary key?
     */
    enum InsertFallback {
        UPDATE,

    }
}
