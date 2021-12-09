package Dao;

import org.neo4j.driver.Result;

import java.util.List;

public class CompanyBdd {
    private static final String FILENAME_LST_COMPANY = "company_data.csv";
    public static List<String[]> getListCompany() { return Bdd.getList(FILENAME_LST_COMPANY); }

    public static void deleteCompanyBdd() {
        Bdd bdd = Bdd.getInstance();
        bdd.connect();
        System.out.println("Suppression des compagnies");
        bdd.run("MATCH (c:Company) DELETE c");
    }
    public static void createBdd() {
        deleteCompanyBdd();
        Bdd bdd = Bdd.getInstance();
        bdd.connect();
        System.out.println("Création des compagnies");
        for (String[] data : getListCompany()) {
            Result res = bdd.run("CREATE (p:Company{" +
                    "company_name:'" + data[0]+ "'" +
                    ", industry:'" + data[1] + "'" +
                    ", stock_market:'" + data[2] + "'" +
                    ", sector:'" + data[3] + "'" +
                    ", address:'" + data[4] + "'" +
                    "})");
        }
        bdd.close();
        System.out.println("Fin créations compagnies");
    }

}
