# Task A3 – Discover the Data-Access Design Pattern (5 marks)

## A3.1 Common pattern

StudentDB, CourseDB and MarksDB repeat: obtain a connection, prepare SQL, bind values, execute, map ResultSet rows to objects, handle exceptions and close JDBC resources. Table names, columns, keys and object mapping differ.

## A3.2 Generic DAO

```java
public interface Dao<T, K> {
    void insert(T value) throws SQLException;
    Optional<T> findById(K id) throws SQLException;
    List<T> findAll() throws SQLException;
    boolean update(T value) throws SQLException;
    boolean delete(K id) throws SQLException;
}
```

StudentDao implements Dao<Student, Integer>. Each method uses try-with-resources and PreparedStatement. SQL and mapping stay in the DAO; service code expresses business rules.

## A3.3 Object-table mapping

Student.studentId → student.student_id (PRIMARY KEY); Student.name → student.name; Student.email → student.email; Student.department → student.department. The primary key uniquely identifies a row and is used by find/update/delete.

## Alternative: SQL in service/GUI

Embedding SQL in multiple services or screens duplicates statements and mappings. A renamed column must be updated in every location; a DAO localizes the change. Runtime time and database space for an equivalent SQL operation are essentially the same; DAO adds a small abstraction/dispatch cost, not a different database query complexity. DAO generally improves maintainability and testability.
