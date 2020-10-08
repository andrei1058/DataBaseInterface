package com.andrei1058.dbi.column.type;

import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.SqlColumnType;

import java.util.Date;

public class DateColumn implements Column<Date> {

    private final String name;
    private final Date defaultValue;

    public DateColumn(String name, Date defaultValue){
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
    public Date getDefaultValue() {
        return defaultValue;
    }

    @Override
    public SqlColumnType getSqlType() {
        return SqlColumnType.DATE;
    }

    @Override
    public Object toExport(Object value) {
        return value == null ? null : value instanceof Date ? value : value;
    }

    @Override
    public Date fromResult(Object o) {
        return (Date) o;
    }
}
