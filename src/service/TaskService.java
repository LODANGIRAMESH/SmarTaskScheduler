package service;

import java.util.List;
import java.util.PriorityQueue;

import dao.TaskDAO;
import model.Task;

public class TaskService {
	private PriorityQueue<Task> queue;
	private TaskDAO dao;
	public TaskService() {
		dao=new TaskDAO();
		queue=new PriorityQueue<>(
				(t1,t2)->{
					if(t1.getPriority()!=t2.getPriority())
						return Integer.compare(t1.getPriority(), t2.getPriority());
					return t1.getDeadline().compareTo(t2.getDeadline());
				}
				);
		loadTasks();
	}
	private void loadTasks() {
		List<Task> tasks=dao.getAllTasks();
		queue.addAll(tasks);
	}
	public void addTask(Task task) {
		dao.addTask(task);
		queue.offer(task);
	}
	public PriorityQueue<Task> getQueue(){
		return queue;
	}
	public void deleteTask(int id) {
		dao.deleteTask(id);
		queue.removeIf(t->t.getId()==id);
	}
}
