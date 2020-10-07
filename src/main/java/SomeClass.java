
import com.andrei1058.dbi.column.IntegerColumn;
import com.andrei1058.dbi.column.UUIDColumn;
import com.andrei1058.dbi.insert.ColumnValue;
import com.andrei1058.dbi.insert.IColumnValue;
import com.andrei1058.dbi.operator.EqualsOperator;
import com.andrei1058.dbi.table.Table;
import com.andrei1058.dbi.table.TableBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SomeClass {

    public static void main(String[] args) {

        // database interface
        DatabaseAdapter databaseAdapter = new InternalDatabaseAdapter();

        // columns
        UUIDColumn primaryKey = new UUIDColumn("player_uuid", null);
        IntegerColumn killsColumn = new IntegerColumn("kills", 2, 8);

        // table builder
        TableBuilder player_stats_builder = new TableBuilder("player_stats", primaryKey);
        player_stats_builder.withColumn(killsColumn);

        // table variable
        Table player_stats = player_stats_builder.build();

        // create table on db
        databaseAdapter.createTable(player_stats, true);

        // uuid for testing
        UUID testUUID = UUID.fromString("c252c59e-dd51-44f6-9697-89ac6a88e6a1");

        // insert
        List<IColumnValue<?>> values = new ArrayList<>();
        values.add(new ColumnValue<>(primaryKey, testUUID));
        values.add(new ColumnValue<>(killsColumn, 4));
        databaseAdapter.insert(player_stats, values);

        // get data from db
        int kills = databaseAdapter.select(killsColumn, player_stats, new EqualsOperator<>(primaryKey, testUUID));

        System.out.println("Kills: " + kills);
    }
}
