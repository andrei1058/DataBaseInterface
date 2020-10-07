package com.andrei1058.dbi.column;

public class IntegerColumn implements Column<Integer> {

    private final String name;
    private final int defaultValue;
    private final Integer size;

    public IntegerColumn(String name, int defaultValue, Integer size){
        this.name = name;
        this.defaultValue = defaultValue;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public DataType getSqlType() {
        return DataType.INT;
    }

    @Override
    public Object toExport() {
        return defaultValue;
    }

    @Override
    public Integer fromResult(Object o) {
        return (Integer) o;
    }

    public Integer getSize() {
        return size;
    }

    @Override
    public Integer getDefaultValue() {
        return defaultValue;
    }
}
