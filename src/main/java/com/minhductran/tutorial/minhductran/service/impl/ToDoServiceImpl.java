package com.minhductran.tutorial.minhductran.service.impl;

import com.minhductran.tutorial.minhductran.dto.request.ToDoDTO;
import com.minhductran.tutorial.minhductran.dto.response.ToDoDetailResponse;
import com.minhductran.tutorial.minhductran.exception.ResourceNotFoundException;
import com.minhductran.tutorial.minhductran.mappers.ToDoMapper;
import com.minhductran.tutorial.minhductran.model.ToDo;
import com.minhductran.tutorial.minhductran.model.User;
import com.minhductran.tutorial.minhductran.repository.ToDoRepository;
import com.minhductran.tutorial.minhductran.repository.UserRepository;
import com.minhductran.tutorial.minhductran.service.ToDoService;
import com.minhductran.tutorial.minhductran.utils.ToDoStatus;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class ToDoServiceImpl implements ToDoService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private ToDoMapper toDoMapper;

    @Override
    @Transactional
    public ToDoDetailResponse createToDo(ToDoDTO request) {
        User user = getUserById(request.getUserId());
        ToDo toDo = toDoMapper.toEntity(request);
        if(toDo.getStatus() == null) {
            toDo.setStatus(ToDoStatus.NOT_STARTED);
        }
        toDo.setUser(user);
        toDoRepository.save(toDo);
        return toDoMapper.toToDoDetailResponse(toDo);
    }

    @Override
    @Transactional
    public ToDoDetailResponse getToDo(int todoId) {
        ToDo todo = getToDoById(todoId);
        System.out.println("Todo status from DB: " + todo.getStatus());
        return toDoMapper.toToDoDetailResponse(todo);
    }

    @Override
    public List<ToDoDetailResponse> getAllToDos(int pageNo, int pageSize, String sortBy, String sortOrder) {
        int page = 0;
        if (pageNo > 0) {
            page = pageNo - 1;
        }
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<ToDo>  toDos= toDoRepository.findAll(pageable);
        log.info("Get all todos successfully");
        return toDos.stream().map(toDoMapper::toToDoDetailResponse).toList();
    }

    @Override
    @Transactional
    public ToDoDetailResponse updateToDo(int toDoId, ToDoDTO request) {
            User user = getUserById(request.getUserId());
            ToDo toDo = getToDoById(toDoId);
            toDoMapper.updateEntity(toDo, request);
            log.info("Update Todo successfully");
            toDoRepository.save(toDo);
            return toDoMapper.toToDoDetailResponse(toDo);
    }

    @Override
    @Transactional
    public void deleteToDo(int todoId) {
        toDoRepository.delete(getToDoById(todoId));
    }

    public ToDo getToDoById(int todoId) {

        return toDoRepository.findById(todoId).orElseThrow(() -> new ResourceNotFoundException("ToDo not found"));
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void changeToDoStatus(int todoId, ToDoStatus status) {
        ToDo todo = getToDoById(todoId);
        todo.setStatus(status);
        log.info("Status changed");
    }


    @Override
    public List<ToDoDetailResponse> getToDoFromFilter(Date date, ToDoStatus status) {
        try{
            List<ToDo> toDos = toDoRepository.findAll();
            if(date != null) {
                toDos.removeIf(toDo -> toDo.getStartDate() == null||  !toDo.getStartDate().equals(date));
            }
            if(status != null) {
                toDos.removeIf(toDo -> toDo.getStartDate() == null||  !toDo.getStatus().equals(status));
            }
//
//        int page = 0;
//        if(pageNo > 0) {
//            page = pageNo - 1;
//        }
////
//        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
//        int start = page * pageSize;
//        int end = Math.min(start + pageSize, toDos.size());
//
//        List<ToDo> pagedToDos = toDos.subList(start, end);
            log.info("Get todos successfully");
            return toDos.stream().map(toDoMapper::toToDoDetailResponse).toList();
        } catch (Exception e) {
            log.error("Error getting todos from filter: {}", e.getMessage());
            throw new ResourceNotFoundException("ToDo not found");
        }

    }
}
