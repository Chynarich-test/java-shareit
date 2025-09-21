package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getOwnRequests(long userId);

    List<ItemRequestDto> getOtherUsersRequests(long userId);

    ItemRequestDto getRequestById(long requestId, long userId);
}
