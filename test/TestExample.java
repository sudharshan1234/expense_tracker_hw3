
// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import view.ExpenseTrackerView;

public class TestExample {

    private ExpenseTrackerModel model;
    private ExpenseTrackerView view;
    private ExpenseTrackerController controller;

    @Before
    public void setup() {
        model = new ExpenseTrackerModel();
        view = new ExpenseTrackerView();
        controller = new ExpenseTrackerController(model, view);
    }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }

    public void checkTransaction(double amount, String category, Transaction transaction) {
        assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        } catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }

    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Perform the action: Add a transaction
        double amount = 50.0;
        String category = "food";
        assertTrue(controller.addTransaction(amount, category));

        // Post-condition: List of transactions contains only
        // the added transaction
        assertEquals(1, model.getTransactions().size());

        // Check the contents of the list
        Transaction firstTransaction = model.getTransactions().get(0);
        checkTransaction(amount, category, firstTransaction);

        // Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }

    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Perform the action: Add and remove a transaction
        double amount = 50.0;
        String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);

        // Pre-condition: List of transactions contains only
        // the added transaction
        assertEquals(1, model.getTransactions().size());
        Transaction firstTransaction = model.getTransactions().get(0);
        checkTransaction(amount, category, firstTransaction);

        assertEquals(amount, getTotalCost(), 0.01);

        // Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);

        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());

        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }

    // Case 1:Add Transaction
    @Test
    public void testAddTransaction2() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Perform the action: Add a transaction
        double amount = 50.0;
        String category = "food";
        assertTrue(controller.addTransaction(amount, category));

        // Trigger the button click to add the transaction
        assertEquals(1, model.getTransactions().size());

        // Check the contents of the list
        Transaction firstTransaction = model.getTransactions().get(0);
        checkTransaction(amount, category, firstTransaction);

        // Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }

    // Case 2:Invalid Input Handling
    @Test
    public void testInvalidInputHandling() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Perform the action: Add a transaction with invalid amount or category
        double invalidAmount = -10.0;
        String invalidCategory = "party";
        // checking the controller to see if it is a valid transaction
        assertFalse(controller.addTransaction(invalidAmount, invalidCategory));

        // Post-condition: Transactions, and Total Cost
        // remain unchanged
        assertEquals(0, model.getTransactions().size());
        assertEquals(0.0, getTotalCost(), 0.01);
    }

    // Case 3:Filter by Amount
    @Test
    public void testFilterByAmount() {
        // Pre-condition: List of transactions with different amounts
        assertTrue(controller.addTransaction(50.0, "food"));
        assertTrue(controller.addTransaction(86.0, "entertainment"));
        assertTrue(controller.addTransaction(50.0, "bills"));

        // Perform the action: Apply amount filter

        double filterAmount = 50.0;
        AmountFilter amountFilter = new AmountFilter(filterAmount);
        controller.setFilter(amountFilter);
        List<Transaction> transactions = model.getTransactions();
        List<Transaction> filteredTransactions = amountFilter.filter(transactions);

        // Post-condition: Only transactions matching the amount are returned
        assertEquals(2, filteredTransactions.size());
        for (Transaction transaction : filteredTransactions) {
            checkTransaction(filterAmount, transaction.getCategory(), transaction);
        }
    }

    // Case 4:Filter by Category
    @Test
    public void testFilterByCategory() {
        // Pre-condition: List of transactions with different categories
        assertTrue(controller.addTransaction(45.0, "food"));
        assertTrue(controller.addTransaction(106.0, "bills"));
        assertTrue(controller.addTransaction(215.0, "food"));

        // Perform the action: Apply category filter
        String filterCategory = "food";
        CategoryFilter categoryFilter = new CategoryFilter(filterCategory);
        controller.setFilter(categoryFilter);
        List<Transaction> transactions = model.getTransactions();
        List<Transaction> filteredTransactions = categoryFilter.filter(transactions);

        // Post-condition: Only transactions matching the amount are returned
        assertEquals(2, filteredTransactions.size());
        for (Transaction transaction : filteredTransactions) {
            checkTransaction(transaction.getAmount(), filterCategory, transaction);
        }
    }

    // Case 5:Undo Disallowed
    @Test
    public void testRemoveDisallowed() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        try {
            // Perform the action: Attempt to remove when the transactions list is empty
            controller.removeSelectedTransaction(0);
            fail("Expected an IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            // Post-condition: an error code (exception) is returned (thrown).
            assertNotNull("Exception message should not be null", e.getMessage());
        }

        assertEquals(0, model.getTransactions().size());
        assertEquals(0.0, getTotalCost(), 0.01);
    }

    // Case 6:Undo Allowed
    @Test
    public void testRemoveAllowed() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Perform the action: Add a transaction and remove the addition
        double amount = 550.0;
        String category = "bills";
        assertTrue(controller.addTransaction(amount, category));

        // Pre-condition: List of transactions contains only
        // the added transaction
        assertEquals(1, model.getTransactions().size());
        Transaction firstTransaction = model.getTransactions().get(0);
        checkTransaction(amount, category, firstTransaction);

        assertEquals(amount, getTotalCost(), 0.01);

        // Perform the action: Remove the transaction
        assertTrue(controller.removeSelectedTransaction(0));

        // Post-condition: Transaction is removed from the table, Total Cost is updated
        assertEquals(0, model.getTransactions().size());
        assertEquals(0.0, getTotalCost(), 0.01);
    }

}
