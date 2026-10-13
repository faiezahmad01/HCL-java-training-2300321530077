package com.hcl.library;

import com.hcl.library.model.Book;
import com.hcl.library.model.DVD;
import com.hcl.library.model.LibraryItem;
import com.hcl.library.model.Magazine;
import com.hcl.library.model.Member;
import com.hcl.library.service.LibraryService;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final LibraryService service = new LibraryService();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            showMenu();
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1": addItem(); break;
                    case "2": registerMember(); break;
                    case "3": borrowItem(); break;
                    case "4": returnItem(); break;
                    case "5": joinWaitlist(); break;
                    case "6": listItems(false); break;
                    case "7": listItems(true); break;
                    case "8": searchItems(); break;
                    case "9": exportReport(); break;
                    case "10": showStatistics(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            } catch (LibraryException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Please enter valid input.");
            }
            System.out.println();
        }

        System.out.println("Goodbye!");
        sc.close();
    }

    private static void showMenu() {
        System.out.println("===== LIBRARY MENU =====");
        System.out.println("1. Add item");
        System.out.println("2. Register member");
        System.out.println("3. Borrow item");
        System.out.println("4. Return item");
        System.out.println("5. Join waitlist");
        System.out.println("6. List items (title order)");
        System.out.println("7. List items (year descending)");
        System.out.println("8. Search");
        System.out.println("9. Export report");
        System.out.println("10. Show statistics");
        System.out.println("0. Exit");
    }

    private static void addItem() {
        System.out.println("1. Book  2. Magazine  3. DVD");
        int type = readInt("Type: ");
        String title = readText("Title: ");
        int year = readInt("Year: ");

        LibraryItem item;
        if (type == 1) {
            String author = readText("Author: ");
            String isbn = readText("ISBN: ");
            item = new Book(title, year, author, isbn);
        } else if (type == 2) {
            int issue = readInt("Issue number: ");
            item = new Magazine(title, year, issue);
        } else if (type == 3) {
            int duration = readInt("Duration in minutes: ");
            item = new DVD(title, year, duration);
        } else {
            System.out.println("Invalid item type.");
            return;
        }

        service.addItem(item);
        System.out.println("Added: " + item);
    }

    private static void registerMember() {
        int id = readInt("Member id: ");
        String name = readText("Name: ");
        String email = readText("Email: ");
        service.registerMember(new Member(id, name, email));
        System.out.println("Member registered.");
    }

    private static void borrowItem() {
        int memberId = readInt("Member id: ");
        int itemId = readInt("Item id: ");
        service.borrowItem(memberId, itemId);
        System.out.println("Item issued successfully.");
    }

    private static void returnItem() {
        int itemId = readInt("Item id: ");
        int daysLate = readInt("Late by (days): ");
        double fee = service.returnItem(itemId, daysLate);
        System.out.println("Late fee: " + fee);
        System.out.println("Item returned.");
    }

    private static void joinWaitlist() {
        int memberId = readInt("Member id: ");
        int itemId = readInt("Item id: ");
        service.joinWaitlist(memberId, itemId);
    }

    private static void listItems(boolean byYear) {
        List<LibraryItem> items = byYear
                ? service.sortByYearDescending() : service.sortByTitle();
        if (items.isEmpty()) {
            System.out.println("No items found.");
            return;
        }
        for (LibraryItem item : items) System.out.println(item);
    }

    private static void searchItems() {
        String title = readText("Enter title or part of title: ");
        List<LibraryItem> result = service.searchByTitle(title);
        if (result.isEmpty()) System.out.println("No matching items found.");
        else for (LibraryItem item : result) System.out.println(item);
    }

    private static void exportReport() {
        String path = readText("File path (example: report.txt): ");
        service.exportReport(path);
        System.out.println("Report exported to " + path);
    }

    private static void showStatistics() {
        System.out.println(service.countItemsByType());
    }

    private static String readText(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    private static int readInt(String message) {
        System.out.print(message);
        return Integer.parseInt(sc.nextLine().trim());
    }
}
