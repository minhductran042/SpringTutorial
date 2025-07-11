package com.minhductran.tutorial.minhductran.controller;


import com.minhductran.tutorial.minhductran.dto.request.ToDoDTO;
import com.minhductran.tutorial.minhductran.dto.response.ResponseErrorEntity;
import com.minhductran.tutorial.minhductran.dto.response.ResponseEntity;
import com.minhductran.tutorial.minhductran.dto.response.ToDoDetailResponse;
import com.minhductran.tutorial.minhductran.service.ToDoService;
import com.minhductran.tutorial.minhductran.utils.ToDoStatus;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/todos")
@Validated
@Slf4j
public class ToDoController {

    @Autowired
    private ToDoService toDoService;

    @PostMapping("create")
    ResponseEntity<ToDoDetailResponse> createTo(@RequestBody @Valid ToDoDTO request) {
        try {
            ToDoDetailResponse toDo = toDoService.createToDo(request);

            return new ResponseEntity<>(HttpStatus.CREATED.value(), "ToDo created successfully", toDo);
        } catch (Exception e) {
            return new ResponseErrorEntity(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("get/{todoId}")
    ResponseEntity<ToDoDetailResponse> getToDo(@PathVariable int todoId) {
        try {
            ToDoDetailResponse toDo = toDoService.getToDo(todoId);
            return new ResponseEntity<>(HttpStatus.OK.value(), "Get ToDo successfully", toDo);
        } catch (Exception e) {
            return new ResponseErrorEntity(HttpStatus.BAD_REQUEST.value(), "ToDo not found");
        }
    }

    @GetMapping("getAll")
    ResponseEntity<List<ToDoDetailResponse>> getAllToDos(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                         @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                         @RequestParam(defaultValue = "id", required = false) String sortBy,
                                                         @RequestParam(defaultValue = "asc", required = false) String sortOrder) {
        try {
            List<ToDoDetailResponse> toDos = toDoService.getAllToDos(pageNo, pageSize, sortBy, sortOrder);
            return new ResponseEntity<>(HttpStatus.OK.value(), "Get all ToDos successfully", toDos);
        } catch (Exception e) {
            return new ResponseErrorEntity(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping("update/{todoId}")
    ResponseEntity updateToDo(@PathVariable int todoId, @RequestBody @Valid ToDoDTO request) {
        try {
            ToDoDetailResponse toDo = toDoService.updateToDo(todoId, request);
            return new ResponseEntity<>(HttpStatus.CREATED.value(), "Updated ToDo successfully", toDo);
        } catch (Exception e) {
            return new ResponseErrorEntity(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("delete/{todoId}")
    ResponseEntity<?> deleteToDo(@PathVariable int todoId) {
        try {
            toDoService.deleteToDo(todoId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT.value(), "ToDo deleted successfully");
        } catch (Exception e) {
            return new ResponseErrorEntity(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<List<ToDoDetailResponse>> getToDoFromFilter(
            @RequestParam (required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam (required = false) ToDoStatus toDoStatus
            ) {
        try {
//            List<ToDoDetailResponse> toDosFromFilter = toDoService.getToDoFromFilter(date, toDoStatus,
//                    pageNo, pageSize, sortBy, sortOrder);
            List<ToDoDetailResponse> toDosFromFilter = toDoService.getToDoFromFilter(date, toDoStatus);
            return new ResponseEntity<>(HttpStatus.OK.value(), "Get ToDo successfully", toDosFromFilter);
        } catch (Exception e) {
            return new ResponseErrorEntity(HttpStatus.BAD_REQUEST.value(), "Error getting ToDos: " + e.getMessage());
        }
    }
}
