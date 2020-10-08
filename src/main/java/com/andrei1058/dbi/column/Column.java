package com.andrei1058.dbi.column;

public interface Column<T> {

    String getName();

    Integer getSize();

    T getDefaultValue();

    SqlColumnType getSqlType();

    Object toExport(Object value);

    T castResult(Object o);
}
