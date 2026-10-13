package com.hcl.library.model;

public abstract class LibraryItem implements Borrowable, Comparable<LibraryItem> {
    private static int nextId = 1001;

    private final int id;
    private String title;
    private int year;
    private boolean available;

    public LibraryItem(String title, int year) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be blank.");
        }
        int currentYear = java.time.Year.now().getValue();
        if (year < 1900 || year > currentYear) {
            throw new IllegalArgumentException("Year must be between 1900 and " + currentYear + ".");
        }
        this.id = nextId++;
        this.title = title.trim();
        this.year = year;
        this.available = true;
    }

    public abstract double lateFee(int daysLate);

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getYear() { return year; }
    public boolean isAvailable() { return available; }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be blank.");
        }
        this.title = title.trim();
    }

    public void borrow() {
        if (!available) throw new IllegalStateException("Item is already issued.");
        available = false;
    }

    public void giveBack() {
        available = true;
    }

    @Override
    public int compareTo(LibraryItem other) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.title, other.title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LibraryItem)) return false;
        LibraryItem other = (LibraryItem) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + ", " + title + ", " + year
                + ", " + (available ? "Available" : "Issued") + "]";
    }
}
