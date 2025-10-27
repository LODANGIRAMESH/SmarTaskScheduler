package model;

import java.time.LocalDateTime;

public class Task {
	private int id;
	private String title;
	private int priority;
	private LocalDateTime deadline;
	private String status;
	public Task(int id, String title, int priority, LocalDateTime deadline, String status) {
		this.id = id;
		this.title = title;
		this.priority = priority;
		this.deadline = deadline;
		this.status = status;
	}
	public Task(String title, int priority, LocalDateTime deadline) {
		this.title = title;
		this.priority = priority;
		this.deadline = deadline;
		this.status = "pending";
	}
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public int getPriority() {
		return priority;
	}
	public LocalDateTime getDeadline() {
		return deadline;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "[" + priority + "]"+title+"(Due:"+deadline+"]";
	}
	
	
	
	
	
	
	
	
	
}
