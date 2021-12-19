import Dao.*;
import Domaine.Activity;
import Domaine.Person;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        /*
        Zone de test:
        Bdd bdd = new Bdd();
        bdd.connect();
        firstRequestBdd(bdd, "Ellen", "Staterfield", "Tennis");
        bdd.close();
        */
        applicationRun();
    }

    public static void applicationRun() {
        System.out.println("Bienvenue sur Big Brother");
        System.out.println("   - 0 : Reinitialiser la bdd (! prend du temps)");
        System.out.println("   - 1 : Requête 1");
        System.out.println("   - 2 : Requête 2");
        System.out.println("   - 3 : Requête 3");
        int action = 9999;
        while (!Arrays.asList(0, 1, 2, 3, 4).contains(action)) {
            System.out.println("Entrer un nombre entre 0 et 4:");
            Scanner obj = new Scanner(System.in);
            action = obj.nextInt();
        }
        Bdd bdd = new Bdd();
        bdd.connect();
        switch (action) {
            case 0: createDataBase(bdd);
                break;
            case 1:
                firstRequestRunning(bdd);
                break;
            case 2:
                secondRequestRunning(bdd);
                break;
            case 3:
                thirdRequestRunning(bdd);
                break;
        }
        bdd.close();
    }

    public static void firstRequestRunning(Bdd bdd) {
        System.out.println("Entrez votre prénom, nom et l'activité");
        Scanner obj = new Scanner(System.in);
        String firstName = obj.nextLine();
        if (Objects.equals(firstName, "")) { firstName = "Marius"; }
        String lastName = obj.nextLine();
        if (Objects.equals(lastName, "")) { lastName = "MacConnechie"; }
        String activity = obj.nextLine();
        if (Objects.equals(activity, "")) { activity = "Basketball"; }
        firstRequestBdd(bdd, firstName, lastName, activity);
    }

    public static void firstRequestBdd(Bdd bdd, String firstName, String lastName, String activity) {
        try {
            Record record = bdd.run("match " +
                    "(p:Person{first_name:'" + firstName + "', last_name:'" + lastName + "'}), " +
                    "(a:Activity{name:'" + activity + "'}), " +
                    "path = shortestPath((p)-[*..10]-(a))" +
                    "WHERE ANY(r in relationships(path) where type(r) = 'AMIS_AVEC')" +
                    "return p, path, a").next();
            // Personne
            Value p = record.get("p");
            Person person = new Person(p.get("first_name").asString(), p.get("last_name").asString(), p.get("birth_date").asString(),
                    p.get("address").asString(), p.get("gender").asString(), p.get("phone").asString(), p.get("email").asString());
            Person premiereP = person;
            // Activité recherché
            Value a = record.get("a");
            Activity act = new Activity(a.get("name").asString());
            // Relation amis
            Path path = record.get("path").asPath();
            for (Node node : path.nodes()) {
                // On vérifie que ca soit des personne puis différent du premier car sinon il est compté deux fois
                if (node.hasLabel("Person") && !Objects.equals(node.get("first_name").asString(), person.getFirstName()) && !Objects.equals(node.get("last_name"), person.getLastName())) {
                    Person pUser = new Person(node.get("first_name").asString(), node.get("last_name").asString(),
                            node.get("birth_date").asString(), node.get("address").asString(), node.get("gender").asString(),
                            node.get("phone").asString(), node.get("email").asString());
                    person.addAmis(pUser);
                    person = pUser;
                }
            }
            person.addActivity(act);
            firstRequestAffichage(premiereP, act);
        } catch (NoSuchRecordException e) {
            System.out.println("Les données entrés sont incorrectes");
        }
    }

    public static void firstRequestAffichage(Person premiereP, Activity activity) {
        System.out.println("Monsieur " + premiereP.getFirstName() + " " + premiereP.getLastName() + " cherche à faire du " + activity.getName());
        System.out.println("Il doit contacter: ");

        Person pers = premiereP.getFirstFriend();
        while (pers.hasFriend()) {
            System.out.println("   - " + pers);
            pers = pers.getFirstFriend();
            if (pers.hasActivity()) {
                System.out.println("   -> " + pers);
            }
        }
    }


    public static void secondRequestRunning(Bdd bdd) {
        System.out.println("Entrez votre prénom et nom");
        Scanner obj = new Scanner(System.in);
        String firstName = obj.nextLine();
        if (Objects.equals(firstName, "")) { firstName = "Queenie"; }
        String lastName = obj.nextLine();
        if (Objects.equals(lastName, "")) { lastName = "Gaitskell"; }
        secondeRequestBdd(bdd, firstName, lastName);
    }

    public static void secondeRequestBdd(Bdd bdd, String firstName, String lastName) {
        try {
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
                secondRequestAffichage(lst, person);
            }
        } catch (NoSuchRecordException e) {
            System.out.println("Les données entrés sont incorrectes");
        }
    }

    private static void secondRequestAffichage(List<Activity> lst, Person person) {
        lst.sort(Activity::compareTo);

        System.out.println("Les activités proposées pour " + person);
        for (int c = 0; c < lst.size(); c++) {
            System.out.println("   - " + lst.get(c).getName() + " : " + lst.get(c).getNb());
        }
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

    public static void thirdRequestRunning(Bdd bdd) {
        System.out.println("Entrez le prénom, nom de départ");
        Scanner obj = new Scanner(System.in);

        String firstName1 = obj.nextLine();
        if (Objects.equals(firstName1, "")) { firstName1 = "Maximilien"; }
        String lastName1 = obj.nextLine();
        if (Objects.equals(lastName1, "")) { lastName1 = "Gabbitus"; }

        System.out.println("Entrez le prénom, nom de fin");
        Scanner obj2 = new Scanner(System.in);

        String firstName2 = obj2.nextLine();
        if (Objects.equals(firstName2, "")) { firstName2 = "Augie"; }
        String lastName2 = obj2.nextLine();
        if (Objects.equals(lastName2, "")) { lastName2 = "Francois"; }

        thirdRequestBdd(bdd, firstName1, lastName1, firstName2, lastName2);
    }

    public static void thirdRequestBdd(Bdd bdd, String firstName1, String lastName1, String firstName2, String lastName2) {
        try {
            Record record = bdd.run("match (p:Person{first_name:'" + firstName1 + "', last_name:'" + lastName1 + "'}), " +
                " path = shortestPath((p)-[:AMIS_AVEC*..10]-(:Person{first_name:'" + firstName2 + "', last_name:'" + lastName2 + "'})) " +
                " return p, path").next();

            // Personne
            Value p = record.get("p");
            Person person = new Person(p.get("first_name").asString(), p.get("last_name").asString(), p.get("birth_date").asString(),
                    p.get("address").asString(), p.get("gender").asString(), p.get("phone").asString(), p.get("email").asString());
            Person premiereP = person;
            // Path
            Path path = record.get("path").asPath();
            for (Node node : path.nodes()) {
                if (!Objects.equals(node.get("first_name").asString(), person.getFirstName()) && !Objects.equals(node.get("last_name"), person.getLastName())) {
                    Person pUser = new Person(node.get("first_name").asString(), node.get("last_name").asString(),
                            node.get("birth_date").asString(), node.get("address").asString(), node.get("gender").asString(),
                            node.get("phone").asString(), node.get("email").asString());
                    person.addAmis(pUser);
                    person = pUser;
                }
            }
            thirdRequestAffichage(premiereP, person);

        } catch (NoSuchRecordException e) {
            System.out.println("Les données entrés sont incorrectes");
        }
    }

    public static void thirdRequestAffichage(Person p1, Person p2) {
        System.out.println("Monsieur " + p1 + " cherche à contacter " + p2);
        System.out.println("Il doit contacter: ");

        for (Person p = p1.getFirstFriend(); p!=p2 ; p=p.getFirstFriend()) {
            System.out.println("   - " + p);
        }
        System.out.println("   -> " + p2);
    }

    public static void createDataBase(Bdd bdd) {
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
    }


}
