package ims.model;

import java.util.ArrayList;
import java.util.List;

public class ContactInfo {
    private List<String> contactNumbers;
    private List<String> emails;

    // Constructor
    public ContactInfo() {
        this.contactNumbers = new ArrayList<>();
        this.emails = new ArrayList<>();
    }

    // Add or remove contact number
    public void addNumber(String number) {
        contactNumbers.add(number);
        System.out.println("Added contact number: " + number);
    }

    public void removeNumber(String number) {
        contactNumbers.remove(number);
        System.out.println("Removed contact number: " + number);
    }

    // Add or remove email
    public void addEmail(String email) {
        emails.add(email);
        System.out.println("Added email: " + email);
    }

    public void removeEmail(String email) {
        emails.remove(email);
        System.out.println("Removed email: " + email);
    }

    // Getters
    public List<String> getContactNumbers() { return contactNumbers; }
    public List<String> getEmails() { return emails; }

    @Override
    public String toString() {
        return "Phones: " + contactNumbers + ", Emails: " + emails;
    }
}
