import java.sql.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.DayOfWeek;

public class TaskChecker {

    static final String DB_URL = "jdbc:mysql://localhost:3306/todo_list_db"; // your DB name
    static final String DB_USER = "root"; // default user in XAMPP
    static final String DB_PASS = ""; // empty password if you're using XAMPP default

    public static void main(String[] args) {
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek(); // e.g., MONDAY
        String today = currentDay.toString().substring(0, 1) + currentDay.toString().substring(1).toLowerCase(); // e.g., "Monday"

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM timetable WHERE day = ? ORDER BY start_time";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();

            LocalTime now = LocalTime.now();
            String ongoing = null;
            String upcoming = null;

            while (rs.next()) {
                Time start = rs.getTime("start_time");
                Time end = rs.getTime("end_time");
                String task = rs.getString("task_name");

                if (now.isAfter(start.toLocalTime()) && now.isBefore(end.toLocalTime())) {
                    ongoing = task;
                } else if (now.isBefore(start.toLocalTime()) && upcoming == null) {
                    upcoming = task;
                }
            }

            System.out.println("🟢 On going: " + (ongoing != null ? ongoing : "No task right now"));
            System.out.println("🔜 Up comming: " + (upcoming != null ? upcoming : "Nothing coming up"));

        } catch (SQLException e) {
            System.out.println("⚠️ Database connection failed!");
            e.printStackTrace();
        }
    }
}
