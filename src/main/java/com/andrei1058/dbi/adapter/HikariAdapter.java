package com.andrei1058.dbi.adapter;

import com.andrei1058.dbi.DatabaseAdapter;
import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.ColumnValue;
import com.andrei1058.dbi.operator.Operator;
import com.andrei1058.dbi.table.Table;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HikariAdapter implements DatabaseAdapter {

    private final HikariDataSource dataSource;
    private final Connection connection;

    /**
     * @param maxLifeTime max lifetime in seconds.
     */
    public HikariAdapter(String poolName, int poolSize, int maxLifeTime, String host, int port, String dbName, String username, String password, boolean verifyCertificate, boolean useSSL) throws SQLException {
        // connect
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setPoolName(poolName);

        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifeTime * 1000);

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + dbName);

        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.addDataSourceProperty("useSSL", useSSL);
        if (!verifyCertificate) {
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        }

        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");

        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Recover if connection gets interrupted
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));

        dataSource = new HikariDataSource(hikariConfig);

        connection = dataSource.getConnection();
    }

    @Override
    public <T> T select(Column<T> from, Table table, Operator<?> where) {
        throw new IllegalStateException("Not supported yet");
    }

    @Override
    public <T> List<T> select(Column<T> from, Table table, Operator<?> where, int start, int limit) {
        throw new IllegalStateException("Not supported yet");
    }

    @Override
    public List<List<?>> selectColumn(Column<?> from, Table table, Operator<?> where) {
        throw new IllegalStateException("Not supported yet");
    }

    @Override
    public List<List<?>> selectColumn(Column<?> from, Table table, Operator<?> where, int start, int limit) {
        throw new IllegalStateException("Not supported yet");
    }

    @Override
    public void insert(Table table, List<ColumnValue<?>> values, @Nullable InsertFallback onFail) {
        throw new IllegalStateException("Not supported yet");
    }

    @Override
    public void createTable(Table table, boolean drop) {
        throw new IllegalStateException("Not supported yet");
    }

    @Override
    public void set(Table table, Column<?> column, ColumnValue<?> value, Operator<?> where) {
        throw new IllegalStateException("Not supported yet");
    }

    @Override
    public void set(Table table, HashMap<Column<?>, ColumnValue<?>> values, Operator<?> where) {
        throw new IllegalStateException("Not supported yet");
    }

    public Connection getConnection() {
        return connection;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
