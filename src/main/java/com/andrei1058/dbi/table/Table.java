package com.andrei1058.dbi.table;

import com.andrei1058.dbi.column.Column;

import java.util.LinkedList;

public interface Table {

    String getName();

    /**
     * Primary key is not included.
     */

    Column<?> getPrimaryKey();

    LinkedList<Column<?>> getColumns();

    boolean isAutoIncrementPK();
}
