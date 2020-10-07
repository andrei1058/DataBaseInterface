package com.andrei1058.dbi.column;

import java.util.UUID;

public class UUIDColumn implements Column<UUID> {

    private final String name;
    private final UUID defaultValue;

    public UUIDColumn(String name, UUID defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public UUID getDefaultValue() {
        return defaultValue;
    }

    public String getName() {
        return name;
    }

    public DataType getSqlType() {
        return DataType.STRING;
    }

    @Override
    public Object toExport() {
        return defaultValue.toString();
    }

    @Override
    public UUID fromResult(Object o) {
        return UUID.fromString((String) o);
    }

    public Integer getSize() {
        return null;
    }
}
