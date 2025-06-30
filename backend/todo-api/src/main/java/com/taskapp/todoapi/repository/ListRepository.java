package com.taskapp.todoapi.repository;

import com.taskapp.todoapi.entity.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * リストエンティティのリポジトリインターフェース
 * データベースのlistsテーブルに対する操作を定義
 */
@Repository
public interface ListRepository extends JpaRepository<ListEntity, Long> {

    /**
     * 位置順でソートしたリストを全件取得
     * @return 位置順でソートされたリストのリスト
     */
    List<ListEntity> findAllByOrderByPositionOrderAsc();

    /**
     * タイトルでリストを検索（部分一致）
     * @param title 検索するタイトル
     * @return マッチしたリストのリスト
     */
    List<ListEntity> findByTitleContainingIgnoreCase(String title);

    /**
     * 指定したタイトルのリストが存在するかチェック
     * @param title チェックするタイトル
     * @return 存在する場合true
     */
    boolean existsByTitle(String title);

    /**
     * 指定したタイトルのリストを検索（完全一致）
     * @param title 検索するタイトル
     * @return マッチしたリスト（Optional）
     */
    Optional<ListEntity> findByTitle(String title);

    /**
     * 指定した位置以降のリストを取得
     * @param positionOrder 基準となる位置
     * @return 指定位置以降のリストのリスト
     */
    List<ListEntity> findByPositionOrderGreaterThanEqualOrderByPositionOrderAsc(Integer positionOrder);

    /**
     * 最大の位置番号を取得
     * @return 最大位置番号（Optional）
     */
    @Query("SELECT MAX(l.positionOrder) FROM ListEntity l")
    Optional<Integer> findMaxPositionOrder();

    /**
     * リストとそれに属するタスクを一緒に取得（N+1問題対策）
     * @return リストとタスクを含むリストのリスト
     */
    @Query("SELECT DISTINCT l FROM ListEntity l LEFT JOIN FETCH l.tasks t ORDER BY l.positionOrder ASC, t.positionOrder ASC")
    List<ListEntity> findAllWithTasks();

    /**
     * 指定したIDのリストとそれに属するタスクを取得
     * @param id リストID
     * @return リストとタスクを含むリスト（Optional）
     */
    @Query("SELECT l FROM ListEntity l LEFT JOIN FETCH l.tasks t WHERE l.id = :id ORDER BY t.positionOrder ASC")
    Optional<ListEntity> findByIdWithTasks(@Param("id") Long id);

    /**
     * 空のリスト（タスクが0件）を検索
     * @return 空のリストのリスト
     */
    @Query("SELECT l FROM ListEntity l WHERE l.tasks IS EMPTY ORDER BY l.positionOrder ASC")
    List<ListEntity> findEmptyLists();

    /**
     * タスク数を含むリスト情報を取得
     * @return リストとタスク数の情報
     */
    @Query("SELECT l, SIZE(l.tasks) FROM ListEntity l ORDER BY l.positionOrder ASC")
    List<Object[]> findAllWithTaskCount();
} 