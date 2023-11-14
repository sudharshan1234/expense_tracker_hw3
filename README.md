# hw3- Implementation & Testing

github link: https://github.com/sudharshan1234/expense_tracker_hw3
The goal of this assignment is to redesign and implement features for the Expense Tracker App, to improve adherence to non-functional requirements (e.g., understandability, modularity, extensibility, testability), design principles/patterns (e.g., open-closed principle, Strategy design pattern), and best practices.

# Features added

Added feature to undo selected transaction
Added 6 new testcases to check for the below cases

1. Add Transaction:

- Steps: Add a transaction with amount 50.00 and category ”food”
- Expected Output: Transaction is added to the table, Total Cost is updated

2. Invalid Input Handling:

- Steps: Attempt to add a transaction with an invalid amount or category
- Expected Output: Error messages are displayed, transactions and Total Cost remain unchanged

3. Filter by Amount:

- Steps: Add multiple transactions with different amounts, apply amount filter
- Expected Output: Only transactions matching the amount are returned (and will be highlighted)

4. Filter by Category:

- Steps: Add multiple transactions with different categories, apply category filter
- Expected Output: Only transactions matching the category are returned (and will be highlighted)

5. Undo Disallowed:

- Steps: Attempt to undo when the transactions list is empty
- Expected Output: Either UI widget is disabled or an error code (exception) is returned (thrown).
  CS 520 Fall 2023. Homework 3. Implementation & Testing Page 2 of 3

6. Undo Allowed:

- Steps: Add a transaction, undo the addition
- Expected Output: Transaction is removed from the table, Total Cost is updated

## Compile

To compile the code from terminal, use the following command:

```
cd src
javac ExpenseTrackerApp.java
java ExpenseTracker
```

You should be able to view the GUI of the project upon successful compilation.

## Java Version

This code is compiled with `openjdk 17.0.7 2023-04-18`. Please update your JDK accordingly if you face any incompatibility issue.
