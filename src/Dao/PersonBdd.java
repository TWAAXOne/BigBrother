package Dao;

import java.util.List;
import java.util.Random;

public class PersonBdd{
    private static final String FILENAME = "personne_data.csv";
    private Bdd bdd;

    public PersonBdd(Bdd bdd) {
        this.bdd = bdd;
    }

    public static List<String[]> getListPerson() { return Bdd.getList(FILENAME); }

    public void createUser() {
        System.out.println("Création des personnes");
        for (String[] data : getListPerson()) {
            bdd.run("CREATE (p:Person{" +
                    "first_name:'" + data[0]+ "'" +
                    ", last_name:'" + data[1] + "'" +
                    ", email:'" + data[2] + "'" +
                    ", gender:'" + data[3] + "'" +
                    ", phone:'" + data[4] + "'" +
                    ", birth_date:'" + data[5] + "'" +
                    ", address:'" + data[6] + "'" +
                    "})");
        }
        System.out.println("Fin créations personnes");
    }

    public void createRelationFriend() {
        System.out.println("Création des relations Friend");
        for (String[] data : getListPerson()) {
            int nbAmis = new Random().nextInt(4) + 1;  // [1...5] [min = 1, max = 5]);
            for (int c = 1; c <= nbAmis; c++) {
                int noAmis = new Random().nextInt(getListPerson().size());
                String[] dataAmis = getListPerson().get(noAmis);
                bdd.run("MATCH(p1:Person{" +
                        "first_name:'" + data[0] + "', " +
                        "last_name:'" + data[1] + "'}), " +
                        "(p2:Person{" +
                        "first_name:'" + dataAmis[0] + "', " +
                        "last_name:'" + dataAmis[1] + "'}) " +
                        "CREATE (p1) -[r:AMIS_AVEC]-> (p2)"
                    );
            }
        }
        System.out.println("Fin des relations Friend");
    }

    public void createRelationCompany() {
        System.out.println("Création des relations Personne - Compagnie");
        for (String[] dataPersonne : getListPerson()) {
            int noCompany = new Random().nextInt(CompanyBdd.getListCompany().size());
            String[] dataCompany = CompanyBdd.getListCompany().get(noCompany);
            bdd.run("MATCH(p:Person{" +
                    "first_name:'" + dataPersonne[0] + "', " +
                    "last_name:'" + dataPersonne[1] + "'}), " +
                    "(c:Company{" +
                    "company_name:'" + dataCompany[0] + "'}) " +
                    "CREATE (p) -[r:TRAVAILLE]-> (c)"
            );
        }
        System.out.println("Fin création Personne - Compagnie");
    }

    public void createRelationPratique() {
        System.out.println("Création des relations Personne - Activity");
        for (String[] dataPersonne : getListPerson()) {
            int nbActivity = new Random().nextInt(2) + 1;
            for (int c = 1; c <= nbActivity; c++) {
                int noActivity = new Random().nextInt(ActivityBdd.getListActivity().size());
                String[] activity = ActivityBdd.getListActivity().get(noActivity);
                bdd.run("MATCH(p:Person{" +
                        "first_name:'" + dataPersonne[0] + "', " +
                        "last_name:'" + dataPersonne[1] + "'}), " +
                        "(a:Activity{" +
                        "name:'" + activity[0] + "'}) " +
                        "CREATE (p) -[r:PRATIQUE]-> (a)"
                );
            }
        }
        System.out.println("Fin créations des relations");
    }


    public void createRelationFrequenteRestaurant() {
        System.out.println("Création des relations Personne - Restaurant");
        for (String[] dataPersonne : getListPerson()) {
            int nbRestaurant = new Random().nextInt(3) + 1;
            for (int c = 1; c <= nbRestaurant; c++) {
                int noRestaurant = new Random().nextInt(RestaurantBdd.getListRestaurant().size());
                String[] activity = RestaurantBdd.getListRestaurant().get(noRestaurant);
                bdd.run("MATCH(p:Person{" +
                        "first_name:'" + dataPersonne[0] + "', " +
                        "last_name:'" + dataPersonne[1] + "'}), " +
                        "(rest:Restaurant{" +
                        "name:'" + activity[0] + "'}) " +
                        "CREATE (p) -[r:FREQUENTE_RESTAURANT]-> (rest)"
                );
            }
        }
        System.out.println("Fin créations des relations");
    }


}

