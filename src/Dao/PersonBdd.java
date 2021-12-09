package Dao;

import org.neo4j.driver.Result;

import java.util.List;
import java.util.Random;

public class PersonBdd {
    private static final String FILENAME_LST_PERSON = "personne_data.csv";
    private static final int NB_AMIS_MIN = 1;
    public static List<String[]> getListPerson() { return Bdd.getList(FILENAME_LST_PERSON); }

    public static void deleteUserBdd() {
        Bdd bdd = Bdd.getInstance();
        bdd.connect();
        System.out.println("Suppression des personnes");
        bdd.run("MATCH (p:Person) DELETE p");
    }
    public static void createUser() {
         deleteUserBdd();
        Bdd bdd = Bdd.getInstance();
        bdd.connect();
        System.out.println("Création des personnes");
        for (String[] data : getListPerson()) {
            Result res = bdd.run("CREATE (p:Person{" +
                    "first_name:'" + data[0]+ "'" +
                    ", last_name:'" + data[1] + "'" +
                    ", email:'" + data[2] + "'" +
                    ", gender:'" + data[3] + "'" +
                    ", phone:'" + data[4] + "'" +
                    ", birth_date:'" + data[5] + "'" +
                    ", address:'" + data[6] + "'" +
                    "})");
        }
        bdd.close();
        System.out.println("Fin créations personnes");
    }

    /*
    match(p1:Person{first_name:'Wilmer', last_name:'McRonald'}), (p2:Person{first_name:'Phillis', last_name:'Sheach'})
    create (p1) -[r:AMIS_AVEC]-> (p2) return p1, p2,r
     */
    public static void deleteRelationFriend() {
        Bdd bdd = Bdd.getInstance();
        bdd.connect();
        bdd.run("MATCH p=()-->() DELETE p");
        bdd.close();
    }

    public static void createRelationFriend() {
        deleteRelationFriend();
        Bdd bdd = Bdd.getInstance();
        bdd.connect();
        System.out.println("Création des relations");
        for (String[] data : getListPerson()) {
            int nbAmis = new Random().nextInt(5) + NB_AMIS_MIN;  // [1...5] [min = 1, max = 5]);
            for (int c = NB_AMIS_MIN; c < nbAmis; c++) {
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
        bdd.close();
        System.out.println("Fin des relations");
    }
}

