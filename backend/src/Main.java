import static spark.Spark.*;
import com.google.gson.Gson;
import java.sql.*;
import java.util.*;

public class Main {

    static Gson gson = new Gson();

    public static void main(String[] args) {

        port(8080);

        // ---- CORS ----
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "*");
            res.header("Access-Control-Allow-Headers", "*");
        });

        options("/*", (req, res) -> "OK");

        // ---- HEALTH CHECK ----
        get("/health", (req, res) -> "Backend running");

        // ---- ADD TASK ----
        post("/tasks", (req, res) -> {
            Task t = gson.fromJson(req.body(), Task.class);

            try (Connection con = DB.getConnection()) {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO tasks(title, category, priority, dueDate, estimatedMinutes, status) VALUES(?,?,?,?,?,?)"
                );
                ps.setString(1, t.title);
                ps.setString(2, t.category);
                ps.setString(3, t.priority);
                ps.setString(4, t.dueDate);
                ps.setInt(5, t.estimatedMinutes);
                ps.setString(6, "PENDING");
                ps.executeUpdate();
            }

            res.status(201);
            return "Task added";
        });

        // ---- GET TASKS ----
        get("/tasks", (req, res) -> {
            List<Task> tasks = new ArrayList<>();

            try (Connection con = DB.getConnection()) {
                ResultSet rs = con.createStatement()
                        .executeQuery("SELECT * FROM tasks");

                while (rs.next()) {
                    Task t = new Task();
                    t.id = rs.getInt("id");
                    t.title = rs.getString("title");
                    t.category = rs.getString("category");
                    t.priority = rs.getString("priority");
                    t.status = rs.getString("status");
                    t.dueDate = rs.getString("dueDate");
                    t.estimatedMinutes = rs.getInt("estimatedMinutes");
                    t.completedAt = rs.getString("completedAt");
                    tasks.add(t);
                }
            }

            res.type("application/json");
            return gson.toJson(tasks);
        });

        // ---- MARK TASK AS COMPLETED ----
        post("/tasks/:id/complete", (req, res) -> {
            int taskId = Integer.parseInt(req.params(":id"));

            try (Connection con = DB.getConnection()) {

                PreparedStatement ps1 = con.prepareStatement(
                    "UPDATE tasks SET status = ?, completedAt = datetime('now') WHERE id = ?"
                );
                ps1.setString(1, "COMPLETED");
                ps1.setInt(2, taskId);
                ps1.executeUpdate();

                PreparedStatement ps2 = con.prepareStatement(
                    "INSERT INTO task_logs(task_id, action, timestamp) VALUES(?,?,datetime('now'))"
                );
                ps2.setInt(1, taskId);
                ps2.setString(2, "COMPLETED");
                ps2.executeUpdate();
            }

            return "Task completed";
        });

        System.out.println("🔥 TaskFlow+ Backend running on http://localhost:8080");
    }
}
