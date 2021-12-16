package Domaine;

public class Activity implements Comparable<Activity> {
    private String name;
    private int nb;

    public Activity(String name) {
        this.name = name;
        this.nb = 1;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() { return name; }
    public int getNb() { return nb; }

    public void addNb() {
        this.nb = this.nb + 1;
    }

    @Override
    public int compareTo(Activity o) {
        if (o.getNb() > this.getNb()) { return 1; }
        return -1;
    }
}
