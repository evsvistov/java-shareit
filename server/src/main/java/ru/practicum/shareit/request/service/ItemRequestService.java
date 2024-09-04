package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto requestDto);

    Collection<ItemRequestDto> getUserRequests(Long userId);

    Collection<ItemRequestDto> getAllItemRequests(Long userId);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}
