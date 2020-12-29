package com.andrei1058.dbi.adapter;

import com.andrei1058.dbi.DatabaseAdapter;
import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.ColumnValue;
import com.andrei1058.dbi.operator.Operator;
import com.andrei1058.dbi.table.Table;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
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
        hikariConfig.setMaxLifetime(maxLifeTime * 1000L);

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
        String query = "SELECT " + from.getName() + " FROM " + table.getName() + " WHERE " + where.toQuery() + ";";
        try (Connection connection = dataSource.getConnection()) {
            try (ResultSet rs = connection.createStatement().executeQuery(query)) {
                if (rs.next()) {
                    Object result = rs.getObject(from.getName());
                    return result == null ? from.getDefaultValue() : from.castResult(result);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return from.getDefaultValue();
    }

    @Override
    public <T> List<T> select(Column<T> from, Table table, Operator<?> where, int start, int limit) {
        throw new IllegalStateException("Not supported yet");
    }

    @Override
    public HashMap<Column<?>, ?> selectRow(Table table, Operator<?> where) {
        HashMap<Column<?>, Object> results = new HashMap<>();
        String query = "SELECT * FROM " + table.getName() + " WHERE " + where.toQuery() + ";";
        try (Connection connection = dataSource.getConnection()) {
            try (ResultSet rs = connection.createStatement().executeQuery(query)) {
                if (rs.next()) {
                    Object result = rs.getObject(table.getPrimaryKey().getName());
                    results.put(table.getPrimaryKey(), (result == null ? table.getPrimaryKey().getDefaultValue() : table.getPrimaryKey().castResult(result)));
                    for (Column<?> column : table.getColumns()) {
                        result = rs.getObject(column.getName());
                        results.put(column, (result == null ? column.getDefaultValue() : column.castResult(result)));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    @Override
    public List<List<ColumnValue<?>>> selectRows(Column<?> from, Table table, Operator<?> where, int start, int limit) {
        throw new IllegalStateException("Not supported yet");
    }

    @Override
    public void insert(Table table, List<ColumnValue<?>> values, @Nullable InsertFallback onFail) {
        if (table.getColumns().isEmpty()) {
            //todo throw empty table exception
            return;
        }
        StringBuilder firstPart = new StringBuilder("INSERT INTO " + table.getName() + "(");
        StringBuilder secondPart = new StringBuilder("VALUES(");
        for (int i = 0; i < values.size(); i++) {
            ColumnValue<?> value = values.get(i);
            firstPart.append(value.getColumn().getName());
            secondPart.append("?");
            if (i < values.size() - 1) {
                firstPart.append(",");
                secondPart.append(",");
            } else {
                firstPart.append(") ");
                secondPart.append(");");
            }
        }
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(firstPart.append(secondPart).toString())) {
                for (int i = 0; i < values.size(); i++) {
                    ColumnValue<?> value = values.get(i);
                    ps.setObject(i + 1, value.getValue());
                }
                ps.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void createTable(Table table, boolean drop) {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(30);
                if (drop) {
                    statement.executeUpdate("drop table if exists " + table.getName() + ";");
                }
                if (table.getColumns().isEmpty()) return;
                Column<?> pk = table.getPrimaryKey();
                StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table.getName() + " (" + pk.getName() + " " + pk.getSqlType().getSqlite()
                        + (pk.getSize() > 0 ? "(" + pk.getSize() + ")" : "") + " PRIMARY KEY" + (table.isAutoIncrementPK() ? " AUTO_INCREMENT" : "") + ",");
                for (int i = 0; i < table.getColumns().size(); i++) {
                    Column<?> c = table.getColumns().get(i);
                    sql.append(" ").append(c.getName()).append(" ").append(c.getSqlType().getSqlite())
                            .append(c.getSize() > 0 ? "(" + c.getSize() + ") " : " ");
                    // do not append ' if null
                    if (c.toExport(c.getDefaultValue()) == null) {
                        sql.append("DEFAULT NULL");
                    } else {
                        sql.append("DEFAULT '").append(c.toExport(c.getDefaultValue())).append("'");
                    }
                    if (i < table.getColumns().size() - 1) {
                        sql.append(",");
                    }
                }
                sql.append(");");
                statement.execute(sql.toString());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void set(Table table, Column<?> column, ColumnValue<?> value, Operator<?> where) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + table.getName() + "` SET `" + column.getName() + "`=? WHERE " + where.toQuery())) {
                statement.setObject(1, value.getColumn().toExport(value.getValue()));
                statement.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void set(Table table, HashMap<Column<?>, ColumnValue<?>> values, Operator<?> where) {
        try (Connection connection = dataSource.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE `" + table.getName() + "` SET ");
            for (Column<?> column : values.keySet()) {
                sql.append("`").append(column.getName()).append("`=?,");
            }
            // remove last comma from sql
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" WHERE ").append(where.toQuery()).append(";");

            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                int index = 0;
                for (ColumnValue<?> entry : values.values()) {
                    statement.setObject(++index, entry.getColumn().toExport(entry.getValue()));
                }
                statement.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void disable() {
        if (connection == null) return;
        try {
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        dataSource.close();
    }

    public Connection getConnection() {
        return connection;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
