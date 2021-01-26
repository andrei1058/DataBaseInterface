package com.andrei1058.dbi;

import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.ColumnValue;
import com.andrei1058.dbi.operator.Operator;
import com.andrei1058.dbi.table.Table;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public interface DatabaseAdapter {
    <T> T select(Column<T> from, Table table, Operator<?> where);

    default <T> List<T> select(Column<T> from, Table table, Operator<?> where, int limit) {
        return select(from, table, where, 0, limit);
    }

    <T> List<T> select(Column<T> from, Table table, Operator<?> where, int start, int limit);

    HashMap<Column<?>, ?> selectRow(Table table, Operator<?> where);

    List<List<ColumnValue<?>>> selectRows(List<Column<?>> selectWhat, Table table, Operator<?> where);

    default List<List<ColumnValue<?>>> selectRows(List<Column<?>> selectWhat, Table table, Operator<?> where, int limit) {
        return selectRows(selectWhat, table, where, 0, limit);
    }

    List<List<ColumnValue<?>>> selectRows(List<Column<?>> selectWhat, Table table, Operator<?> where, int start, int limit);

    default void insert(Table table, List<ColumnValue<?>> values) {
        insert(table, values, null);
    }

    void insert(Table table, List<ColumnValue<?>> values, @Nullable InsertFallback onFail);

    void createTable(Table table, boolean drop);

    void set(Table table, Column<?> column, ColumnValue<?> value, Operator<?> where);

    void set(Table table, HashMap<Column<?>, ColumnValue<?>> values, Operator<?> where);

    void disable();

    enum InsertFallback {
        /**
         * Replace existing data on primary key violation/ duplication.
         * NOTE: SQLite equivalent is REPLACE.
         */
        UPDATE,

        /**
         * Insert only valid data and ignore invalid values without throwing an error.
         */
        IGNORE,

    }
}
