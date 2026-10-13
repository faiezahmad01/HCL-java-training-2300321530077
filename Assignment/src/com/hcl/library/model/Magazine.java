package com.hcl.library.model;

public class Magazine extends LibraryItem {
    private int issueNumber;

    public Magazine(String title, int year, int issueNumber) {
        super(title, year);
        if (issueNumber < 1) throw new IllegalArgumentException("Issue number must be positive.");
        this.issueNumber = issueNumber;
    }

    public int getIssueNumber() { return issueNumber; }

    @Override
    public double lateFee(int daysLate) {
        return Math.max(0, daysLate) * 1.0;
    }

    @Override
    public String toString() {
        return super.toString() + " Issue=" + issueNumber;
    }
}
