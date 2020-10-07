package com.andrei1058.dbi.column.datavalue;

import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.ColumnValue;

public class SimpleValue<T> implements ColumnValue<T> {

    private final Column<T> a;
    private final T b;

    public SimpleValue(Column<T> a, T b) {
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
