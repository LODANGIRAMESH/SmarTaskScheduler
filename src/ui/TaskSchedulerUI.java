package ui;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

import model.Task;
import service.TaskService;

public class TaskSchedulerUI extends JFrame {
    private TaskService service;
    private DefaultListModel<Task> listModel;
    private JList<Task> taskList;
    private Timer reminderTimer;

    public TaskSchedulerUI() {
        service = new TaskService();
        listModel = new DefaultListModel<>();
        service.getQueue().forEach(listModel::addElement);

        taskList = new JList<>(listModel);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskList.setBackground(new Color(245, 245, 245));
        taskList.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane scrollPane = new JScrollPane(taskList);

        
        JButton addBtn = createStyledButton("➕ Add Task", new Color(0, 153, 76));
        JButton deleteBtn = createStyledButton("🗑 Delete Task", new Color(220, 53, 69));
        JButton updateBtn = createStyledButton("✏️ Update Task", new Color(255, 193, 7));
        JButton reminderBtn = createStyledButton("🔔 Start Reminder", new Color(0, 123, 255));

        addBtn.addActionListener(e -> addTaskDialog());
        deleteBtn.addActionListener(e -> deleteSelectedTask());
        updateBtn.addActionListener(e -> updateTaskDialog());
        reminderBtn.addActionListener(e -> startReminders());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 230, 250));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(reminderBtn);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setTitle("✨ Smart Task Scheduler");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addTaskDialog() {
        JTextField titleField = new JTextField();
        JTextField priorityField = new JTextField();
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner deadlineSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(deadlineSpinner, "yyyy-MM-dd HH:mm");
        deadlineSpinner.setEditor(dateEditor);

        Object[] message = {
            "Title:", titleField,
            "Priority (1 = High):", priorityField,
            "Deadline:", deadlineSpinner
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText();
                int priority = Integer.parseInt(priorityField.getText());
                java.util.Date selectedDate = (java.util.Date) deadlineSpinner.getValue();
                LocalDateTime deadline = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                Task task = new Task(title, priority, deadline);
                service.addTask(task);
                listModel.addElement(task);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        }
    }

    private void updateTaskDialog() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a task to update.");
            return;
        }

        JTextField titleField = new JTextField(selected.getTitle());
        JTextField priorityField = new JTextField(String.valueOf(selected.getPriority()));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner deadlineSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(deadlineSpinner, "yyyy-MM-dd HH:mm");
        deadlineSpinner.setEditor(dateEditor);
        deadlineSpinner.setValue(java.util.Date.from(selected.getDeadline().atZone(ZoneId.systemDefault()).toInstant()));

        Object[] message = {
            "Title:", titleField,
            "Priority (1=High):", priorityField,
            "Deadline:", deadlineSpinner
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                selected.setStatus("Pending");
                selected = new Task(
                    selected.getId(),
                    titleField.getText(),
                    Integer.parseInt(priorityField.getText()),
                    ((java.util.Date) deadlineSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    "Pending"
                );
                service.deleteTask(selected.getId());
                service.addTask(selected);

                listModel.clear();
                service.getQueue().forEach(listModel::addElement);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
            }
        }
    }

    private void deleteSelectedTask() {
        Task selected = taskList.getSelectedValue();
        if (selected != null) {
            service.deleteTask(selected.getId());
            listModel.removeElement(selected);
        }
    }

    private void startReminders() {
        if (reminderTimer != null) {
            reminderTimer.cancel();
        }

        reminderTimer = new Timer(true);
        reminderTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < listModel.size(); i++) {
                    Task task = listModel.getElementAt(i);
                    if (task.getDeadline().isBefore(LocalDateTime.now().plusMinutes(5))
                            && task.getDeadline().isAfter(LocalDateTime.now())) {
                        SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(null,
                                "⏰ Reminder: Task '" + task.getTitle() + "' is due soon!",
                                "Task Reminder",
                                JOptionPane.INFORMATION_MESSAGE)
                        );
                    }
                }
            }
        }, 0, 60000); 
        JOptionPane.showMessageDialog(this, "Reminder started successfully!");
    }
}
