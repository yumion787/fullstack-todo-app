package com.taskapp.todoapi.mapper;

import com.taskapp.todoapi.dto.ListDto;
import com.taskapp.todoapi.dto.TaskDto;
import com.taskapp.todoapi.entity.ListEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ListEntityとListDTOの変換を行うマッパークラス
 */
public class ListMapper {

    /**
     * ListEntityをListDtoに変換
     * @param listEntity 変換元のListEntity
     * @return 変換されたListDto
     */
    public static ListDto toListDto(ListEntity listEntity) {
        if (listEntity == null) {
            return null;
        }

        ListDto dto = new ListDto();
        dto.setId(listEntity.getId());
        dto.setTitle(listEntity.getTitle());
        dto.setPositionOrder(listEntity.getPositionOrder());
        dto.setCreatedAt(listEntity.getCreatedAt());
        dto.setUpdatedAt(listEntity.getUpdatedAt());

        return dto;
    }

    /**
     * ListEntityをListDtoに変換（タスクを含む）
     * @param listEntity 変換元のListEntity
     * @return 変換されたListDto（タスクを含む）
     */
    public static ListDto toListDtoWithTasks(ListEntity listEntity) {
        if (listEntity == null) {
            return null;
        }

        ListDto dto = toListDto(listEntity);
        
        if (listEntity.getTasks() != null) {
            List<TaskDto> taskDtos = listEntity.getTasks().stream()
                    .map(TaskMapper::toTaskDto)
                    .collect(Collectors.toList());
            dto.setTasks(taskDtos);
        }

        return dto;
    }

    /**
     * ListDtoをListEntityに変換
     * @param listDto 変換元のListDto
     * @return 変換されたListEntity
     */
    public static ListEntity toListEntity(ListDto listDto) {
        if (listDto == null) {
            return null;
        }

        ListEntity entity = new ListEntity();
        entity.setId(listDto.getId());
        entity.setTitle(listDto.getTitle());
        entity.setPositionOrder(listDto.getPositionOrder());
        entity.setCreatedAt(listDto.getCreatedAt());
        entity.setUpdatedAt(listDto.getUpdatedAt());

        return entity;
    }

    /**
     * ListEntityのリストをListDtoのリストに変換
     * @param listEntities 変換元のListEntityのリスト
     * @return 変換されたListDtoのリスト
     */
    public static List<ListDto> toListDtos(List<ListEntity> listEntities) {
        if (listEntities == null) {
            return null;
        }

        return listEntities.stream()
                .map(ListMapper::toListDto)
                .collect(Collectors.toList());
    }

    /**
     * ListEntityのリストをListDtoのリストに変換（タスクを含む）
     * @param listEntities 変換元のListEntityのリスト
     * @return 変換されたListDtoのリスト（タスクを含む）
     */
    public static List<ListDto> toListDtosWithTasks(List<ListEntity> listEntities) {
        if (listEntities == null) {
            return null;
        }

        return listEntities.stream()
                .map(ListMapper::toListDtoWithTasks)
                .collect(Collectors.toList());
    }
} 