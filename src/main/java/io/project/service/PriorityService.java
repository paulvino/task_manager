package io.project.service;

import io.project.dto.priorityDto.PriorityCreateDTO;
import io.project.dto.priorityDto.PriorityDTO;
import io.project.dto.priorityDto.PriorityUpdateDTO;
import io.project.exception.ResourceNotFoundException;
import io.project.mapper.PriorityMapper;
import io.project.repository.PriorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriorityService {
    private final PriorityRepository priorityRepository;
    private final PriorityMapper priorityMapper;

    public List<PriorityDTO> getAll() {
        var priority = priorityRepository.findAll();
        return priority.stream()
                .map(priorityMapper::map)
                .toList();
    }

    public PriorityDTO findById(Long id) {
        var priority = priorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Priority with id: " + id + " not found"));

        return priorityMapper.map(priority);
    }

    public PriorityDTO create(PriorityCreateDTO priorityData) {
        var priority = priorityMapper.map(priorityData);
        priorityRepository.save(priority);

        return priorityMapper.map(priority);
    }

    public PriorityDTO update(PriorityUpdateDTO priorityData, Long id) {
        var priority = priorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Priority with id: " + id + " not found"));

        priorityMapper.update(priorityData, priority);
        priorityRepository.save(priority);

        return priorityMapper.map(priority);
    }

    public void delete(Long id) {
        priorityRepository.deleteById(id);
    }
}
