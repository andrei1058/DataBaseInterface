package com.andrei1058.dbi.insert;

import com.andrei1058.dbi.column.Column;

public interface IColumnValue<K> {

    Column<K> getColumn();

    K getValue();
}
