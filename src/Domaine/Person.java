package Domaine;

public class Person {
    private String firstName;
    private String lastName;
    private String birthDate;
    private String address;
    private String gender;
    private String phone;
    private String email;

    public Person(String firstName, String lastName, String birthDate, String address, String gender, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

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

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return firstName + ' ' + lastName;
    }
}
