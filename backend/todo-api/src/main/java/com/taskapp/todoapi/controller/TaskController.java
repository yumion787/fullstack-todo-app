package com.taskapp.todoapi.controller;

import com.taskapp.todoapi.mapper.TaskMapper;
import com.taskapp.todoapi.dto.TaskDto;
import com.taskapp.todoapi.entity.TaskEntity;
import com.taskapp.todoapi.entity.TaskEntity.TaskStatus;
import com.taskapp.todoapi.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * タスク操作のREST APIコントローラ
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 全てのタスクを取得（フィルタリング対応）
     * @param status タスクステータス（オプション）
     * @param listId リストID（オプション）
     * @return タスクのリスト
     */
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long listId) {
        
        List<TaskEntity> tasks;
        
        if (status != null && listId != null) {
            // ステータスとリストIDの両方で絞り込み
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            tasks = taskService.getTasksByListIdAndStatus(listId, taskStatus);
        } else if (status != null) {
            // ステータスのみで絞り込み
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            tasks = taskService.getTasksByStatus(taskStatus);
        } else if (listId != null) {
            // リストIDのみで絞り込み
            tasks = taskService.getTasksByListId(listId);
        } else {
            // フィルタなし（全取得）
            tasks = taskService.getAllTasks();
        }
        
        return ResponseEntity.ok(TaskMapper.toTaskDtos(tasks));
    }

    /**
     * IDでタスクを取得
     * @param id タスクID
     * @return タスク
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        TaskEntity task = taskService.getTaskByIdWithList(id);
        return ResponseEntity.ok(TaskMapper.toTaskDto(task));
    }

    /**
     * 指定したリストのタスクを取得
     * @param listId リストID
     * @param status タスクステータス（オプション）
     * @return タスクのリスト
     */
    @GetMapping("/list/{listId}")
    public ResponseEntity<List<TaskDto>> getTasksByListId(
            @PathVariable Long listId,
            @RequestParam(required = false) String status) {
        
        List<TaskEntity> tasks;
        if (status != null) {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            tasks = taskService.getTasksByListIdAndStatus(listId, taskStatus);
        } else {
            tasks = taskService.getTasksByListId(listId);
        }
        
        return ResponseEntity.ok(TaskMapper.toTaskDtos(tasks));
    }

    /**
     * 指定したステータスのタスクを取得
     * @param status タスクステータス
     * @return タスクのリスト
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable String status) {
        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        List<TaskEntity> tasks = taskService.getTasksByStatus(taskStatus);
        return ResponseEntity.ok(TaskMapper.toTaskDtos(tasks));
    }

    /**
     * 新しいタスクを作成
     * @param taskDto 作成するタスク
     * @return 作成されたタスク
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        TaskEntity taskEntity = TaskMapper.toTaskEntity(taskDto);
        TaskEntity createdTask = taskService.createTask(taskEntity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskMapper.toTaskDto(createdTask));
    }

    /**
     * タスクを更新
     * @param id 更新するタスクのID
     * @param taskDto 更新内容
     * @return 更新されたタスク
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDto taskDto) {
        
        TaskEntity taskEntity = TaskMapper.toTaskEntity(taskDto);
        TaskEntity updatedTask = taskService.updateTask(id, taskEntity);
        return ResponseEntity.ok(TaskMapper.toTaskDto(updatedTask));
    }

    /**
     * タスクを削除
     * @param id 削除するタスクのID
     * @return レスポンス
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * タスクのステータスを切り替え
     * @param id タスクID
     * @return 更新されたタスク
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<TaskDto> toggleTaskStatus(@PathVariable Long id) {
        TaskEntity updatedTask = taskService.toggleTaskStatus(id);
        return ResponseEntity.ok(TaskMapper.toTaskDto(updatedTask));
    }

    /**
     * タスクの位置を更新
     * @param id タスクID
     * @param newPosition 新しい位置
     * @return 更新されたタスク
     */
    @PatchMapping("/{id}/position")
    public ResponseEntity<TaskDto> updateTaskPosition(
            @PathVariable Long id,
            @RequestParam Integer newPosition) {
        
        TaskEntity updatedTask = taskService.updateTaskPosition(id, newPosition);
        return ResponseEntity.ok(TaskMapper.toTaskDto(updatedTask));
    }

    /**
     * リスト内のタスクの順序を一括更新
     * @param listId リストID
     * @param taskIds 新しい順序のタスクID配列
     * @return 更新されたタスクのリスト
     */
    @PatchMapping("/list/{listId}/reorder")
    public ResponseEntity<List<TaskDto>> reorderTasks(
            @PathVariable Long listId,
            @RequestBody List<Long> taskIds) {
        
        List<TaskEntity> updatedTasks = taskService.reorderTasks(listId, taskIds);
        return ResponseEntity.ok(TaskMapper.toTaskDtos(updatedTasks));
    }

    /**
     * タスクを別のリストに移動
     * @param id タスクID
     * @param newListId 移動先のリストID
     * @param newPosition 移動先での位置（オプション）
     * @return 更新されたタスク
     */
    @PatchMapping("/{id}/move")
    public ResponseEntity<TaskDto> moveTaskToList(
            @PathVariable Long id,
            @RequestParam Long newListId,
            @RequestParam(required = false) Integer newPosition) {
        
        TaskEntity updatedTask = taskService.moveTaskToList(id, newListId, newPosition);
        return ResponseEntity.ok(TaskMapper.toTaskDto(updatedTask));
    }

    /**
     * キーワードでタスクを検索
     * @param keyword 検索キーワード
     * @return マッチしたタスクのリスト
     */
    @GetMapping("/search")
    public ResponseEntity<List<TaskDto>> searchTasks(@RequestParam String keyword) {
        List<TaskEntity> tasks = taskService.searchTasks(keyword);
        return ResponseEntity.ok(TaskMapper.toTaskDtos(tasks));
    }

    /**
     * 期間を指定してタスクを検索
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @return 期間内に作成されたタスクのリスト
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<TaskDto>> getTasksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<TaskEntity> tasks = taskService.getTasksByDateRange(startDate, endDate);
        return ResponseEntity.ok(TaskMapper.toTaskDtos(tasks));
    }

    /**
     * 未完了タスクを優先度順で取得
     * @return 未完了タスクのリスト
     */
    @GetMapping("/todo/priority")
    public ResponseEntity<List<TaskDto>> getTodoTasksOrderByPriority() {
        List<TaskEntity> tasks = taskService.getTodoTasksOrderByPriority();
        return ResponseEntity.ok(TaskMapper.toTaskDtos(tasks));
    }

    /**
     * 指定したリストのタスク数を取得
     * @param listId リストID
     * @param status タスクステータス（オプション）
     * @return タスク数
     */
    @GetMapping("/count/list/{listId}")
    public ResponseEntity<Long> getTaskCountByListId(
            @PathVariable Long listId,
            @RequestParam(required = false) String status) {
        
        long count;
        if (status != null) {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            count = taskService.getTaskCountByListIdAndStatus(listId, taskStatus);
        } else {
            count = taskService.getTaskCountByListId(listId);
        }
        
        return ResponseEntity.ok(count);
    }
} 