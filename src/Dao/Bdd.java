package Dao;
import org.neo4j.driver.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bdd {
    private Driver driver;
    private Session session;

    private Bdd() { }

    private static class BddHolder{
        private static final Bdd uniqueInstance = new Bdd();
    }

    public static Bdd getInstance(){
        return BddHolder.uniqueInstance;
    }

    public void connect() {
        driver = GraphDatabase.driver("neo4j+s://d02c7c37.databases.neo4j.io", AuthTokens.basic("neo4j", "DVzcvsyY872krNWP-XTYIphLfvB5i8hvHVO2U2BczLo"));
        session = driver.session();
    }

    public void close() {
        session.close();
        driver.close();
    }

    public static List<String[]> getList(String fileName) {
        try {
            BufferedReader reader  = new BufferedReader(new FileReader(fileName));
            List<String[]> lst = new ArrayList<>();
            String ligne = reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                lst.add(ligne.split(","));
            }
            reader.close();
            return lst;
        } catch (IOException e) {  e.printStackTrace(); return null; }
    }

    public Result run(String insrt) {
        return session.run(insrt);
    }
}
