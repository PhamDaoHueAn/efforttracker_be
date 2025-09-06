package com.efforttracker.service;

import com.efforttracker.exception.ResourceNotFoundException;
import com.efforttracker.model.dto.TaskDtos;
import com.efforttracker.model.entity.Task;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.model.entity.User;
import com.efforttracker.model.mapper.TaskMapper;
import com.efforttracker.repository.TaskRepository;
import com.efforttracker.repository.TimeEntryRepository;
import com.efforttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TimeEntryRepository timeEntryRepository;

    // Lấy tất cả task
    public List<TaskDtos.TaskResponse> getAll() {
        return taskRepository.findAll().stream()
                .map(TaskMapper::toTaskResponse)
                .toList();
    }

    // Lấy task theo id
    public TaskDtos.TaskResponse getById(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Task với id = " + id));
        return TaskMapper.toTaskResponse(task);
    }

    // Tạo task mới
    public TaskDtos.TaskResponse create(TaskDtos.CreateTaskRequest req) {
        Task task = new Task();
        task.setName(req.getName());
        task.setDescription(req.getDescription());
        task.setStatus(req.getStatus());
        task.setStartDate(req.getStartDate());
        task.setDueDate(req.getDueDate());

        Task saved = taskRepository.save(task);
        return TaskMapper.toTaskResponse(saved);
    }

    // Tạo task kèm time entries
    public TaskDtos.TaskResponse createWithEntries(TaskDtos.CreateTaskWithEntriesRequest req, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với id = " + userId));

        Task task = new Task();
        task.setName(req.getName());
        task.setDescription(req.getDescription());
        task.setStatus(req.getStatus());
        task.setStartDate(req.getStartDate());
        task.setDueDate(req.getDueDate());

        Task savedTask = taskRepository.save(task);

        if (req.getEntries() != null && !req.getEntries().isEmpty()) {
            List<TimeEntry> entries = req.getEntries().stream()
                    .map(e -> {
                        TimeEntry te = new TimeEntry();
                        te.setTask(savedTask);
                        te.setUser(user);
                        te.setDate(e.getDate());
                        te.setHours(e.getHours());
                        te.setDescription(e.getDescription());
                        return te;
                    }).toList();

            timeEntryRepository.saveAll(entries);
        }

        return TaskMapper.toTaskResponse(savedTask);
    }

    // Cập nhật task
    public TaskDtos.TaskResponse update(String id, TaskDtos.UpdateTaskRequest req) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Task với id = " + id));

        if (req.getName() != null) task.setName(req.getName());
        if (req.getDescription() != null) task.setDescription(req.getDescription());
        if (req.getStatus() != null) task.setStatus(req.getStatus());
        if (req.getStartDate() != null) task.setStartDate(req.getStartDate());
        if (req.getDueDate() != null) task.setDueDate(req.getDueDate());

        Task updated = taskRepository.save(task);
        return TaskMapper.toTaskResponse(updated);
    }

    // Xóa task
    public void delete(String id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy Task với id = " + id);
        }
        taskRepository.deleteById(id);
    }

    // Lấy danh sách task theo userId
    public List<TaskDtos.TaskResponse> getByUserId(String userId) {
        // nếu muốn kiểm tra user có tồn tại
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Không tìm thấy User với id = " + userId);
        }

        List<Task> tasks = taskRepository.findByUserIdWithAll(userId);

        return tasks.stream()
                .map(TaskMapper::toTaskResponse)
                .toList();
    }

}
