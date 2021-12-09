import Dao.CompanyBdd;
import Dao.PersonBdd;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        /*
        Bdd bdd = new Bdd();
        bdd.connect();
        Result res = bdd.run("MATCH (p:Person) RETURN p");
        while (res.hasNext()) {
            Record rec = res.next();
            System.out.println(rec.get("p").get("name"));
        }

         */

        PersonBdd.createUser();
        CompanyBdd.createBdd();
        PersonBdd.createRelationFriend();

    }
}
