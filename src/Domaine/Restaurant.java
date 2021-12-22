package Domaine;

public class Restaurant implements Comparable<Restaurant> {
    private String name;
    private int nb;

    public Restaurant(String name) {
        this.name = name;
        this.nb = 0;
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
    public int compareTo(Restaurant o) {
        if (o.getNb() > this.getNb()) { return 1; }
        return -1;
    }

}
