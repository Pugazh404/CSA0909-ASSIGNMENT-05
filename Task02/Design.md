# Task A2 – Don’t Read What You Don’t Need (5 marks)

## A2.1 Predict the cost

A 50 MiB file contains 52,428,800 bytes. A loop calling FileInputStream.read() once per byte makes approximately 52,428,800 Java read calls (plus one final end-of-file call). Each call has method and stream overhead; if it reaches the operating system individually, system-call overhead is especially expensive.

## A2.2 Buffered redesign

Use BufferedInputStream with a byte array of 8 KiB or 64 KiB. Repeatedly read a block, inspect only the returned bytes, and increment the line count for each newline byte. The number of bulk reads is approximately fileSize / bufferSize rather than fileSize individual reads. Time complexity remains O(n); auxiliary memory is O(bufferSize).

## A2.3 When buffering gives little benefit

Buffering gives little benefit when the data is already in memory, when the underlying API itself performs large buffered reads, or when the program reads only a tiny amount once. Buffering does not reduce the total bytes that must be examined.

## Alternative: Files.readAllLines

It is simple when the entire text file and its decoded lines comfortably fit in memory. It uses O(file size + line/object overhead) memory, while streaming uses O(buffer size) extra memory. Both require O(n) time. A 50 MB file can consume substantially more than 50 MB when represented as Java String objects and a List.
