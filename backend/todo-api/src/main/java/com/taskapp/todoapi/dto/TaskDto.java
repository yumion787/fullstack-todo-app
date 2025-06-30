package com.taskapp.todoapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * タスク用のデータ転送オブジェクト
 */
public class TaskDto {

    private Long id;

    @NotBlank(message = "タイトルは必須です")
    @Size(max = 255, message = "タイトルは255文字以内で入力してください")
    private String title;

    private String description;

    private String status; // "TODO" or "DONE"

    private Integer positionOrder;

    @NotNull(message = "リストIDは必須です")
    private Long listId;

    private String listTitle; // リスト名（表示用）

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // デフォルトコンストラクタ
    public TaskDto() {}

    // 基本コンストラクタ
    public TaskDto(Long id, String title, String description, String status, 
                   Integer positionOrder, Long listId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.positionOrder = positionOrder;
        this.listId = listId;
    }

    // 全フィールドコンストラクタ
    public TaskDto(Long id, String title, String description, String status, 
                   Integer positionOrder, Long listId, String listTitle,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.positionOrder = positionOrder;
        this.listId = listId;
        this.listTitle = listTitle;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPositionOrder() {
        return positionOrder;
    }

    public void setPositionOrder(Integer positionOrder) {
        this.positionOrder = positionOrder;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
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

    // ヘルパーメソッド
    public boolean isCompleted() {
        return "DONE".equals(status);
    }

    @Override
    public String toString() {
        return "TaskDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", positionOrder=" + positionOrder +
                ", listId=" + listId +
                ", listTitle='" + listTitle + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 