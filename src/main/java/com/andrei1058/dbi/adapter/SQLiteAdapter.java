package com.andrei1058.dbi.adapter;

import com.andrei1058.dbi.DatabaseAdapter;
import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.ColumnValue;
import com.andrei1058.dbi.operator.Operator;
import com.andrei1058.dbi.table.Table;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

public class SQLiteAdapter implements DatabaseAdapter {

    private final Connection connection;

    public SQLiteAdapter(String url) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + url);

    }

    public <T> T select(Column<T> what, Table from, Operator<?> where) {
        String query = "SELECT " + what.getName() + " FROM " + from.getName() + " WHERE " + where.toQuery() + ";";
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            if (rs.next()) {
                Object result = rs.getObject(what.getName());
                return result == null ? what.getDefaultValue() : what.fromResult(result);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return what.getDefaultValue();
    }

    public <T> List<T> select(Column<T> from, Table table, Operator<?> where, int limit) {
        throw new IllegalStateException("Not implemented yet!");
    }

    @Override
    public <T> List<T> select(Column<T> from, Table table, Operator<?> where, int start, int limit) {
        throw new IllegalStateException("Not implemented yet!");
    }

    @Override
    public List<List<?>> selectAll(Column<?> from, Table table, Operator<?> where) {
        throw new IllegalStateException("Not implemented yet!");
    }

    @Override
    public List<List<?>> selectAll(Column<?> from, Table table, Operator<?> where, int start, int limit) {
        throw new IllegalStateException("Not implemented yet!");
    }

    @Override
    public void insert(Table table, List<ColumnValue<?>> values, InsertFallback onFail) {
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
        try (PreparedStatement ps = connection.prepareStatement(firstPart.append(secondPart).toString())) {
            for (int i = 0; i < values.size(); i++) {
                ColumnValue<?> value = values.get(i);
                ps.setObject(i + 1, value.getValue());
            }
            ps.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void createTable(Table table, boolean drop) {
        try (Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);
            if (drop) {
                statement.executeUpdate("drop table if exists " + table.getName() + ";");
            }
            if (table.getColumns().isEmpty()) return;
            Column<?> pk = table.getPrimaryKey();
            StringBuilder sql = new StringBuilder("create table " + table.getName() + " (" + pk.getName() + " " + pk.getSqlType().getSqlite()
                    + " PRIMARY KEY" + (table.isAutoIncrementPK() ? " AUTOINCREMENT" : "") + ",");
            for (int i = 0; i < table.getColumns().size(); i++) {
                Column<?> c = table.getColumns().get(i);
                sql.append(" ").append(c.getName()).append(" ").append(c.getSqlType().getSqlite())
                        .append(c.getSize() > 0 ? "(" + c.getSize() + ")" : "").append(" DEFAULT ").append(c.toExport(c.getDefaultValue()));
                if (i < table.getColumns().size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(");");
            System.out.println(sql.toString());
            statement.executeUpdate(sql.toString());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void set(Table table, Column<?> column, ColumnValue<?> value, Operator<?> where) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + table.getName() + "` SET `" + column.getName() + "`=? WHERE " + where.toQuery())) {
            statement.setObject(1, value.getColumn().toExport(value.getValue()));
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void set(Table table, HashMap<Column<?>, ColumnValue<?>> values, Operator<?> where) {
        throw new IllegalStateException("Not implemented yet!");
    }
}
