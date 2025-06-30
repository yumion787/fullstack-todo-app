package com.taskapp.todoapi.controller;

import com.taskapp.todoapi.mapper.ListMapper;
import com.taskapp.todoapi.dto.ListDto;
import com.taskapp.todoapi.entity.ListEntity;
import com.taskapp.todoapi.service.ListService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * リスト操作のREST APIコントローラ
 */
@RestController
@RequestMapping("/api/lists")
@CrossOrigin(origins = "http://localhost:3000")
public class ListController {

    private final ListService listService;

    @Autowired
    public ListController(ListService listService) {
        this.listService = listService;
    }

    /**
     * 全てのリストを取得
     * @param includeTasks タスクを含むかどうか
     * @return リストのリスト
     */
    @GetMapping
    public ResponseEntity<List<ListDto>> getAllLists(
            @RequestParam(value = "includeTasks", defaultValue = "false") boolean includeTasks) {
        
        List<ListEntity> lists;
        if (includeTasks) {
            lists = listService.getAllListsWithTasks();
            return ResponseEntity.ok(ListMapper.toListDtosWithTasks(lists));
        } else {
            lists = listService.getAllLists();
            return ResponseEntity.ok(ListMapper.toListDtos(lists));
        }
    }

    /**
     * IDでリストを取得
     * @param id リストID
     * @param includeTasks タスクを含むかどうか
     * @return リスト
     */
    @GetMapping("/{id}")
    public ResponseEntity<ListDto> getListById(
            @PathVariable Long id,
            @RequestParam(value = "includeTasks", defaultValue = "false") boolean includeTasks) {
        
        ListEntity list;
        if (includeTasks) {
            list = listService.getListByIdWithTasks(id);
            return ResponseEntity.ok(ListMapper.toListDtoWithTasks(list));
        } else {
            list = listService.getListById(id);
            return ResponseEntity.ok(ListMapper.toListDto(list));
        }
    }

    /**
     * 新しいリストを作成
     * @param listDto 作成するリスト
     * @return 作成されたリスト
     */
    @PostMapping
    public ResponseEntity<ListDto> createList(@Valid @RequestBody ListDto listDto) {
        ListEntity listEntity = ListMapper.toListEntity(listDto);
        ListEntity createdList = listService.createList(listEntity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ListMapper.toListDto(createdList));
    }

    /**
     * リストを更新
     * @param id 更新するリストのID
     * @param listDto 更新内容
     * @return 更新されたリスト
     */
    @PutMapping("/{id}")
    public ResponseEntity<ListDto> updateList(
            @PathVariable Long id, 
            @Valid @RequestBody ListDto listDto) {
        
        ListEntity listEntity = ListMapper.toListEntity(listDto);
        ListEntity updatedList = listService.updateList(id, listEntity);
        return ResponseEntity.ok(ListMapper.toListDto(updatedList));
    }

    /**
     * リストを削除
     * @param id 削除するリストのID
     * @return レスポンス
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        listService.deleteList(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * リストの位置を更新
     * @param id リストID
     * @param newPosition 新しい位置
     * @return 更新されたリスト
     */
    @PatchMapping("/{id}/position")
    public ResponseEntity<ListDto> updateListPosition(
            @PathVariable Long id,
            @RequestParam Integer newPosition) {
        
        ListEntity updatedList = listService.updateListPosition(id, newPosition);
        return ResponseEntity.ok(ListMapper.toListDto(updatedList));
    }

    /**
     * タイトルでリストを検索
     * @param title 検索するタイトル
     * @return マッチしたリストのリスト
     */
    @GetMapping("/search")
    public ResponseEntity<List<ListDto>> searchListsByTitle(
            @RequestParam String title) {
        
        List<ListEntity> lists = listService.searchListsByTitle(title);
        return ResponseEntity.ok(ListMapper.toListDtos(lists));
    }

    /**
     * 空のリスト（タスクが0件）を取得
     * @return 空のリストのリスト
     */
    @GetMapping("/empty")
    public ResponseEntity<List<ListDto>> getEmptyLists() {
        List<ListEntity> lists = listService.getEmptyLists();
        return ResponseEntity.ok(ListMapper.toListDtos(lists));
    }

    /**
     * リスト数を取得
     * @return リスト数
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getListCount() {
        long count = listService.getListCount();
        return ResponseEntity.ok(count);
    }

    /**
     * リストとタスク数の統計情報を取得
     * @return リストとタスク数の情報
     */
    @GetMapping("/stats")
    public ResponseEntity<List<Object[]>> getListsWithTaskCount() {
        List<Object[]> stats = listService.getListsWithTaskCount();
        return ResponseEntity.ok(stats);
    }
} 