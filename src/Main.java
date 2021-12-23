import Dao.*;
import Domaine.Activity;
import Domaine.Company;
import Domaine.Person;
import Domaine.Restaurant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        applicationRun();
    }

    public static void applicationRun() {
        System.out.println("Bienvenue sur Big Brother");
        System.out.println("   - 0 : Reinitialiser la bdd (! prend du temps)");
        System.out.println("   - 1 : Rechercher le chemin d'une personne à une entreprise");
        System.out.println("   - 2 : Recommandation d'une activité à une personne");
        System.out.println("   - 3 : Partir d'une personne trouvé le chemin jusqu'à une autre personne");
        System.out.println("   - 4 : Recommandation d'un restaurant à partir des collègues d'une même entreprise");
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
            case 4:
                fourthRequestRunning(bdd);
        }
        bdd.close();
    }


    public static void firstRequestRunning(Bdd bdd) {
        Scanner obj = new Scanner(System.in);
        System.out.println("Entrez votre prénom: ");
        String firstName = obj.nextLine();
        if (Objects.equals(firstName, "")) { firstName = "Jayne"; }
        System.out.println("Entrez votre nom: ");
        String lastName = obj.nextLine();
        if (Objects.equals(lastName, "")) { lastName = "Lober"; }
        System.out.println("Entrez l'entreprise recherché: ");
        String company = obj.nextLine();
        if (Objects.equals(company, "")) { company = "Sotherly Hotels LP"; }
        firstRequestBdd(bdd, firstName, lastName, company);
    }

    public static void firstRequestBdd(Bdd bdd, String firstName, String lastName, String activity) {
        try {
            Record record = bdd.run("match (p:Person{first_name:'"+firstName+"', last_name:'"+lastName + "'}), " +
                    "(c:Company{company_name:'"+activity+"'})," +
                    "path = shortestpath((p)-[:AMIS_AVEC|TRAVAILLE*..20]-(c))" +
                    "return p, path, c").next();
            // Personne
            Value p = record.get("p");
            Person person = new Person(p.get("first_name").asString(), p.get("last_name").asString(), p.get("birth_date").asString(),
                    p.get("address").asString(), p.get("gender").asString(), p.get("phone").asString(), p.get("email").asString());
            Person premiereP = person;
            // Company
            Company company = null;
            // Path
            Path path = record.get("path").asPath();
            for (Node node : path.nodes()) {
                // On vérifie que ca soit des personne puis différent du premier car sinon il est compté deux fois
                if (node.hasLabel("Person") && !Objects.equals(node.get("first_name").asString(), person.getFirstName()) && !Objects.equals(node.get("last_name"), person.getLastName())) {
                    Person pUser = new Person(node.get("first_name").asString(), node.get("last_name").asString(),
                            node.get("birth_date").asString(), node.get("address").asString(), node.get("gender").asString(),
                            node.get("phone").asString(), node.get("email").asString());
                    if (company == null) { // Si il n'y a pas de company, on ajoute à l'amis précédent
                        person.addAmis(pUser);
                    } else { // Sinon, on ajoute la personne dans la liste des travailleur dans l'entreprise
                        company.addListOfWorker(pUser);
                        company = null;
                    }
                    person = pUser;
                } else if(node.hasLabel("Company")) {
                    company = new Company(node.get("company_name").asString(), node.get("industry").asString(),
                            node.get("stock_market").asString(), node.get("sector").asString(), node.get("address").asString());
                    person.setCompany(company);
                }
            }
            firstRequestAffichage(premiereP, company);
        } catch (NoSuchRecordException e) {
            System.out.println("Les données entrés sont incorrectes");
        }
    }

    public static void firstRequestAffichage(Person person, Company company) {
        System.out.println("Monsieur " + person + " cherche à entrer en contact avec l'entreprise: " + company);
        System.out.println("Chemin: ");

        while (person != null) {
            System.out.println("   -" + person);
            if (person.hasFriend()) {
                person = person.getFirstFriend();
            }
            else if (person.hasCompany()) {
                Company comp = person.getCompany();
                System.out.println("    -> Travaille dans " + comp);
                if (comp.hasWorker()) {
                    person = comp.getFirstWorker();
                } else {
                    person = null;
                }
            }
        }
    }


    public static void secondRequestRunning(Bdd bdd) {
        Scanner obj = new Scanner(System.in);
        System.out.println("Entrez votre prénom");
        String firstName = obj.nextLine();
        if (Objects.equals(firstName, "")) { firstName = "Queenie"; }
        System.out.println("Entrez votre nom");
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
            }
            secondRequestAffichage(lst, person);
        } catch (NoSuchRecordException e) {
            System.out.println("Les données entrés sont incorrectes");
        }
    }

    private static void secondRequestAffichage(List<Activity> lst, Person person) {
        lst.sort(Activity::compareTo);

        System.out.println("Les activités proposées pour " + person);
        for (int c = 0; c < (Math.min(lst.size(), 3)); c++) {
            System.out.println("   - " + lst.get(c).getName());
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
        Scanner obj = new Scanner(System.in);

        System.out.println("Entrez le prénom de départ");
        String firstName1 = obj.nextLine();
        if (Objects.equals(firstName1, "")) { firstName1 = "Maximilien"; }

        System.out.println("Entrez le nom de départ");
        String lastName1 = obj.nextLine();
        if (Objects.equals(lastName1, "")) { lastName1 = "Gabbitus"; }

        Scanner obj2 = new Scanner(System.in);

        System.out.println("Entrez le prénom de fin");
        String firstName2 = obj2.nextLine();
        if (Objects.equals(firstName2, "")) { firstName2 = "Augie"; }
        System.out.println("Entrez le nom de fin");
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

    private static void fourthRequestRunning(Bdd bdd) {
        Scanner obj = new Scanner(System.in);
        System.out.println("Entrez votre prénom");
        String firstName = obj.nextLine();
        if (Objects.equals(firstName, "")) { firstName = "Marcello"; }
        System.out.println("Entrez votre nom");
        String lastName = obj.nextLine();
        if (Objects.equals(lastName, "")) { lastName = "Raikes"; }
        fourthRequestBdd(bdd, firstName, lastName);
    }

    public static void fourthRequestBdd(Bdd bdd, String firstName, String lastName) {
        try {
            Result res = bdd.run("MATCH " +
                    "(pPers:Person{first_name:'" + firstName + "', last_name:'" + lastName + "'})," +
                    "(pPers)-[:TRAVAILLE]->(pCompany)," +
                    "(pCompany)<-[:TRAVAILLE]-(pPerson)," +
                    "(pPerson)-[:FREQUENTE_RESTAURANT]->(rRest)"+
                    "RETURN  pPers,pCompany,pPerson,rRest");
            List<Restaurant> lst = new ArrayList<>();
            Value infopPers = res.peek().get(0);
            Person person = new Person(infopPers.get("first_name").asString(), infopPers.get("last_name").asString(),
                    infopPers.get("birth_date").asString(), infopPers.get("address").asString(),
                    infopPers.get("gender").asString(), infopPers.get("phone").asString(), infopPers.get("email").asString());

            while (res.hasNext()) {
                Record rec = res.next();
                String RestaurantName = rec.get(3).get("name").asString();
                Restaurant restaurant;
                if (hasRestaurant(lst, RestaurantName)) {
                    restaurant = getRestaurant(lst, RestaurantName);
                } else {
                    restaurant = new Restaurant(RestaurantName);
                    lst.add(restaurant);
                }
                restaurant.addNb();
            }
            fourthRequestAffichage(lst, person);
        } catch (NoSuchRecordException e) {
            System.out.println("Les données entrés sont incorrectes");
        }
    }

    private static boolean hasRestaurant(List<Restaurant> lst, String restaurant) {
        for (Restaurant rest: lst) {
            if (Objects.equals(rest.getName(), restaurant)) {
                return true;
            }
        }
        return false;
    }

    private static Restaurant getRestaurant(List<Restaurant> lst, String restaurant) {
        for (Restaurant rest: lst) {
            if (Objects.equals(rest.getName(), restaurant)) {
                return rest;
            }
        }
        throw new RuntimeException("Le restaurant n'existe pas");
    }

    private static void fourthRequestAffichage(List<Restaurant> lst, Person person) {
        lst.sort(Restaurant::compareTo);

        System.out.println("Les restaurants proposées pour " + person);
        for (int c = 0; c < (Math.min(lst.size(), 3)); c++) {
            System.out.println("   - " + lst.get(c).getName());
        }
    }

    public static void createDataBase(Bdd bdd) {
        bdd.deleteDataBase();
        PersonBdd personBdd = new PersonBdd(bdd);
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