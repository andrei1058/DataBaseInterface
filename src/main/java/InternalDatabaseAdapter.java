import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.insert.IColumnValue;
import com.andrei1058.dbi.operator.Operator;
import com.andrei1058.dbi.table.Table;

import java.sql.*;
import java.util.List;

public class InternalDatabaseAdapter implements DatabaseAdapter {

    private Connection connection;

    public InternalDatabaseAdapter() {
        try {
            // create a database connection
            //if (DriverManager.getDriver("sqlite") == null){
            //    DriverManager.registerDriver(new org.sqlite.JDBC());
            //}
            connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
            //statement.executeUpdate("insert into person values(2, 'yui')");
            //ResultSet rs = statement.executeQuery("select * from person");
            //while (rs.next()) {
            // read the result set
            //    System.out.println("name = " + rs.getString("name"));
            //    System.out.println("id = " + rs.getInt("id"));
            // }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public <T> T select(Column<T> what, Table from, Operator<?> where) {
        String query = "SELECT " + what.getName() + " FROM " + from.getName() + " WHERE " + where.toQuery() + ";";
        System.out.println(query);
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
        return null;
    }

    @Override
    public void insert(Table table, List<IColumnValue<?>> values) {
        if (table.getColumns().isEmpty()){
            //todo throw empty table exception
            return;
        }
        StringBuilder firstPart = new StringBuilder("INSERT INTO " + table.getName() +"(");
        StringBuilder secondPart = new StringBuilder("VALUES(");
        for (int i = 0; i < values.size(); i++) {
            IColumnValue<?> value = values.get(i);
            firstPart.append(value.getColumn().getName());
            secondPart.append("?");
            if (i < values.size()-1){
                firstPart.append(",");
                secondPart.append(",");
            } else {
                firstPart.append(") ");
                secondPart.append(");");
            }
        }
        System.out.println(firstPart.append(secondPart).toString());
        try (PreparedStatement ps = connection.prepareStatement(firstPart.append(secondPart).toString())) {
            for (int i = 0; i < values.size(); i++) {
                IColumnValue<?> value = values.get(i);
                ps.setObject(i+1, value.getValue());
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
                sql.append(" ").append(c.getName()).append(" ").append(c.getSqlType().getSqlite()).append(" DEFAULT ").append(c.getDefaultValue());
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
}
