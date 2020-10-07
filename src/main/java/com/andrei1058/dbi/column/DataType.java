package com.andrei1058.dbi.column;

public enum DataType {

    INT("INT", "INTEGER"),
    //todo not sure sqlite
    STRING("VARCHAR", "VAR");

    private final String mysql;
    private final String sqlite;

    DataType(String mysql, String sqlite) {
        this.mysql = mysql;
        this.sqlite = sqlite;
    }

    public String getMysql() {
        return mysql;
    }

    public String getSqlite() {
        return sqlite;
    }
}
