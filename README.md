# DataBaseInterface
This is an interface between different database adapters. It currently supports SQLite and MySQL (hikari cp).

# Dependencies
- [SQLite](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc)
- [HikariCP](https://mvnrepository.com/artifact/com.zaxxer/HikariCP)

# Maven Repo
```xml
<repository>
  <id>andrei1058-releases</id>
  <!-- Use /releases if you want a stable version -->
  <url>http://repo.andrei1058.com/snapshots</url>
</repository>
```
```xml
<dependency>
  <groupId>com.andrei1058.dbi</groupId>
  <artifactId>DataBaseInterface</artifactId>
  <version>0.1-SNAPSHOT</version>
  <scope>compile</scope>
</dependency>
```

```java

@SuppressWarnings("UnusedAssignment")
public class SomeClass {

    @Test
    public static void main(String[] args) {

        // database interface
        DatabaseAdapter databaseAdapter = new SQLiteAdapter("localDatabase.db");
        // or MySQL
        databaseAdapter = new HikariAdapter(poolName, poolSize, maxLifeTime, host, port, dbName, username, password, verifyCertificate, useSSL);

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