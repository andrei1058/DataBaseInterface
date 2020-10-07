package com.andrei1058.dbi.operator;

import com.andrei1058.dbi.column.Column;

public class GreaterOperator implements Operator<Integer> {

    private final Column<Integer> a;
    private final int b;

    public GreaterOperator(Column<Integer> a, int b) {
        this.a = a;
        this.b = b;
    }

    public String toQuery() {
        return a.getName() + ">" + b;
    }
}
