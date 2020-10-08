package com.andrei1058.dbi.column;

public enum SqlColumnType {

    INT("INT", "INTEGER"),
    STRING("VARCHAR", "VARCHAR"),
    DATE("DATE", "DATE");

    private final String mysql;
    private final String sqlite;

    SqlColumnType(String mysql, String sqlite) {
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
