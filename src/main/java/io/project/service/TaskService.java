package io.project.service;

import io.project.dto.taskDto.TaskCreateDTO;
import io.project.dto.taskDto.TaskDTO;
import io.project.dto.taskDto.TaskParamsDTO;
import io.project.dto.taskDto.TaskUpdateDTO;
import io.project.exception.ResourceNotFoundException;
import io.project.mapper.TaskMapper;
import io.project.mapper.UserMapper;
import io.project.repository.TaskRepository;
import io.project.util.TaskUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    @Autowired
    private final TaskRepository taskRepository;

    @Autowired
    private final TaskMapper taskMapper;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final TaskUtils builder;

    @Autowired
    private final UserService userService;

    public List<TaskDTO> getAll(TaskParamsDTO params) {
        var specification = builder.build(params);
        return taskRepository.findAll(specification).stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        return taskMapper.map(task);
    }

    public TaskDTO create(TaskCreateDTO taskData) {
        var task = taskMapper.map(taskData);

        var currentUser = userService.getCurrentUser();

        task.setAuthor(currentUser);

        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO update(TaskUpdateDTO taskData, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        taskMapper.update(taskData, task);
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
