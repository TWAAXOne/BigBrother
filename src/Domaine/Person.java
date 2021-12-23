package Domaine;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String firstName;
    private String lastName;
    private String birthDate;
    private String address;
    private String gender;
    private String phone;
    private String email;
    private List<Person> amis;
    private List<Activity> activity;
    private Company company;

    public Person(String firstName, String lastName, String birthDate, String address, String gender, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.amis = new ArrayList<>();
        this.activity = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() { return lastName;}
    public String getBirthDate() {
        return birthDate;
    }
    public String getAddress() {
        return address;
    }
    public String getGender() {
        return gender;
    }
    public String getPhone() {
        return phone;
    }
    public String getEmail() { return email; }
    public Person getFirstFriend() {
        return this.amis.get(0);
    }
    public Boolean hasFriend() {
        return this.amis.size() != 0;
    }
    public Company getCompany() { return this.company; }

    public void addAmis(Person amis) {
        this.amis.add(amis);
    }

    public void addActivity(Activity activity) {
        this.activity.add(activity);
    }
    public void setCompany(Company company) { this.company = company; }

    @Override
    public String toString() {
        return firstName + ' ' + lastName;
    }

    public boolean hasActivity() {
        return this.activity.size() != 0;
    }

    public boolean hasCompany() {
        return this.company != null;
    }
}
