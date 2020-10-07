package com.andrei1058.dbi.table;

import com.andrei1058.dbi.column.Column;

import java.util.LinkedList;
import java.util.List;

public class TableBuilder {

    private final String tableName;
    private final Column<?> primaryKey;
    private boolean autoIncrement = false;
    private final LinkedList<Column<?>> columns = new LinkedList<>();

    public TableBuilder(String tableName, Column<?> primaryKey){
        this.tableName = tableName;
        this.primaryKey = primaryKey;
    }

    public TableBuilder autoIncrementPK(){
        if (primaryKey.getClass().isInstance(Integer.class)){
            autoIncrement = true;
        }
        return this;
    }

    public TableBuilder withColumn(Column<?> column){
        if (!column.equals(primaryKey)){
            columns.add(column);
        }
        return this;
    }

    public TableBuilder withColumns(List<Column<?>> columns){
        columns.forEach(column -> {
            if (!(column.equals(primaryKey) && this.columns.contains(column))){
                this.columns.add(column);
            }
        });
        return this;
    }

    public Table build(){
        return new Table() {
            @Override
            public String getName() {
                return tableName;
            }

            @Override
            public Column<?> getPrimaryKey() {
                return primaryKey;
            }

            @Override
            public LinkedList<Column<?>> getColumns() {
                return columns;
            }

            @Override
            public boolean isAutoIncrementPK() {
                return autoIncrement;
            }
        };
    }
}
