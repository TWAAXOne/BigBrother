import Dao.*;

public class Main {
    public static void main(String[] args) {
        // Lancement de la création de la base donnée.
        createDataBase();
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
