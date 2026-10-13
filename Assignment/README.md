# Library Management System

**Name:** Add your name  
**Roll Number:** Add your roll number  
**Batch:** Add your batch

## Description
A beginner-friendly console-based Library Management System written in Core Java.

## Requirements
- Java JDK 17 or later
- No external libraries

## Compile and run
Open a terminal in the `Assignment` folder.

Windows PowerShell:
```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.hcl.library.Main
```

macOS/Linux:
```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out com.hcl.library.Main
```

## Main classes
- `LibraryItem`: abstract parent class for Book, Magazine, and DVD.
- `Borrowable`: interface for borrowing operations.
- `Member`: stores member details and borrowed items.
- `LibraryService`: handles library rules and operations.
- `Repository` and `InMemoryRepository`: generic in-memory storage.
- `SearchCache`: stores up to five recent searches.
- `CollectionUtils`: generic collection helper methods.

## Test cases
1. Add a book and borrow it: item should become issued.
2. Borrow an already issued item: show an availability error.
3. Borrow a missing item ID: show an item-not-found error.
4. Borrow a fourth item for one member: show the borrow-limit error.
5. Return an item: show its late fee and notify the first waitlisted member.
