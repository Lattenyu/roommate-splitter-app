package SplitterApp;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private ExpenseManager expenseManager;

    public Group(String name, List<String> members) {
        this.name = name;
        this.expenseManager = new ExpenseManager();
        for (String member : members) {
            expenseManager.addRoommate(member);
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        List<String> members = new ArrayList<>();
        for (Roommate roommate : expenseManager.getRoommates()) {
            members.add(roommate.getName());
        }
        return members;
    }

    public ExpenseManager getExpenseManager() {
        return expenseManager;
    }
}