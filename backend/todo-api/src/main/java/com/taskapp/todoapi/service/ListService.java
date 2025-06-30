package com.taskapp.todoapi.service;

import com.taskapp.todoapi.entity.ListEntity;
import com.taskapp.todoapi.exception.DuplicateResourceException;
import com.taskapp.todoapi.exception.ResourceNotFoundException;
import com.taskapp.todoapi.repository.ListRepository;
import com.taskapp.todoapi.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * リスト操作のビジネスロジックを提供するサービスクラス
 */
@Service
@Transactional
public class ListService {

    private final ListRepository listRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ListService(ListRepository listRepository, TaskRepository taskRepository) {
        this.listRepository = listRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * 全てのリストを位置順で取得
     * @return リストのリスト
     */
    @Transactional(readOnly = true)
    public List<ListEntity> getAllLists() {
        return listRepository.findAllByOrderByPositionOrderAsc();
    }

    /**
     * 全てのリストをタスクと一緒に取得
     * @return リストとタスクを含むリストのリスト
     */
    @Transactional(readOnly = true)
    public List<ListEntity> getAllListsWithTasks() {
        return listRepository.findAllWithTasks();
    }

    /**
     * IDでリストを取得
     * @param id リストID
     * @return リスト
     * @throws ResourceNotFoundException リストが見つからない場合
     */
    @Transactional(readOnly = true)
    public ListEntity getListById(Long id) {
        return listRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("List", "id", id));
    }

    /**
     * IDでリストをタスクと一緒に取得
     * @param id リストID
     * @return リストとタスク
     * @throws ResourceNotFoundException リストが見つからない場合
     */
    @Transactional(readOnly = true)
    public ListEntity getListByIdWithTasks(Long id) {
        return listRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new ResourceNotFoundException("List", "id", id));
    }

    /**
     * 新しいリストを作成
     * @param listEntity 作成するリスト
     * @return 作成されたリスト
     * @throws DuplicateResourceException 同じタイトルのリストが既に存在する場合
     */
    public ListEntity createList(ListEntity listEntity) {
        // タイトルの重複チェック
        if (listRepository.existsByTitle(listEntity.getTitle())) {
            throw new DuplicateResourceException("List", "title", listEntity.getTitle());
        }

        // 位置が指定されていない場合は最後に追加
        if (listEntity.getPositionOrder() == null) {
            Optional<Integer> maxPosition = listRepository.findMaxPositionOrder();
            listEntity.setPositionOrder(maxPosition.orElse(-1) + 1);
        }

        return listRepository.save(listEntity);
    }

    /**
     * リストを更新
     * @param id 更新するリストのID
     * @param updatedList 更新内容
     * @return 更新されたリスト
     * @throws ResourceNotFoundException リストが見つからない場合
     * @throws DuplicateResourceException 同じタイトルのリストが既に存在する場合
     */
    public ListEntity updateList(Long id, ListEntity updatedList) {
        ListEntity existingList = getListById(id);

        // タイトルが変更される場合の重複チェック
        if (!existingList.getTitle().equals(updatedList.getTitle()) &&
            listRepository.existsByTitle(updatedList.getTitle())) {
            throw new DuplicateResourceException("List", "title", updatedList.getTitle());
        }

        existingList.setTitle(updatedList.getTitle());
        if (updatedList.getPositionOrder() != null) {
            existingList.setPositionOrder(updatedList.getPositionOrder());
        }

        return listRepository.save(existingList);
    }

    /**
     * リストを削除
     * @param id 削除するリストのID
     * @throws ResourceNotFoundException リストが見つからない場合
     */
    public void deleteList(Long id) {
        ListEntity list = getListById(id);
        
        // 関連するタスクも一緒に削除される（CascadeType.ALL設定により）
        listRepository.delete(list);
    }

    /**
     * リストの位置を更新
     * @param id リストID
     * @param newPosition 新しい位置
     * @return 更新されたリスト
     * @throws ResourceNotFoundException リストが見つからない場合
     */
    public ListEntity updateListPosition(Long id, Integer newPosition) {
        ListEntity list = getListById(id);
        Integer oldPosition = list.getPositionOrder();

        if (oldPosition.equals(newPosition)) {
            return list; // 位置が変わらない場合は何もしない
        }

        // 位置の調整処理
        if (newPosition > oldPosition) {
            // 後ろに移動：間の要素を前に詰める
            List<ListEntity> listsToUpdate = listRepository
                    .findByPositionOrderGreaterThanEqualOrderByPositionOrderAsc(oldPosition + 1);
            for (ListEntity listToUpdate : listsToUpdate) {
                if (listToUpdate.getPositionOrder() <= newPosition) {
                    listToUpdate.setPositionOrder(listToUpdate.getPositionOrder() - 1);
                    listRepository.save(listToUpdate);
                }
            }
        } else {
            // 前に移動：間の要素を後ろにずらす
            List<ListEntity> listsToUpdate = listRepository
                    .findByPositionOrderGreaterThanEqualOrderByPositionOrderAsc(newPosition);
            for (ListEntity listToUpdate : listsToUpdate) {
                if (listToUpdate.getPositionOrder() < oldPosition) {
                    listToUpdate.setPositionOrder(listToUpdate.getPositionOrder() + 1);
                    listRepository.save(listToUpdate);
                }
            }
        }

        list.setPositionOrder(newPosition);
        return listRepository.save(list);
    }

    /**
     * タイトルでリストを検索
     * @param title 検索するタイトル
     * @return マッチしたリストのリスト
     */
    @Transactional(readOnly = true)
    public List<ListEntity> searchListsByTitle(String title) {
        return listRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * 空のリスト（タスクが0件）を取得
     * @return 空のリストのリスト
     */
    @Transactional(readOnly = true)
    public List<ListEntity> getEmptyLists() {
        return listRepository.findEmptyLists();
    }

    /**
     * リスト数を取得
     * @return リスト数
     */
    @Transactional(readOnly = true)
    public long getListCount() {
        return listRepository.count();
    }

    /**
     * リストとタスク数の情報を取得
     * @return リストとタスク数の情報
     */
    @Transactional(readOnly = true)
    public List<Object[]> getListsWithTaskCount() {
        return listRepository.findAllWithTaskCount();
    }
} 