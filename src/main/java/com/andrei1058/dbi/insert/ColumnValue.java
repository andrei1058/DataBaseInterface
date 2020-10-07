package com.andrei1058.dbi.insert;

import com.andrei1058.dbi.column.Column;

public class ColumnValue<T> implements IColumnValue<T> {

    private final Column<T> a;
    private final T b;

    public ColumnValue(Column<T> a, T b) {
        this.a = a;
        this.b = b;
    }

    public Column<T> getColumn() {
        return a;
    }

    public T getValue() {
        return b;
    }
}
