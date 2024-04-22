import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Adding a book
        Book book1 = new Book();
        book1.setTitle("The Great Gatsby");
        book1.setAuthor("F. Scott Fitzgerald");
        book1.setQuantity(5);
        book1.setAvailableQuantity(5);

        BookManager.addBook(book1);

        // Adding a user
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setPhone("123-456-7890");

        UserManager.addUser(user1);

        // Issuing a book to a user
        LibraryManager.issueBook(user1.getId(), book1.getId());

        // Returning a book
        List<Transaction> transactions = TransactionManager.getAllTransactions();
        for (Transaction transaction : transactions) {
            if (transaction.getStatus().equals("issued")) {
                LibraryManager.returnBook(transaction.getId());
                break; // Assuming we return only one book for demonstration
            }
        }

        // Tracking overdue items
        LibraryManager.trackOverdueItems();

        // Getting all books
        List<Book> books = BookManager.getAllBooks();
        for (Book book : books) {
            System.out.println(book.getTitle() + " by " + book.getAuthor() + " - Available Quantity: " + book.getAvailableQuantity());
        }

        // Getting all users
        List<User> users = UserManager.getAllUsers();
        for (User user : users) {
            System.out.println(user.getName() + " - " + user.getEmail() + " - " + user.getPhone());
        }
    }
}
