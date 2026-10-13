package com.hcl.library.service;

import com.hcl.library.model.Book;
import com.hcl.library.model.LibraryItem;
import com.hcl.library.model.Magazine;
import com.hcl.library.model.Member;
import com.hcl.library.repository.InMemoryRepository;
import com.hcl.library.util.SearchCache;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class LibraryService {
    private final InMemoryRepository<LibraryItem, Integer> items =
            new InMemoryRepository<>(LibraryItem::getId);
    private final InMemoryRepository<Member, Integer> members =
            new InMemoryRepository<>(Member::getId);
    private final Set<String> isbns = new HashSet<>();
    private final Map<Integer, Queue<Member>> waitlists = new HashMap<>();
    private final SearchCache searchCache = new SearchCache();

    public void addItem(LibraryItem item) {
        if (item instanceof Book) {
            String isbn = ((Book) item).getIsbn();
            if (isbns.contains(isbn)) {
                throw new LibraryException("A book with this ISBN already exists.");
            }
            isbns.add(isbn);
        }
        items.save(item);
    }

    public void registerMember(Member member) {
        members.save(member);
    }

    public List<LibraryItem> getItems() {
        return items.findAll();
    }

    public List<Member> getMembers() {
        return members.findAll();
    }

    public LibraryItem findItem(int id) {
        return items.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item " + id + " was not found."));
    }

    public Member findMember(int id) {
        return members.findById(id)
                .orElseThrow(() -> new LibraryException("Member " + id + " was not found."));
    }

    public void borrowItem(int memberId, int itemId) {
        Member member = findMember(memberId);
        LibraryItem item = findItem(itemId);

        if (!item.isAvailable()) {
            throw new ItemNotAvailableException("Item " + itemId + " is not available.");
        }
        if (member.getBorrowedItems().size() >= 3) {
            throw new BorrowLimitExceededException("A member cannot borrow more than 3 items.");
        }

        item.borrow();
        member.addBorrowedItem(item);
    }

    public double returnItem(int itemId, int daysLate) {
        LibraryItem item = findItem(itemId);
        Member borrower = null;

        for (Member member : members.findAll()) {
            if (member.getBorrowedItems().contains(item)) {
                borrower = member;
                break;
            }
        }
        if (borrower == null) {
            throw new LibraryException("This item is not currently borrowed.");
        }

        double fee = item.lateFee(daysLate);
        borrower.removeBorrowedItem(item);
        item.giveBack();

        Queue<Member> queue = waitlists.get(itemId);
        if (queue != null && !queue.isEmpty()) {
            Member next = queue.poll();
            System.out.println("Notification: Member " + next.getId() + " (" + next.getName()
                    + ") is next in the waitlist for " + itemId + ".");
        }
        return fee;
    }

    public void joinWaitlist(int memberId, int itemId) {
        Member member = findMember(memberId);
        findItem(itemId);
        Queue<Member> queue = waitlists.computeIfAbsent(itemId, key -> new ArrayDeque<>());
        if (!queue.contains(member)) queue.offer(member);
        System.out.println("Member added to waitlist.");
    }

    public List<LibraryItem> sortByTitle() {
        List<LibraryItem> result = getItems();
        result.sort(Comparator.naturalOrder());
        return result;
    }

    public List<LibraryItem> sortByYearDescending() {
        List<LibraryItem> result = getItems();
        result.sort(Comparator.comparingInt(LibraryItem::getYear).reversed()
                .thenComparing(LibraryItem::getTitle, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public List<LibraryItem> searchByTitle(String title) {
        String cached = searchCache.get(title);
        if (cached != null) {
            System.out.println("Search result loaded from recent-search cache.");
        }
        List<LibraryItem> result = getItems().stream()
                .filter(item -> item.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
        searchCache.put(title, result.toString());
        return result;
    }

    public Map<String, Long> countItemsByType() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("Book", 0L);
        counts.put("Magazine", 0L);
        counts.put("DVD", 0L);
        for (LibraryItem item : getItems()) {
            String type = item instanceof Book ? "Book"
                    : item instanceof Magazine ? "Magazine" : "DVD";
            counts.merge(type, 1L, Long::sum);
        }
        return counts;
    }

    public void removeOldItems(int beforeYear) {
        items.findAll().removeIf(item -> {
            if (item.getYear() < beforeYear && item.isAvailable()) {
                if (item instanceof Book) isbns.remove(((Book) item).getIsbn());
                items.deleteById(item.getId());
                return true;
            }
            return false;
        });
    }

    public void exportReport(String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("LIBRARY REPORT");
            writer.newLine();
            for (LibraryItem item : getItems()) {
                writer.write(item.toString());
                writer.newLine();
            }
            writer.write("Item statistics: " + countItemsByType());
            writer.newLine();
        } catch (IOException e) {
            throw new LibraryException("Could not export report.", e);
        }
    }
}
