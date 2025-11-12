package ims.model;

public class User {
    protected int userId;
    protected String userName;
    protected String role;
    protected String password;

    // Composition: User has Address and ContactInfo
    private Address address;
    private ContactInfo contactInfo;

    public User(int userId, String userName, String role, String password) {
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.password = password;
        this.address = new Address();
        this.contactInfo = new ContactInfo();
    }

    // Getters & Setters
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getRole() { return role; }
    public String getPassword() { return password; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public ContactInfo getContactInfo() { return contactInfo; }
    public void setContactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; }

    public boolean login(String inputUsername, String inputPassword) {
        return userName.equals(inputUsername) && password.equals(inputPassword);
    }

    public void showUserDetails() {
        System.out.println("User: " + userName);
        System.out.println("Role: " + role);
        System.out.println("Address: " + address);
        System.out.println("Contact: " + contactInfo);
    }
}
