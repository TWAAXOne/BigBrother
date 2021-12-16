import Dao.*;
import Domaine.Activity;
import Domaine.Person;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Lancement de la création de la base donnée.
        //createDataBase();
        trouverUnePersonnePratiquantUneActivite("Ellen", "Staterfield", "Vélo");
        RecomendationActivite("Queenie", "Gaitskell");
    }

    public static void trouverUnePersonnePratiquantUneActivite(String firstName, String lastname, String activity) {
        Bdd bdd = new Bdd();
        bdd.connect();
        // todo: Ne fonctionne pas avec les liens directs mais seulement avec les relations amical
        Result result = bdd.run("match " +
                "(p:Person{first_name:'" + firstName + "', last_name:'" + lastname + "'}), " +
                "(a:Activity{name:'" + activity + "'}), " +
                "path = shortestPath((p)-[*..10]-(a))" +
                "WHERE ANY(r in relationships(path) where type(r) = 'AMIS_AVEC')" +
                "return path, a");
        Path path = result.single().get("path").asPath();
        List<Person> lstPersonne = new ArrayList<>();
        Activity a = null;
        for (Node node: path.nodes()) {
            if (node.hasLabel("Person")) {
                lstPersonne.add(new Person(node.get("first_name").asString(), node.get("last_name").asString(),
                        node.get("birth_date").asString(), node.get("address").asString(), node.get("gender").asString(),
                        node.get("phone").asString(), node.get("email").asString()));
            } else {
                a = new Activity(node.get("name").asString());
            }
        }
        Person firstPersonne = lstPersonne.get(0);
        System.out.println("Monsieur " +  firstPersonne.getFirstName() + " " + firstPersonne.getLastName() + " cherche à faire du " + a.getName());
        System.out.println("Il doit contacter: ");
        for (int c = 1; c < lstPersonne.size(); c++) {
            System.out.println("   - " + lstPersonne.get(c));
        }

        bdd.close();
    }

    public static void RecomendationActivite(String firstName, String lastName) {
        Bdd bdd = new Bdd();
        bdd.connect();
        Result res = bdd.run("MATCH " +
                "(pPers:Person{first_name:'" + firstName + "', last_name:'" + lastName + "'})," +
                "rPersAmis=(pPers)-[:AMIS_AVEC*..4]->(pAmis)," +
                "rAmisActivity=(pAmis)-[:PRATIQUE]->(activity)" +
                "RETURN pPers, activity");
        List<Activity> lst = new ArrayList<>();
        Value infopPers = res.peek().get(0);
        Person person = new Person(infopPers.get("first_name").asString(), infopPers.get("last_name").asString(),
                infopPers.get("birth_date").asString(), infopPers.get("address").asString(),
                infopPers.get("gender").asString(), infopPers.get("phone").asString(), infopPers.get("email").asString());

        while (res.hasNext()) {
            Record rec = res.next();
            String activityName = rec.get(1).get("name").asString();
            Activity activity;
            if (hasActivity(lst, activityName)) {
                activity = getActivity(lst, activityName);
            } else {
                activity = new Activity(activityName);
                lst.add(activity);
            }
            activity.addNb();
        }
        lst.sort(Activity::compareTo);

        System.out.println("Les activités proposées pour " + person);
        for (int c = 0; c < 3 ; c++) {
            System.out.println("   - " + lst.get(c));
        }
        bdd.close();
    }

    private static boolean hasActivity(List<Activity> lst, String activity) {
        for (Activity act: lst) {
            if (Objects.equals(act.getName(), activity)) {
                return true;
            }
        }
        return false;
    }

    private static Activity getActivity(List<Activity> lst, String activity) {
        for (Activity act: lst) {
            if (Objects.equals(act.getName(), activity)) {
                return act;
            }
        }
        throw new RuntimeException("L'activité n'existe pas");
    }


    public static void createDataBase() {
        Bdd bdd = new Bdd();
        bdd.connect();
        PersonBdd personBdd = new PersonBdd(bdd, 1, 4);
        CompanyBdd companyBdd = new CompanyBdd(bdd);
        ActivityBdd activityBdd = new ActivityBdd(bdd);
        RestaurantBdd restaurantBdd = new RestaurantBdd(bdd);

        personBdd.createUser();
        companyBdd.createBddActivity();
        activityBdd.createActivity();
        restaurantBdd.createRestaurant();

        personBdd.createRelationFriend();
        personBdd.createRelationCompany();
        personBdd.createRelationPratique();
        personBdd.createRelationFrequenteRestaurant();
        bdd.close();
    }


}
