package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Task;

public class TaskDAO {
	public void addTask(Task task) {
		String sql="INSERT INTO tasks(title,priority,deadline,status)VALUES(?,?,?,?)";
		try(Connection conn=DBConnection.getConnection();
				PreparedStatement ps=conn.prepareStatement(sql)){
			ps.setString(1, task.getTitle());
			ps.setInt(2, task.getPriority());
			ps.setTimestamp(3, Timestamp.valueOf(task.getDeadline()));
			ps.setString(4,task.getStatus());
			ps.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public List<Task> getAllTasks(){
		List<Task> tasks=new ArrayList<>();
		String sql="SELECT * FROM tasks ORDER BY priority ASC,deadline ASC";
		try(Connection conn=DBConnection.getConnection();
				Statement stmt=conn.createStatement();
				ResultSet rs=stmt.executeQuery(sql)){
					while(rs.next()) {
						Task task=new Task(
								rs.getInt("id"),
								rs.getString("title"),
								rs.getInt("priority"),
								rs.getTimestamp("deadline").toLocalDateTime(),
								rs.getString("status")
								);
						tasks.add(task);
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
		return tasks;
	}
	public void deleteTask(int id) {
		String sql="DELETE FROM tasks WHERE id=?";
		try(Connection conn=DBConnection.getConnection();
				PreparedStatement ps=conn.prepareStatement(sql)){
					ps.setInt(1, id);
					ps.executeUpdate();
				}catch(SQLException e) {
					e.printStackTrace();
		}
	}
	public void updateStatus(int id,String status) {
		String sql="UPDATE tasks SET status=? WHERE id=?";
		try(Connection conn=DBConnection.getConnection();
				PreparedStatement ps=conn.prepareStatement(sql)){
			ps.setString(1, status);
			ps.setInt(2, id);
			ps.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
