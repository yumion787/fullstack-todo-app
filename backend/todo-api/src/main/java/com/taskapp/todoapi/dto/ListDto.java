package com.taskapp.todoapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * リスト用のデータ転送オブジェクト
 */
public class ListDto {

    private Long id;

    @NotBlank(message = "タイトルは必須です")
    @Size(max = 255, message = "タイトルは255文字以内で入力してください")
    private String title;

    private Integer positionOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskDto> tasks;
    private Long taskCount;

    // デフォルトコンストラクタ
    public ListDto() {}

    // コンストラクタ
    public ListDto(Long id, String title, Integer positionOrder) {
        this.id = id;
        this.title = title;
        this.positionOrder = positionOrder;
    }

    // 全フィールドコンストラクタ
    public ListDto(Long id, String title, Integer positionOrder, 
                   LocalDateTime createdAt, LocalDateTime updatedAt, 
                   List<TaskDto> tasks) {
        this.id = id;
        this.title = title;
        this.positionOrder = positionOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tasks = tasks;
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPositionOrder() {
        return positionOrder;
    }

    public void setPositionOrder(Integer positionOrder) {
        this.positionOrder = positionOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }

    public Long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Long taskCount) {
        this.taskCount = taskCount;
    }

    @Override
    public String toString() {
        return "ListDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", positionOrder=" + positionOrder +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", taskCount=" + (tasks != null ? tasks.size() : taskCount) +
                '}';
    }
} 