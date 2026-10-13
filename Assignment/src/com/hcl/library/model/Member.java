package com.hcl.library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Member {
    private final int id;
    private String name;
    private String email;
    private final List<LibraryItem> borrowedItems = new ArrayList<>();

    public Member(int id, String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }
        setEmail(email);
        this.id = id;
        this.name = name.trim();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @.");
        }
        this.email = email.trim();
    }

    public List<LibraryItem> getBorrowedItems() {
        return Collections.unmodifiableList(new ArrayList<>(borrowedItems));
    }

    public void addBorrowedItem(LibraryItem item) {
        borrowedItems.add(item);
    }

    public void removeBorrowedItem(LibraryItem item) {
        borrowedItems.remove(item);
    }

    @Override
    public String toString() {
        return "Member[" + id + ", " + name + ", " + email
                + ", borrowed=" + borrowedItems.size() + "]";
    }
}
