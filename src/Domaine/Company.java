package Domaine;

import java.util.ArrayList;
import java.util.List;

public class Company {
    private String company_name;
    private String industry;
    private String stock_market;
    private String sector;
    private String address;
    private List<Person> listOfWorker;

    public Company(String company_name, String industry, String stock_market, String sector, String address) {
        this.company_name = company_name;
        this.industry = industry;
        this.stock_market = stock_market;
        this.sector = sector;
        this.address = address;
        this.listOfWorker = new ArrayList<>();
    }

    public void addListOfWorker(Person person) {
        this.listOfWorker.add(person);
    }

    public Person getFirstWorker() {
        return this.listOfWorker.get(0);
    }
    public Boolean hasWorker() {
        return this.listOfWorker.size() > 0;
    }

    @Override
    public String toString() {
        return this.company_name;
    }
}
