package ims.model;

public class Category {
    private int id;
    private String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Operations
    public void addCategory() {
        System.out.println("Category added: " + name);
    }

    public void updateCategory(String newName) {
        this.name = newName;
        System.out.println("Category updated to: " + newName);
    }

    public void deleteCategory() {
        System.out.println("Category deleted: " + name);
    }
}
