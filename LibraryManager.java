import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibraryManager {

  public static void trackOverdueItems() {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "SELECT * FROM transactions WHERE status = 'issued' AND return_date < CURDATE()")) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          int transactionId = resultSet.getInt("id");
          int bookId = resultSet.getInt("book_id");
          // Perform actions for overdue items, such as sending notifications
          // or marking them as overdue in the system
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void addUser(User user) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "INSERT INTO users (name, email, phone) VALUES (?, ?, ?)")) {
      preparedStatement.setString(1, user.getName());
      preparedStatement.setString(2, user.getEmail());
      preparedStatement.setString(3, user.getPhone());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void issueBook(int userId, int bookId) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "INSERT INTO transactions (book_id, user_id, issue_date) VALUES (?, ?, NOW())")) {
      preparedStatement.setInt(1, bookId);
      preparedStatement.setInt(2, userId);
      preparedStatement.executeUpdate();
      decrementBookQuantity(bookId);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void returnBook(int transactionId) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "UPDATE transactions SET status = 'returned', return_date = NOW() WHERE id = ?")) {
      preparedStatement.setInt(1, transactionId);
      preparedStatement.executeUpdate();
      incrementBookQuantity(getBookIdFromTransaction(transactionId));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void addBook(Book book) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "INSERT INTO books (title, author, quantity, available_quantity) VALUES (?, ?, ?, ?)")) {
      preparedStatement.setString(1, book.getTitle());
      preparedStatement.setString(2, book.getAuthor());
      preparedStatement.setInt(3, book.getQuantity());
      preparedStatement.setInt(4, book.getAvailableQuantity());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void updateUser(User user) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "UPDATE users SET name = ?, email = ?, phone = ? WHERE id = ?")) {
      preparedStatement.setString(1, user.getName());
      preparedStatement.setString(2, user.getEmail());
      preparedStatement.setString(3, user.getPhone());
      preparedStatement.setInt(4, user.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void updateBook(Book book) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "UPDATE books SET title = ?, author = ?, quantity = ?, available_quantity = ? WHERE id = ?")) {
      preparedStatement.setString(1, book.getTitle());
      preparedStatement.setString(2, book.getAuthor());
      preparedStatement.setInt(3, book.getQuantity());
      preparedStatement.setInt(4, book.getAvailableQuantity());
      preparedStatement.setInt(5, book.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void deleteBook(int bookId) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "DELETE FROM books WHERE id = ?")) {
      preparedStatement.setInt(1, bookId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void deleteUser(int userId) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "DELETE FROM users WHERE id = ?")) {
      preparedStatement.setInt(1, userId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void decrementBookQuantity(int bookId) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "UPDATE books SET available_quantity = available_quantity - 1 WHERE id = ?")) {
      preparedStatement.setInt(1, bookId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void incrementBookQuantity(int bookId) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "UPDATE books SET available_quantity = available_quantity + 1 WHERE id = ?")) {
      preparedStatement.setInt(1, bookId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static int getBookIdFromTransaction(int transactionId) {
    int bookId = -1;
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "SELECT book_id FROM transactions WHERE id = ?")) {
      preparedStatement.setInt(1, transactionId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          bookId = resultSet.getInt("book_id");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return bookId;
  }

  public static List<Book> getAllBooks() {
    List<Book> books = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM books")) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          Book book = new Book();
          book.setId(resultSet.getInt("id"));
          book.setTitle(resultSet.getString("title"));
          book.setAuthor(resultSet.getString("author"));
          book.setQuantity(resultSet.getInt("quantity"));
          book.setAvailableQuantity(resultSet.getInt("available_quantity"));
          books.add(book);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return books;
  }

  public static List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users")) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          User user = new User();
          user.setId(resultSet.getInt("id"));
          user.setName(resultSet.getString("name"));
          user.setEmail(resultSet.getString("email"));
          user.setPhone(resultSet.getString("phone"));
          users.add(user);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return users;
  }

}
