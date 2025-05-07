package SplitterApp;

public class Roommate {
    private int id;
    private String name;

    public Roommate(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Roommate{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}