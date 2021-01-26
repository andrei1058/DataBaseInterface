package com.andrei1058.dbi.operator;

import com.andrei1058.dbi.column.ColumnValue;

@SuppressWarnings("unused")
public class MultiEqualsOperator implements Operator<Void> {

    private final ColumnValue<?>[] columnValues;

    public MultiEqualsOperator(ColumnValue<?>... checks) {
        columnValues = checks;
    }

    @Override
    public String toQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ColumnValue<?> cv : columnValues) {
            stringBuilder.append(cv.getColumn().getName()).append("='")
                    .append(cv.getColumn().toExport(cv.getValue())).append("'").append(",");
        }
        if (stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
}
