package Dao;

import org.neo4j.driver.Result;

import java.util.List;

public class RestaurantBdd {
    private static final String FILENAME = "restaurant_data.csv";
    private Bdd bdd;
    public static List<String[]> getListRestaurant() { return Bdd.getList(FILENAME); }

    public RestaurantBdd(Bdd bdd) {
        this.bdd = bdd;
    }

    public void createRestaurant() {
        System.out.println("Création des restaurants");
        for (String[] dataRestaurant : getListRestaurant()) {
            bdd.run("CREATE (p:Restaurant{" +
                    "name:'" + dataRestaurant[0]+ "'" +
                    ", legalName:'" + dataRestaurant[1] + "'" +
                    ", categoryLabel:'" + dataRestaurant[2] + "'" +
                    ", categorySize:'" + dataRestaurant[3] + "'" +
                    ", foundingYear:'" + dataRestaurant[4] + "'" +
                    "})");
        }
        System.out.println("Fin créations restaurants");
    }
}
