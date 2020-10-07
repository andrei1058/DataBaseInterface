import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.insert.IColumnValue;
import com.andrei1058.dbi.operator.Operator;
import com.andrei1058.dbi.table.Table;

import java.util.List;

public interface DatabaseAdapter {
    <T> T select(Column<T> from, Table table, Operator<?> where);

    <T> List<T> select(Column<T> from, Table table, Operator<?> where, int limit);

    void insert(Table table, List<IColumnValue<?>> values);

    void createTable(Table table, boolean drop);
}
