package com.taskapp.todoapi.mapper;

import com.taskapp.todoapi.dto.TaskDto;
import com.taskapp.todoapi.entity.ListEntity;
import com.taskapp.todoapi.entity.TaskEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TaskEntityとTaskDTOの変換を行うマッパークラス
 */
public class TaskMapper {

    /**
     * TaskEntityをTaskDtoに変換
     * @param taskEntity 変換元のTaskEntity
     * @return 変換されたTaskDto
     */
    public static TaskDto toTaskDto(TaskEntity taskEntity) {
        if (taskEntity == null) {
            return null;
        }

        TaskDto dto = new TaskDto();
        dto.setId(taskEntity.getId());
        dto.setTitle(taskEntity.getTitle());
        dto.setDescription(taskEntity.getDescription());
        dto.setStatus(taskEntity.getStatus() != null ? taskEntity.getStatus().name() : null);
        dto.setPositionOrder(taskEntity.getPositionOrder());
        dto.setCreatedAt(taskEntity.getCreatedAt());
        dto.setUpdatedAt(taskEntity.getUpdatedAt());

        if (taskEntity.getList() != null) {
            dto.setListId(taskEntity.getList().getId());
            dto.setListTitle(taskEntity.getList().getTitle());
        }

        return dto;
    }

    /**
     * TaskDtoをTaskEntityに変換
     * @param taskDto 変換元のTaskDto
     * @return 変換されたTaskEntity
     */
    public static TaskEntity toTaskEntity(TaskDto taskDto) {
        if (taskDto == null) {
            return null;
        }

        TaskEntity entity = new TaskEntity();
        entity.setId(taskDto.getId());
        entity.setTitle(taskDto.getTitle());
        entity.setDescription(taskDto.getDescription());
        
        if (taskDto.getStatus() != null) {
            entity.setStatus(TaskEntity.TaskStatus.valueOf(taskDto.getStatus()));
        }
        
        entity.setPositionOrder(taskDto.getPositionOrder());
        entity.setCreatedAt(taskDto.getCreatedAt());
        entity.setUpdatedAt(taskDto.getUpdatedAt());

        // リストは別途設定する必要がある
        if (taskDto.getListId() != null) {
            ListEntity listEntity = new ListEntity();
            listEntity.setId(taskDto.getListId());
            entity.setList(listEntity);
        }

        return entity;
    }

    /**
     * TaskEntityのリストをTaskDtoのリストに変換
     * @param taskEntities 変換元のTaskEntityのリスト
     * @return 変換されたTaskDtoのリスト
     */
    public static List<TaskDto> toTaskDtos(List<TaskEntity> taskEntities) {
        if (taskEntities == null) {
            return null;
        }

        return taskEntities.stream()
                .map(TaskMapper::toTaskDto)
                .collect(Collectors.toList());
    }
} 