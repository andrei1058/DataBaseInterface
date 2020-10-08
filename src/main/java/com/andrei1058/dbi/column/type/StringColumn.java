package com.andrei1058.dbi.column.type;

import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.SqlColumnType;

public class StringColumn implements Column<String> {

    private final String name;
    private final int size;
    private final String defaultValue;

    public StringColumn(String name, int size, String defaultValue){
        this.name = name;
        this.size = size;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public SqlColumnType getSqlType() {
        return SqlColumnType.STRING;
    }

    @Override
    public Object toExport(Object value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public String castResult(Object o) {
        return o.toString();
    }
}
