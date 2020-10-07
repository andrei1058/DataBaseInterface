package com.andrei1058.dbi.column;

public interface Column<T> {

    String getName();

    Integer getSize();

    T getDefaultValue();

    DataType getSqlType();

    Object toExport();

    T fromResult(Object o);
}
