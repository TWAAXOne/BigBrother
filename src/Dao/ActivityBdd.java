package Dao;

import java.util.List;

public class ActivityBdd {
    private static final String FILENAME = "activity_data.csv";
    private Bdd bdd;
    public static List<String[]> getListActivity() { return Bdd.getList(FILENAME); }

    public ActivityBdd(Bdd bdd) {
        this.bdd = bdd;
    }

    public void createActivity() {
        System.out.println("Création des activités");
        for (String[] activity : getListActivity()) {
            bdd.run("CREATE (a:Activity{" +
                    "name:'" + activity[0]+ "'})");
        }
        System.out.println("Fin de la création des activités");
    }
}
