# Task A5 – Design for Correctness and Safety (4 marks)

## A5.1 Transaction pseudocode

1. START
2. GET JDBC connection
3. SET autoCommit FALSE
4. TRY
5. UPDATE hostel_wallet SET balance = balance - 500 WHERE id = source AND balance >= 500
6. IF exactly one row was not updated, RAISE insufficient-funds/error
7. UPDATE canteen_wallet SET balance = balance + 500 WHERE id = destination
8. IF exactly one row was not updated, RAISE error
9. COMMIT
10. CATCH any failure: ROLLBACK and report transfer failed
11. FINALLY restore autoCommit and CLOSE connection
12. STOP

Both updates commit together or neither does. In a real system, validate both account rows and use suitable isolation/locking.

## A5.2 SQL injection

Concatenating input into SQL lets an attacker provide SQL syntax rather than a literal value. For example, an ID such as `' OR '1'='1' -- ` can alter the WHERE condition and bypass a weak login query. Use parameters:

```java
String sql = "SELECT id FROM users WHERE id = ? AND pwd = ?";
try (PreparedStatement ps = con.prepareStatement(sql)) {
    ps.setString(1, id);
    ps.setString(2, password);
    try (ResultSet rs = ps.executeQuery()) {
        boolean accepted = rs.next();
    }
}
```

Store password hashes rather than plaintext passwords in a real application.

## Alternative: two auto-commit updates

If the debit succeeds and the program fails before the credit, money disappears. If credit runs first and debit fails, money is created. A transaction adds coordination/logging overhead but protects the invariant; two independent updates may be marginally simpler/faster but are not correct for a transfer.
