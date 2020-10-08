package com.andrei1058.dbi.column.type;

import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.SqlColumnType;

import java.sql.Timestamp;

public class TimestampColumn implements Column<Timestamp> {

    private final String name;
    private final Timestamp defaultValue;

    public TimestampColumn(String name, Timestamp defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getSize() {
        return 0;
    }

    @Override
    public Timestamp getDefaultValue() {
        return defaultValue;
    }

    @Override
    public SqlColumnType getSqlType() {
        return SqlColumnType.TIMESTAMP;
    }

    @Override
    public Object toExport(Object value) {
        return value == null ? null : value instanceof Timestamp ? value : null;
    }

    @Override
    public Timestamp fromResult(Object o) {
        return (Timestamp) o;
    }
}
