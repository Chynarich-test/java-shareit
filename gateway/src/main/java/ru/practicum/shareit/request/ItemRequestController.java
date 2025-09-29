package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.base.AppConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;


@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader(AppConstants.USER_ID_HEADER) long userId) {
        return itemRequestClient.create(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(AppConstants.USER_ID_HEADER) long userId) {
        return itemRequestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader(AppConstants.USER_ID_HEADER) long userId) {
        return itemRequestClient.getOtherUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable("requestId") long requestId,
                                                 @RequestHeader(AppConstants.USER_ID_HEADER) long userId) {
        return itemRequestClient.getRequestById(requestId, userId);
    }
}
