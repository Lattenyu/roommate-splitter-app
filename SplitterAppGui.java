package SplitterApp;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SplitterAppGui extends JFrame {
    private static final String[] QUOTES = {
        "Split the bill, not the friendship!",
        "In pizza we crust, in bills we split!",
        "Pay up or walk the plank!",
        "Because no one should cry over spilt milk... or bills!",
        "Friends don't let friends overpay!",
        "Divide the bill, conquer the awkwardness!",
        "Bills split faster than a banana in a smoothie!"
    };

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Group group; // Single group instance
    private DatabaseManager dbManager; // Database manager for file operations
    private List<Group> groups; // List to store all groups

    public SplitterAppGui() {
        super("Roommate Splitter Expense App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(650, 850);
        setLocationRelativeTo(null);
        setResizable(false);

        dbManager = new DatabaseManager();
        groups = new ArrayList<>();
        try {
            groups = dbManager.loadGroups();
            dbManager.loadExpenses(groups);
            // Set the last group as the active group if groups exist
            if (!groups.isEmpty()) {
                group = groups.get(groups.size() - 1);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        // No group initially if groups.txt is empty

        mainPanel.add(createStartPanel(), "start");

        setContentPane(mainPanel);
        cardLayout.show(mainPanel, "start");

        // Ensure UI rendering is complete
        SwingUtilities.invokeLater(() -> revalidate());
    }

    // First page where it shows a funny quote and a button to go to the group creation dialog
    private JPanel createStartPanel() {
        JPanel startPanel = new JPanel();
        startPanel.setLayout(null);
        startPanel.setBackground(Color.BLACK);

        // Random quote label
        Random random = new Random();
        String randomQuote = QUOTES[random.nextInt(QUOTES.length)];
        JLabel quoteLabel = new JLabel("<html><div style='text-align: center;'>" + randomQuote + "</div></html>");
        quoteLabel.setFont(new Font("Arial", Font.BOLD, 20));
        quoteLabel.setForeground(Color.WHITE);
        quoteLabel.setBounds(100, 250, 500, 50); 
        startPanel.add(quoteLabel,BorderLayout.CENTER);

        JButton letsGoButton = new JButton("Let's Go!");
        letsGoButton.setFont(new Font("Arial", Font.PLAIN, 18));
        letsGoButton.setBackground(new Color(50, 205, 50));
        letsGoButton.setForeground(Color.BLACK);
        letsGoButton.setFocusPainted(false);
        letsGoButton.setBounds(290, 450, 100, 50); // Adjusted y to 450 to avoid overlap
        letsGoButton.addActionListener(e -> {
            if (group != null) {
                showGroupDetails(); // Show last used group if groups.txt is not empty
            } else {
                showCreateGroupDialog(); // Show create group dialog if no groups exist
            }
        });
        startPanel.add(letsGoButton);

        return startPanel;
    }

    // Creating or editing group
    private void showCreateGroupDialog() {
        JDialog dialog = new JDialog(this, group == null ? "Create New Group" : "Edit Group", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Group Name:");
        JTextField nameField = new JTextField();
        if (group != null) {
            nameField.setText(group.getName()); // Pre-fill group name if editing
        }

        JLabel countLabel = new JLabel("Number of Roommates:");
        JTextField countField = new JTextField();
        if (group != null) {
            countField.setText(String.valueOf(group.getMembers().size())); // Pre-fill number of roommates
        }

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(countLabel);
        panel.add(countField);

        JPanel namesPanel = new JPanel(); 
        namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(namesPanel);
        scrollPane.setPreferredSize(new Dimension(300, 150));

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            String groupName = nameField.getText().trim(); 
            String countText = countField.getText().trim();

            if (groupName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Group name cannot be empty!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int numPeople;
            try {
                numPeople = Integer.parseInt(countText);
                if (numPeople <= 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid number (2 or more)!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Set name fields for each roommate
            namesPanel.removeAll();
            List<String> existingMembers = (group != null) ? group.getMembers() : new ArrayList<>();
            for (int i = 0; i < numPeople; i++) {
                JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                rowPanel.add(new JLabel("Roommate " + (i + 1) + ":"));
                JTextField nameTextField = new JTextField(15);
                // Pre-fill existing member names if available
                if (i < existingMembers.size()) {
                    nameTextField.setText(existingMembers.get(i));
                }
                rowPanel.add(nameTextField);
                namesPanel.add(rowPanel);
            }

            namesPanel.revalidate();
            namesPanel.repaint();
            
            dialog.getContentPane().remove(panel);
            dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
            dialog.getContentPane().add(createDialogButtonPanel(dialog, nameField, namesPanel), BorderLayout.SOUTH);
            dialog.pack();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        buttonPanel.add(nextButton); 

        dialog.getContentPane().add(panel, BorderLayout.CENTER);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Create or update the group
    private JPanel createDialogButtonPanel(JDialog dialog, JTextField nameField, JPanel namesPanel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton createButton = new JButton(group == null ? "Create" : "Update");
        createButton.addActionListener(e -> {
            String groupName = nameField.getText().trim();
            if (groupName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a group name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Collect member names and check for duplicates
            List<String> members = new ArrayList<>();
            Set<String> uniqueNames = new HashSet<>();
            for (Component comp : namesPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel rowPanel = (JPanel) comp;
                    for (Component fieldComp : rowPanel.getComponents()) {
                        if (fieldComp instanceof JTextField) {
                            String name = ((JTextField) fieldComp).getText().trim();
                            if (!name.isEmpty()) {
                                if (!uniqueNames.add(name)) {
                                    JOptionPane.showMessageDialog(dialog, 
                                        "Duplicate name detected: '" + name + "'. Names must be unique.", 
                                        "Error", 
                                        JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                members.add(name);
                            }
                        }
                    }
                }
            }

            if (members.size() < 2) {
                JOptionPane.showMessageDialog(dialog, "Group must have at least 2 members", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Preserve expenses if editing
            List<Expense> existingExpenses = (group != null) ? group.getExpenseManager().getExpenses() : new ArrayList<>();
            group = new Group(groupName, members); // Create or update group
            groups.removeIf(g -> g.getName().equals(groupName)); // Remove old group if updating
            groups.add(group); // Add new/updated group
            // Re-add existing expenses to the new ExpenseManager
            for (Expense expense : existingExpenses) {
                Roommate payer = group.getExpenseManager().findRoommateByName(expense.getPayer().getName());
                if (payer != null) {
                    group.getExpenseManager().addExpense(expense.getDescription(), expense.getAmount(), payer);
                }
            }

            // Save groups to file
            try {
                dbManager.saveGroups(groups);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving group: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dialog.dispose();
            showGroupDetails(); // Show updated group details
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);

        return buttonPanel;
    }

    // Group details such as who owes or owed who, how much does each need to pay etc.
    private void showGroupDetails() {
        if (group == null) {
            showCreateGroupDialog();
            return;
        }

        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new BorderLayout());
        groupPanel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel(group.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        groupPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.BLACK);

        // Expenses list
        JPanel expensesPanel = new JPanel();
        expensesPanel.setLayout(new BoxLayout(expensesPanel, BoxLayout.Y_AXIS));
        expensesPanel.setBackground(Color.BLACK);

        JLabel expensesLabel = new JLabel("Expenses");
        expensesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        expensesLabel.setForeground(Color.WHITE);
        expensesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        expensesPanel.add(expensesLabel);

        List<Expense> expenses = group.getExpenseManager().getExpenses();
        if (expenses.isEmpty()) {
            JLabel noExpensesLabel = new JLabel("No expenses yet.");
            noExpensesLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            noExpensesLabel.setForeground(Color.WHITE);
            noExpensesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            expensesPanel.add(noExpensesLabel);
        } else {
            for (Expense expense : expenses) {
                JLabel expenseLabel = new JLabel(
                    "Date: " + expense.getDate().toString() + ", " +
                    expense.getDescription() + ": $" + String.format("%.2f", expense.getAmount()) +
                    " (Paid by " + expense.getPayer().getName() + ")"
                );
                expenseLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                expenseLabel.setForeground(Color.WHITE);
                expenseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                expensesPanel.add(expenseLabel);
            }
        }

        JScrollPane expensesScrollPane = new JScrollPane(expensesPanel);
        expensesScrollPane.getViewport().setBackground(Color.BLACK);
        expensesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(expensesScrollPane, BorderLayout.CENTER);

        // Splits
        JPanel splitsPanel = new JPanel();
        splitsPanel.setLayout(new BoxLayout(splitsPanel, BoxLayout.Y_AXIS));
        splitsPanel.setBackground(Color.BLACK);

        JLabel splitsLabel = new JLabel("Splits");
        splitsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        splitsLabel.setForeground(Color.WHITE);
        splitsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        splitsPanel.add(splitsLabel);

        List<Split> splits = group.getExpenseManager().calculateSplits();
        for (Split split : splits) {
            JLabel splitLabel = new JLabel(split.toString());
            splitLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            splitLabel.setForeground(Color.WHITE);
            splitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            splitsPanel.add(splitLabel);
        }

        JScrollPane splitsScrollPane = new JScrollPane(splitsPanel);
        splitsScrollPane.getViewport().setBackground(Color.BLACK);
        splitsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(splitsScrollPane, BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton createExpenseButton = new JButton("Add Expense");
        styleButton(createExpenseButton);
        createExpenseButton.addActionListener(e -> showCreateExpenseDialog());
        buttonPanel.add(createExpenseButton);

        JButton editGroupButton = new JButton("Edit Group");
        styleButton(editGroupButton);
        editGroupButton.addActionListener(e -> showCreateGroupDialog());
        buttonPanel.add(editGroupButton);

        groupPanel.add(contentPanel, BorderLayout.CENTER);
        groupPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(groupPanel, "group");
        cardLayout.show(mainPanel, "group");
    }

    // Pop up window when pressed "Add New Expense"
    private void showCreateExpenseDialog() {
        if (group == null) {
            showCreateGroupDialog();
            return;
        }

        JDialog dialog = new JDialog(this, "Add New Expense", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Expense Name:");
        JTextField nameField = new JTextField();

        JLabel amountLabel = new JLabel("Total Amount:");
        JTextField amountField = new JTextField();

        JLabel payerLabel = new JLabel("Paid By:");
        JComboBox<String> payerComboBox = new JComboBox<>();
        for (Roommate roommate : group.getExpenseManager().getRoommates()) {
            payerComboBox.addItem(roommate.getName());
        }

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(payerLabel);
        panel.add(payerComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> {
            String expenseName = nameField.getText().trim();
            if (expenseName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an expense name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String payerName = (String) payerComboBox.getSelectedItem();
            Roommate payer = group.getExpenseManager().findRoommateByName(payerName);
            if (payer == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a valid payer", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            group.getExpenseManager().addExpense(expenseName, amount, payer);
            try {
                dbManager.saveExpenses(group);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving expense: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dialog.dispose();
            showGroupDetails();
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);

        dialog.getContentPane().add(panel, BorderLayout.CENTER);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Button appearance
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 50));
        button.setMinimumSize(new Dimension(250, 50));
        button.setMaximumSize(new Dimension(250, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}