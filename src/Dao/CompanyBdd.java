package Dao;

import java.util.List;

public class CompanyBdd{
    private static final String FILENAME_LST_COMPANY = "company_data.csv";
    private Bdd bdd;

    public CompanyBdd(Bdd bdd) {
        this.bdd = bdd;
    }

    public static List<String[]> getListCompany() { return Bdd.getList(FILENAME_LST_COMPANY); }

    public void deleteRelationCompany() {
        bdd.run("MATCH p=()-[r:TRAVAILLE]->() delete r");
    }

    public void deleteCompanyBdd() {
        System.out.println("Suppression des compagnies");
        bdd.run("MATCH (c:Company) DELETE c");
    }
    public void createBddActivity() {
        deleteRelationCompany();
        deleteCompanyBdd();
        System.out.println("Création des compagnies");
        for (int c = 0; c < getListCompany().size(); c++) {
            String[] data = getListCompany().get(c);
            bdd.run("CREATE (p:Company{" +
                    "company_name:'" + data[0]+ "'" +
                    ", industry:'" + data[1] + "'" +
                    ", stock_market:'" + data[2] + "'" +
                    ", sector:'" + data[3] + "'" +
                    ", address:'" + data[4] + "'" +
                    "})");
        }
        System.out.println("Fin créations compagnies");
    }

}
