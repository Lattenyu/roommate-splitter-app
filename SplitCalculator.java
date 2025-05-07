package SplitterApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitCalculator {
    public static List<Split> calculateSplits(List<Roommate> roommates, List<Expense> expenses) {
        if (roommates.isEmpty()) {
            return new ArrayList<>();
        }

        double totalExpenses = 0;
        Map<Roommate, Double> contributions = new HashMap<>();
        for (Roommate roommate : roommates) {
            contributions.put(roommate, 0.0);
        }

        for (Expense expense : expenses) {
            totalExpenses += expense.getAmount();
            contributions.put(expense.getPayer(),
                    contributions.getOrDefault(expense.getPayer(), 0.0) + expense.getAmount());
        }

        double fairShare = totalExpenses / roommates.size();
        List<Split> splits = new ArrayList<>();

        for (Roommate roommate : roommates) {
            double paid = contributions.get(roommate);
            double balance = paid - fairShare;
            splits.add(new Split(roommate, balance));
        }

        return splits;
    }
}