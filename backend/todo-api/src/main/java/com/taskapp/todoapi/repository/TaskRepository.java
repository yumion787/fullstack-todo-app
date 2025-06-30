package com.taskapp.todoapi.repository;

import com.taskapp.todoapi.entity.TaskEntity;
import com.taskapp.todoapi.entity.TaskEntity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * タスクエンティティのリポジトリインターフェース
 * データベースのtasksテーブルに対する操作を定義
 */
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    /**
     * 指定したリストIDのタスクを位置順で取得
     * @param listId リストID
     * @return 位置順でソートされたタスクのリスト
     */
    List<TaskEntity> findByListIdOrderByPositionOrderAsc(Long listId);

    /**
     * 指定したリストIDとステータスのタスクを位置順で取得
     * @param listId リストID
     * @param status タスクステータス
     * @return マッチしたタスクのリスト
     */
    List<TaskEntity> findByListIdAndStatusOrderByPositionOrderAsc(Long listId, TaskStatus status);

    /**
     * 指定したステータスのタスクを全件取得
     * @param status タスクステータス
     * @return マッチしたタスクのリスト
     */
    List<TaskEntity> findByStatusOrderByCreatedAtDesc(TaskStatus status);

    /**
     * タイトルでタスクを検索（部分一致、大文字小文字無視）
     * @param title 検索するタイトル
     * @return マッチしたタスクのリスト
     */
    List<TaskEntity> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

    /**
     * 説明文でタスクを検索（部分一致、大文字小文字無視）
     * @param description 検索する説明文
     * @return マッチしたタスクのリスト
     */
    List<TaskEntity> findByDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(String description);

    /**
     * タイトルまたは説明文でタスクを検索
     * @param keyword 検索キーワード
     * @return マッチしたタスクのリスト
     */
    @Query("SELECT t FROM TaskEntity t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY t.createdAt DESC")
    List<TaskEntity> findByTitleOrDescriptionContaining(@Param("keyword") String keyword);

    /**
     * 指定したリストIDのタスク数を取得
     * @param listId リストID
     * @return タスク数
     */
    long countByListId(Long listId);

    /**
     * 指定したリストIDとステータスのタスク数を取得
     * @param listId リストID
     * @param status タスクステータス
     * @return タスク数
     */
    long countByListIdAndStatus(Long listId, TaskStatus status);

    /**
     * 指定したリストIDで最大の位置番号を取得
     * @param listId リストID
     * @return 最大位置番号（Optional）
     */
    @Query("SELECT MAX(t.positionOrder) FROM TaskEntity t WHERE t.list.id = :listId")
    Optional<Integer> findMaxPositionOrderByListId(@Param("listId") Long listId);

    /**
     * 指定した期間に作成されたタスクを取得
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @return 期間内に作成されたタスクのリスト
     */
    List<TaskEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 指定した期間に更新されたタスクを取得
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @return 期間内に更新されたタスクのリスト
     */
    List<TaskEntity> findByUpdatedAtBetweenOrderByUpdatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 未完了タスクを優先度順（位置順）で取得
     * @return 未完了タスクのリスト
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.status = 'TODO' ORDER BY t.list.positionOrder ASC, t.positionOrder ASC")
    List<TaskEntity> findTodoTasksOrderByPriority();

    /**
     * 指定したリストIDのタスクを削除
     * @param listId リストID
     */
    void deleteByListId(Long listId);

    /**
     * 指定したリストIDと位置以降のタスクを取得
     * @param listId リストID
     * @param positionOrder 基準位置
     * @return 指定位置以降のタスクのリスト
     */
    List<TaskEntity> findByListIdAndPositionOrderGreaterThanEqualOrderByPositionOrderAsc(Long listId, Integer positionOrder);

    /**
     * リスト情報を含むタスクを取得（N+1問題対策）
     * @param id タスクID
     * @return リスト情報を含むタスク（Optional）
     */
    @Query("SELECT t FROM TaskEntity t JOIN FETCH t.list WHERE t.id = :id")
    Optional<TaskEntity> findByIdWithList(@Param("id") Long id);
} 