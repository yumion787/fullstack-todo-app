package com.taskapp.todoapi.service;

import com.taskapp.todoapi.entity.ListEntity;
import com.taskapp.todoapi.entity.TaskEntity;
import com.taskapp.todoapi.entity.TaskEntity.TaskStatus;
import com.taskapp.todoapi.exception.ResourceNotFoundException;
import com.taskapp.todoapi.repository.ListRepository;
import com.taskapp.todoapi.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * タスク操作のビジネスロジックを提供するサービスクラス
 */
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ListRepository listRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, ListRepository listRepository) {
        this.taskRepository = taskRepository;
        this.listRepository = listRepository;
    }

    /**
     * 全てのタスクを取得
     * @return タスクのリスト
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * IDでタスクを取得
     * @param id タスクID
     * @return タスク
     * @throws ResourceNotFoundException タスクが見つからない場合
     */
    @Transactional(readOnly = true)
    public TaskEntity getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    /**
     * IDでタスクをリスト情報と一緒に取得
     * @param id タスクID
     * @return タスクとリスト情報
     * @throws ResourceNotFoundException タスクが見つからない場合
     */
    @Transactional(readOnly = true)
    public TaskEntity getTaskByIdWithList(Long id) {
        return taskRepository.findByIdWithList(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    /**
     * 指定したリストのタスクを取得
     * @param listId リストID
     * @return タスクのリスト
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getTasksByListId(Long listId) {
        return taskRepository.findByListIdOrderByPositionOrderAsc(listId);
    }

    /**
     * 指定したリストとステータスのタスクを取得
     * @param listId リストID
     * @param status タスクステータス
     * @return タスクのリスト
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getTasksByListIdAndStatus(Long listId, TaskStatus status) {
        return taskRepository.findByListIdAndStatusOrderByPositionOrderAsc(listId, status);
    }

    /**
     * 指定したステータスのタスクを取得
     * @param status タスクステータス
     * @return タスクのリスト
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * 新しいタスクを作成
     * @param taskEntity 作成するタスク
     * @return 作成されたタスク
     * @throws ResourceNotFoundException 指定されたリストが見つからない場合
     */
    public TaskEntity createTask(TaskEntity taskEntity) {
        // リストの存在確認
        ListEntity list = listRepository.findById(taskEntity.getList().getId())
                .orElseThrow(() -> new ResourceNotFoundException("List", "id", taskEntity.getList().getId()));

        // 位置が指定されていない場合は最後に追加
        if (taskEntity.getPositionOrder() == null) {
            Optional<Integer> maxPosition = taskRepository.findMaxPositionOrderByListId(list.getId());
            taskEntity.setPositionOrder(maxPosition.orElse(-1) + 1);
        }

        // ステータスが指定されていない場合はTODOに設定
        if (taskEntity.getStatus() == null) {
            taskEntity.setStatus(TaskStatus.TODO);
        }

        taskEntity.setList(list);
        return taskRepository.save(taskEntity);
    }

    /**
     * タスクを更新
     * @param id 更新するタスクのID
     * @param updatedTask 更新内容
     * @return 更新されたタスク
     * @throws ResourceNotFoundException タスクまたはリストが見つからない場合
     */
    public TaskEntity updateTask(Long id, TaskEntity updatedTask) {
        TaskEntity existingTask = getTaskById(id);

        // リストが変更される場合の存在確認
        if (updatedTask.getList() != null && 
            !existingTask.getList().getId().equals(updatedTask.getList().getId())) {
            ListEntity newList = listRepository.findById(updatedTask.getList().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("List", "id", updatedTask.getList().getId()));
            existingTask.setList(newList);
            
            // 新しいリストに移動する場合は位置を最後に設定
            Optional<Integer> maxPosition = taskRepository.findMaxPositionOrderByListId(newList.getId());
            existingTask.setPositionOrder(maxPosition.orElse(-1) + 1);
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        
        if (updatedTask.getStatus() != null) {
            existingTask.setStatus(updatedTask.getStatus());
        }
        
        if (updatedTask.getPositionOrder() != null && 
            existingTask.getList().getId().equals(updatedTask.getList().getId())) {
            existingTask.setPositionOrder(updatedTask.getPositionOrder());
        }

        return taskRepository.save(existingTask);
    }

    /**
     * タスクを削除
     * @param id 削除するタスクのID
     * @throws ResourceNotFoundException タスクが見つからない場合
     */
    public void deleteTask(Long id) {
        TaskEntity task = getTaskById(id);
        taskRepository.delete(task);
    }

    /**
     * タスクのステータスを切り替え
     * @param id タスクID
     * @return 更新されたタスク
     * @throws ResourceNotFoundException タスクが見つからない場合
     */
    public TaskEntity toggleTaskStatus(Long id) {
        TaskEntity task = getTaskById(id);
        
        if (task.getStatus() == TaskStatus.TODO) {
            task.markAsCompleted();
        } else {
            task.markAsTodo();
        }
        
        return taskRepository.save(task);
    }

    /**
     * タスクの位置を更新
     * @param id タスクID
     * @param newPosition 新しい位置
     * @return 更新されたタスク
     * @throws ResourceNotFoundException タスクが見つからない場合
     */
    public TaskEntity updateTaskPosition(Long id, Integer newPosition) {
        TaskEntity task = getTaskById(id);
        Integer oldPosition = task.getPositionOrder();
        Long listId = task.getList().getId();

        if (oldPosition.equals(newPosition)) {
            return task; // 位置が変わらない場合は何もしない
        }

        // 同じリスト内での位置調整処理
        if (newPosition > oldPosition) {
            // 後ろに移動：間の要素を前に詰める
            List<TaskEntity> tasksToUpdate = taskRepository
                    .findByListIdAndPositionOrderGreaterThanEqualOrderByPositionOrderAsc(listId, oldPosition + 1);
            for (TaskEntity taskToUpdate : tasksToUpdate) {
                if (taskToUpdate.getPositionOrder() <= newPosition) {
                    taskToUpdate.setPositionOrder(taskToUpdate.getPositionOrder() - 1);
                    taskRepository.save(taskToUpdate);
                }
            }
        } else {
            // 前に移動：間の要素を後ろにずらす
            List<TaskEntity> tasksToUpdate = taskRepository
                    .findByListIdAndPositionOrderGreaterThanEqualOrderByPositionOrderAsc(listId, newPosition);
            for (TaskEntity taskToUpdate : tasksToUpdate) {
                if (taskToUpdate.getPositionOrder() < oldPosition) {
                    taskToUpdate.setPositionOrder(taskToUpdate.getPositionOrder() + 1);
                    taskRepository.save(taskToUpdate);
                }
            }
        }

        task.setPositionOrder(newPosition);
        return taskRepository.save(task);
    }

    /**
     * タスクを別のリストに移動
     * @param id タスクID
     * @param newListId 移動先のリストID
     * @param newPosition 移動先での位置（nullの場合は最後に追加）
     * @return 更新されたタスク
     * @throws ResourceNotFoundException タスクまたはリストが見つからない場合
     */
    public TaskEntity moveTaskToList(Long id, Long newListId, Integer newPosition) {
        TaskEntity task = getTaskById(id);
        ListEntity newList = listRepository.findById(newListId)
                .orElseThrow(() -> new ResourceNotFoundException("List", "id", newListId));

        // 位置が指定されていない場合は最後に追加
        if (newPosition == null) {
            Optional<Integer> maxPosition = taskRepository.findMaxPositionOrderByListId(newListId);
            newPosition = maxPosition.orElse(-1) + 1;
        }

        task.setList(newList);
        task.setPositionOrder(newPosition);
        
        return taskRepository.save(task);
    }

    /**
     * キーワードでタスクを検索
     * @param keyword 検索キーワード
     * @return マッチしたタスクのリスト
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> searchTasks(String keyword) {
        return taskRepository.findByTitleOrDescriptionContaining(keyword);
    }

    /**
     * 期間を指定してタスクを検索
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @return 期間内に作成されたタスクのリスト
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getTasksByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return taskRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }

    /**
     * 未完了タスクを優先度順で取得
     * @return 未完了タスクのリスト
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getTodoTasksOrderByPriority() {
        return taskRepository.findTodoTasksOrderByPriority();
    }

    /**
     * 指定したリストのタスク数を取得
     * @param listId リストID
     * @return タスク数
     */
    @Transactional(readOnly = true)
    public long getTaskCountByListId(Long listId) {
        return taskRepository.countByListId(listId);
    }

    /**
     * 指定したリストとステータスのタスク数を取得
     * @param listId リストID
     * @param status タスクステータス
     * @return タスク数
     */
    @Transactional(readOnly = true)
    public long getTaskCountByListIdAndStatus(Long listId, TaskStatus status) {
        return taskRepository.countByListIdAndStatus(listId, status);
    }

    /**
     * リスト内のタスクの順序を一括更新（デッドロック回避）
     * @param listId リストID
     * @param taskIds 新しい順序のタスクID配列
     * @return 更新されたタスクのリスト
     * @throws ResourceNotFoundException リストまたはタスクが見つからない場合
     */
    public List<TaskEntity> reorderTasks(Long listId, List<Long> taskIds) {
        // リストの存在確認
        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("List", "id", listId));

        // デッドロック回避のため、IDでソートしてから更新
        List<Long> sortedTaskIds = new ArrayList<>(taskIds);
        sortedTaskIds.sort(Long::compareTo);

        // 各タスクの位置を更新
        for (Long taskId : sortedTaskIds) {
            TaskEntity task = getTaskById(taskId);
            
            // リストIDの検証
            if (!task.getList().getId().equals(listId)) {
                throw new IllegalArgumentException("Task " + taskId + " does not belong to list " + listId);
            }
            
            // 新しい位置を設定
            int newPosition = taskIds.indexOf(taskId);
            task.setPositionOrder(newPosition);
            taskRepository.save(task);
        }

        // 更新されたタスク一覧を返す（位置順でソート）
        return taskRepository.findByListIdOrderByPositionOrderAsc(listId);
    }
} 