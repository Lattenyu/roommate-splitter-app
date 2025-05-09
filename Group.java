package SplitterApp;
/*Keep track of the groups
Usable for any type of group, it doesn't have to be only roommate
Very practible
*/
import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name; //group name
    private ExpenseManager expenseManager; 

    // Constructor to create a group
    public Group(String name, List<String> members) {
        this.name = name;
        this.expenseManager = new ExpenseManager();
        for (String member : members) {
            expenseManager.addRoommate(member);
        }
    }

    // Get the group name
    public String getName() {
        return name;
    }

    // Create the list of roommate name
    public List<String> getMembers() {
        List<String> members = new ArrayList<>();
        for (Roommate roommate : expenseManager.getRoommates()) {
            members.add(roommate.getName());
        }
        return members;
    }

    // Create a manager
    public ExpenseManager getExpenseManager() {
        return expenseManager;
    }
}