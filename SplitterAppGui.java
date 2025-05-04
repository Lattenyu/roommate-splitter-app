package SplitterAppGui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SplitterAppGui extends JFrame {
    private List<Bill> bills = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable billTable;
    private String currentUser = "alice@example.com"; // Simulated logged-in user

    public SplitterAppGui() {
        super("Roommate Splitter Expense App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Create Bill Form
        JLabel nameLabel = new JLabel("Bill Name:");
        nameLabel.setBounds(20, 20, 100, 25);
        add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(120, 20, 300, 25);
        add(nameField);

        JLabel amountLabel = new JLabel("Total Amount:");
        amountLabel.setBounds(20, 50, 100, 25);
        add(amountLabel);

        JTextField amountField = new JTextField();
        amountField.setBounds(120, 50, 300, 25);
        add(amountField);

        JLabel splitLabel = new JLabel("Split (email:amount):");
        splitLabel.setBounds(20, 80, 150, 25);
        add(splitLabel);

        JTextField splitField = new JTextField("alice@example.com:0,bob@example.com:0");
        splitField.setBounds(170, 80, 250, 25);
        add(splitField);

        JButton createButton = new JButton("Create Bill");
        createButton.setBounds(20, 110, 400, 30);
        add(createButton);

        // Bill Table
        String[] columns = {"ID", "Name", "Amount", "Creator", "Actions"};
        tableModel = new DefaultTableModel(columns, 0);
        billTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.setBounds(20, 150, 400, 400);
        add(scrollPane);

        // Create Bill Action
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    double amount = Double.parseDouble(amountField.getText());
                    String[] splits = splitField.getText().split(",");

                    // Create bill
                    String billId = UUID.randomUUID().toString();
                    Bill bill = new Bill(billId, name, amount, currentUser);

                    // Parse splits
                    for (String split : splits) {
                        String[] parts = split.split(":");
                        if (parts.length == 2) {
                            String user = parts[0].trim();
                            double splitAmount = Double.parseDouble(parts[1].trim());
                            bill.addSplit(user, splitAmount);
                        }
                    }

                    // Add to list and table
                    bills.add(bill);
                    JButton viewButton = new JButton("View");
                    JButton editButton = bill.getCreator().equals(currentUser) ? new JButton("Edit") : null;

                    // Add row to table
                    Object[] row = {
                        bill.getId(),
                        bill.getName(),
                        bill.getTotalAmount(),
                        bill.getCreator(),
                        bill.getCreator().equals(currentUser) ? new JPanel() {{ add(viewButton); add(editButton); }} : new JPanel() {{ add(viewButton); }}
                    };
                    tableModel.addRow(row);

                    // View Bill Action
                    viewButton.addActionListener(ev -> {
                        JOptionPane.showMessageDialog(null, formatBillDetails(bill), "Bill Details", JOptionPane.INFORMATION_MESSAGE);
                    });

                    // Edit Bill Action (only for creator)
                    if (editButton != null) {
                        editButton.addActionListener(ev -> {
                            JTextField editNameField = new JTextField(bill.getName());
                            JTextField editAmountField = new JTextField(String.valueOf(bill.getTotalAmount()));
                            Object[] message = {
                                "Bill Name:", editNameField,
                                "Total Amount:", editAmountField
                            };
                            int option = JOptionPane.showConfirmDialog(null, message, "Edit Bill", JOptionPane.OK_CANCEL_OPTION);
                            if (option == JOptionPane.OK_OPTION) {
                                bill = new Bill(bill.getId(), editNameField.getText(), Double.parseDouble(editAmountField.getText()), bill.getCreator());
                                bills.set(bills.indexOf(bill), bill);
                                refreshTable();
                            }
                        });
                    }

                    // Clear form
                    nameField.setText("");
                    amountField.setText("");
                    splitField.setText("alice@example.com:0,bob@example.com:0");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount or split format!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private String formatBillDetails(Bill bill) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bill: ").append(bill.getName()).append("\n");
        sb.append("Total Amount: $").append(bill.getTotalAmount()).append("\n");
        sb.append("Creator: ").append(bill.getCreator()).append("\n");
        sb.append("Splits:\n");
        for (Map.Entry<String, Double> split : bill.getSplits().entrySet()) {
            sb.append("  ").append(split.getKey()).append(": $").append(split.getValue()).append("\n");
        }
        return sb.toString();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Bill bill : bills) {
            JButton viewButton = new JButton("View");
            JButton editButton = bill.getCreator().equals(currentUser) ? new JButton("Edit") : null;

            Object[] row = {
                bill.getId(),
                bill.getName(),
                bill.getTotalAmount(),
                bill.getCreator(),
                bill.getCreator().equals(currentUser) ? new JPanel() {{ add(viewButton); add(editButton); }} : new JPanel() {{ add(viewButton); }}
            };
            tableModel.addRow(row);

            viewButton.addActionListener(ev -> {
                JOptionPane.showMessageDialog(null, formatBillDetails(bill), "Bill Details", JOptionPane.INFORMATION_MESSAGE);
            });

            if (editButton != null) {
                editButton.addActionListener(ev -> {
                    JTextField editNameField = new JTextField(bill.getName());
                    JTextField editAmountField = new JTextField(String.valueOf(bill.getTotalAmount()));
                    Object[] message = {
                        "Bill Name:", editNameField,
                        "Total Amount:", editAmountField
                    };
                    int option = JOptionPane.showConfirmDialog(null, message, "Edit Bill", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        Bill updatedBill = new Bill(bill.getId(), editNameField.getText(), Double.parseDouble(editAmountField.getText()), bill.getCreator());
                        bills.set(bills.indexOf(bill), updatedBill);
                        refreshTable();
                    }
                });
            }
        }
    }
}