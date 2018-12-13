# MySQL library for Java

## Connect to the DB

The main class of this library, `DBContext` allows for connecting to a MySQL
database using the JDBC API backed by a connection pool powered by 
[HikariCP](https://github.com/brettwooldridge/HikariCP).

Additional tools use prepared statements to create sanitized queries and 
connection artifacts are managed properly to avoid any memory leaks.

### Simple SELECT
```java
DbContext dbContext = new DbContext(
    Configuration.builder()
        .setDbName("db_name")
        .setHost("127.0.0.1")
        .setPort(3306)
        .build(),
    () -> new Credentials("user", "password"));
List<String> results = dbContext.executeQuery(
    "SELECT * FROM test",
    (rs, idx) -> rs.getString("col"));
```

## Prepared Statements

Properly prepared statements avoid MySQL injection.

```java
DbContext db = ...;

String test = "hello world";
String query = "INSERT INTO test(col) VALUES(:col)";
NamedPreparedStatement ns = new NamedPreparedStatement(query);
ns.setInteger("col", test);

int id = db.executeInsert(ns);
```

## Transactions

Run mutiple statements within one DB transaction with rollback.

```java
DbContext db = ...;

Transaction.Builder transactionBuilder = new Transaction.Builder();
transactionBuilder.addStatement((connection, artifacts) -> {
    NamedPreparedStatement ns = ...;
    ns.executeInsert(connection, artifacts);
}).addStatement((connection, artifacts) -> {
    NamedPreparedStatement ns = ...;
    ns.executeInsert(connection, artifacts);
});

db.runTransaction(transactionBuilder.build());
```