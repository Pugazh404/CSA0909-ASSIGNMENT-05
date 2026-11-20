# Task A1 – Design a File Format (7 marks)

## A1.1 Record layouts

**Delimited text layout:** `passId,regNo,name,routeNo,boardingStop,validityDate,amountPaid` – one pass per line. Use UTF-8 and comma delimiter. To support commas and quotes in names/stops, use CSV quoting: surround a field containing comma, quote or newline with double quotes and double each embedded quote. A production implementation should use a CSV library; the simple implementation in Task06 deliberately uses five comma-separated fields as specified by the supplied code.

**Binary DataOutputStream layout:** write fields in this exact order: passId (writeUTF), regNo (writeUTF), name (writeUTF), routeNo (writeInt), boardingStop (writeUTF), validityDate (writeUTF ISO-8601), amountPaid (writeDouble). Each writeUTF stores a length prefix and UTF-8 bytes; numeric fields have fixed-width representations.

## A1.2 Operations – SIMATS-style pseudocode

**ADD PASS**
1. START
2. VALIDATE pass ID, register number, route, date and amount
3. OPEN file using FileWriter in append mode wrapped in BufferedWriter
4. ENCODE fields using CSV escaping
5. WRITE encoded record and NEWLINE
6. FLUSH and CLOSE using automatic resource handling
7. STOP

**FIND BY PASS ID**
1. START
2. IF file does not exist, RETURN NOT FOUND
3. OPEN FileReader wrapped in BufferedReader
4. REPEAT READ one line until end of file
5. PARSE line; IF field count or field validation is invalid, RECORD damaged line and CONTINUE
6. IF parsed pass ID equals requested ID, RETURN record
7. CLOSE reader; RETURN NOT FOUND
8. STOP

**LIST BY ROUTE**
1. START
2. OPEN FileReader wrapped in BufferedReader
3. READ each line; parse and validate
4. IF valid record route equals requested route, DISPLAY record
5. SKIP and report malformed records
6. CLOSE reader
7. STOP

BufferedReader/BufferedWriter reduce small underlying read/write operations. FileReader/FileWriter provide character conversion. FileInputStream/FileOutputStream are suitable for raw bytes and binary formats, not convenient text parsing.

## A1.3 Damaged data and failure handling

- Damaged line: validate field count, numeric route/amount, required fields and date format; log line number and skip it rather than stopping the whole scan.
- Missing file: distinguish a missing file from an empty file; report it and create the file only for an add operation.
- Incomplete write/power failure: write a complete replacement to a temporary file, flush and close it, then atomically move it over the original where supported. For append-only use, keep a backup or journal and detect a final line without a valid terminator/checksum.

## Alternative: RandomAccessFile

With fixed-size records, record i begins at `i × recordSize`; direct access is O(1) once the record index is known. Finding by pass ID still requires O(n) unless a separate ID-to-index index is maintained. Fixed-size padded name/stop fields waste space when actual strings are short; the benefit is predictable offsets and in-place updates.
