package com.andrei1058.dbi.adapter;

import com.andrei1058.dbi.DatabaseAdapter;
import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.ColumnValue;
import com.andrei1058.dbi.column.datavalue.SimpleValue;
import com.andrei1058.dbi.operator.Operator;
import com.andrei1058.dbi.table.Table;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
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
                return result == null ? what.getDefaultValue() : what.castResult(result);
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
    public HashMap<Column<?>, ?> selectRow(Table table, Operator<?> where) {
        HashMap<Column<?>, Object> results = new HashMap<>();
        String query = "SELECT * FROM " + table.getName() + " WHERE " + where.toQuery() + ";";
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            if (rs.next()) {
                Object result = rs.getObject(table.getPrimaryKey().getName());
                results.put(table.getPrimaryKey(), (result == null ? table.getPrimaryKey().getDefaultValue() : table.getPrimaryKey().castResult(result)));
                for (Column<?> column : table.getColumns()) {
                    result = rs.getObject(column.getName());
                    results.put(column, (result == null ? column.getDefaultValue() : column.castResult(result)));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    @Override
    public List<List<ColumnValue<?>>> selectRows(List<Column<?>> selectWhat, Table table, Operator<?> where) {
        List<List<ColumnValue<?>>> results = new LinkedList<>();
        String query = "SELECT * FROM " + table.getName() + " WHERE " + where.toQuery() + ";";
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            if (rs.next()) {
                List<ColumnValue<?>> row = new LinkedList<>();
                for (Column<?> column : selectWhat) {
                    Object result = rs.getObject(column.getName());
                    //noinspection unchecked
                    row.add(new SimpleValue<>((Column<Object>) column, (result == null ? column.getDefaultValue() : column.castResult(result))));
                }
                results.add(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    @Override
    public List<List<ColumnValue<?>>> selectRows(List<Column<?>> selectWhat, Table table, Operator<?> where, int start, int limit) {
        List<List<ColumnValue<?>>> results = new LinkedList<>();
        String query = "SELECT * FROM " + table.getName() + " WHERE " + where.toQuery() + " LIMIT " + start + "," + limit + ";";
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            if (rs.next()) {
                List<ColumnValue<?>> row = new LinkedList<>();
                for (Column<?> column : selectWhat) {
                    Object result = rs.getObject(column.getName());
                    //noinspection unchecked
                    row.add(new SimpleValue<>((Column<Object>) column, (result == null ? column.getDefaultValue() : column.castResult(result))));
                }
                results.add(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
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
                ps.setObject(i + 1, value.getColumn().toExport(value.getValue()));
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
            StringBuilder sql = new StringBuilder("create table if not exists " + table.getName() + " (" + pk.getName() + " " + pk.getSqlType().getSqlite()
                    + (pk.getSize() > 0 ? "(" + pk.getSize() + ")" : "") + " PRIMARY KEY" + (table.isAutoIncrementPK() ? " AUTOINCREMENT" : "") + ",");
            for (int i = 0; i < table.getColumns().size(); i++) {
                Column<?> c = table.getColumns().get(i);
                sql.append(" ").append(c.getName()).append(" ").append(c.getSqlType().getSqlite())
                        .append(c.getSize() > 0 ? "(" + c.getSize() + ") " : " ").append("DEFAULT '").append(c.toExport(c.getDefaultValue())).append("'");
                if (i < table.getColumns().size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(");");
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
    }
}
