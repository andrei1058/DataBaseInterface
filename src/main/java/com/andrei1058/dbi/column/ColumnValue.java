package com.andrei1058.dbi.column;

public interface ColumnValue<K> {

    Column<K> getColumn();

    K getValue();
}
