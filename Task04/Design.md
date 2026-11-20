# Task A4 – What Should Be Serialized? (4 marks)

## A4.1 Decide what to persist

Mark the live JDBC Connection field transient because a connection is an external, live resource and is not a serializable application-state value. Mark the password transient because credentials should not be stored in a serialized snapshot. Re-establish the connection after loading and obtain credentials securely. Hostel → Rooms list → Residents list forms an object graph: serialization follows reachable non-transient references, so every reachable object must be serializable or serialization fails with NotSerializableException. Transient fields are restored to default values (null for references).

## A4.2 Plan for change

Adding phoneNumber and reading an old serialized file: if an explicit stable serialVersionUID is retained, the new field is assigned its default value (null for String) when absent in the old stream. If serialVersionUID differs, InvalidClassException is thrown. Define an explicit serialVersionUID and perform validation/default migration after loading.

## Alternative: CSV/JSON

Text formats are portable and human-readable; field additions can be handled with optional fields/defaults. Java serialization is convenient for Java object graphs but couples files to class definitions and version compatibility. Both save/load n residents in O(n) time. File size depends on encoding and metadata; text is often larger but easier to inspect, while serialization includes class and object metadata. Avoid storing passwords or live connections in either format.
