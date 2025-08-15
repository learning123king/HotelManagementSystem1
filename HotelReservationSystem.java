import java.sql.*;
import java.util.*;

public class HotelReservationSystem {

    // DB credentials
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USER = "root";
    private static final String PASSWORD = "pass123"; 

    // JDBC connection helper
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Add reservation
    public static void addReservation(String guestName, int roomNumber, String checkIn, String checkOut) {
        String findRoom = "SELECT id FROM rooms WHERE room_number = ?";
        String insert = "INSERT INTO reservations (guest_name, room_id, check_in, check_out) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement psFind = conn.prepareStatement(findRoom)) {

            psFind.setInt(1, roomNumber);
            ResultSet rs = psFind.executeQuery();
            if (!rs.next()) {
                System.out.println("⚠ Room number " + roomNumber + " not found!");
                return;
            }
            int roomId = rs.getInt("id");

            // Check if room is already booked in given dates
            String checkAvailability = "SELECT * FROM reservations WHERE room_id = ? AND status='Booked' AND " +
                    "( (check_in <= ? AND check_out > ?) OR (check_in < ? AND check_out >= ?) OR (check_in >= ? AND check_out <= ?) )";
            try (PreparedStatement psCheck = conn.prepareStatement(checkAvailability)) {
                psCheck.setInt(1, roomId);
                psCheck.setString(2, checkIn);
                psCheck.setString(3, checkIn);
                psCheck.setString(4, checkOut);
                psCheck.setString(5, checkOut);
                psCheck.setString(6, checkIn);
                psCheck.setString(7, checkOut);
                ResultSet rsCheck = psCheck.executeQuery();
                if (rsCheck.next()) {
                    System.out.println("Room already booked for given dates!");
                    return;
                }
            }

            // Insert reservation
            try (PreparedStatement psInsert = conn.prepareStatement(insert)) {
                psInsert.setString(1, guestName);
                psInsert.setInt(2, roomId);
                psInsert.setString(3, checkIn);
                psInsert.setString(4, checkOut);
                psInsert.executeUpdate();
                System.out.println("Reservation added successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all reservations
    public static void viewReservations() {
        String sql = "SELECT r.id, r.guest_name, rm.room_number, r.check_in, r.check_out, r.status " +
                     "FROM reservations r JOIN rooms rm ON r.room_id = rm.id ORDER BY r.id";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n---- Reservations ----");
            while (rs.next()) {
                System.out.printf("ID: %d | Guest: %s | Room: %d | %s to %s | Status: %s\n",
                        rs.getInt("id"),
                        rs.getString("guest_name"),
                        rs.getInt("room_number"),
                        rs.getString("check_in"),
                        rs.getString("check_out"),
                        rs.getString("status"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cancel reservation
    public static void cancelReservation(int id) {
        String sql = "UPDATE reservations SET status='Cancelled' WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Reservation cancelled successfully!");
            } else {
                System.out.println(" Reservation not found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View rooms
    public static void viewRooms() {
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n---- Rooms ----");
            while (rs.next()) {
                System.out.printf("ID: %d | Room No: %d | Type: %s | Price: %.2f\n",
                        rs.getInt("id"),
                        rs.getInt("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price_per_night"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Main menu
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== HOTEL RESERVATION SYSTEM =====");
            System.out.println("1. View Rooms");
            System.out.println("2. Add Reservation");
            System.out.println("3. View Reservations");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewRooms();
                    break;
                case 2:
                    System.out.print("Guest Name: ");
                    String name = sc.nextLine();
                    System.out.print("Room Number: ");
                    int room = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Check-In (YYYY-MM-DD): ");
                    String in = sc.nextLine();
                    System.out.print("Check-Out (YYYY-MM-DD): ");
                    String out = sc.nextLine();
                    addReservation(name, room, in, out);
                    break;
                case 3:
                    viewReservations();
                    break;
                case 4:
                    System.out.print("Reservation ID to cancel: ");
                    int id = sc.nextInt();
                    cancelReservation(id);
                    break;
                case 5:
                    System.out.println(" Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
