```java
public class SomeClass {

    @Test
    public static void main(String[] args) {

        // database interface
        DatabaseAdapter databaseAdapter = new SQLiteAdapter();

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
        List<ColumnValue<?>> values = new ArrayList<>();
        values.add(new SimpleValue<>(primaryKey, testUUID));
        values.add(new SimpleValue<>(killsColumn, 4));
        databaseAdapter.insert(player_stats, values);

        // get data from db
        int kills = databaseAdapter.select(killsColumn, player_stats, new EqualsOperator<>(primaryKey, testUUID));

        System.out.println("Kills: " + kills);
    }
}
```