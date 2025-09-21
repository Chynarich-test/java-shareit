package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemWithBookingsDto getItem(long id) {
        Item item = findById(id);

        List<CommentDto> commentDtos = commentRepository.findByItemId(id)
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        return ItemMapper.toItemWithBookingsDto(
                item,
                null,
                null,
                commentDtos
        );
    }

    private Item findById(long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Предмет не найден"));

    }

    @Override
    @Transactional
    public ItemDto crateItem(ItemCreateDto itemCreateDto, long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Владелец с ID " + userId + " не найден"));
        Item item = ItemMapper.createToItem(itemCreateDto);

        item.setOwner(owner);

        if (itemCreateDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.getReferenceById(itemCreateDto.getRequestId()));
        }


        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        Item existingItem = findById(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getRequest() != null) {
            existingItem.setRequest(ItemRequestMapper.toItemRequest(itemDto.getRequest()));
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }


    @Override
    public List<ItemWithBookingsDto> getAllItems(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Item> items = itemRepository.findAllByOwnerId(userId, pageable);
        List<Long> itemIds = items.getContent().stream()
                .map(Item::getId)
                .toList();

        Map<Long, List<Comment>> commentsByItemId = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        List<Booking> allBookings = bookingRepository.findByItemIdInAndStatus(
                itemIds,
                BookingStatus.APPROVED
        );

        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<Booking>> pastBookingsByItem = allBookings.stream()
                .filter(b -> b.getEnd().isBefore(now) || b.getEnd().isEqual(now))
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Map<Long, List<Booking>> futureBookingsByItem = allBookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        return items.getContent().stream()
                .map(item -> {
                    Booking last = pastBookingsByItem.getOrDefault(item.getId(), List.of())
                            .stream()
                            .max(Comparator.comparing(Booking::getEnd))
                            .orElse(null);

                    Booking next = futureBookingsByItem.getOrDefault(item.getId(), List.of())
                            .stream()
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    List<CommentDto> commentDtos = commentsByItemId
                            .getOrDefault(item.getId(), List.of())
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .toList();

                    return ItemMapper.toItemWithBookingsDto(
                            item,
                            BookingMapper.toBookingShortDto(last),
                            BookingMapper.toBookingShortDto(next),
                            commentDtos
                    );
                })
                .toList();
    }

    public CommentDto createComment(long itemId, long userId, CommentDto commentDto) {
        if (!bookingRepository.userCanCreateComment(userId, itemId, LocalDateTime.now())) {
            throw new ValidationException("Пользователь не пользовался данной вещью");
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(findById(itemId));
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Комментарий написал несуществующий пользователь")));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text.isBlank()) return List.of();
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.searchByText(text, pageable).getContent().stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
