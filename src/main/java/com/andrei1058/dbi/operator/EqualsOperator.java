package com.andrei1058.dbi.operator;

import com.andrei1058.dbi.column.Column;

public class EqualsOperator<T> implements Operator<T> {

    private final Column<T> a;
    private final T b;

    public EqualsOperator(Column<T> a, T b) {
        this.a = a;
        this.b = b;
    }

    public String toQuery() {
        return a.getName() + "='" + a.toExport(b)+"'";
    }
}
