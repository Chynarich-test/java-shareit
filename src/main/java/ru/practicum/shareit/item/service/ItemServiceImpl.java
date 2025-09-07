package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemWithBookingsDto getItem(long id) {
        Item item = getItemPrivate(id);

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

    private Item getItemPrivate(long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Предмет не найден"));

    }

    @Override
    @Transactional
    public ItemDto crateItem(ItemDto itemDto, long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Владелец с ID " + userId + " не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        Item existingItem = getItemPrivate(itemId);

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

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }


    //Мне, честно говоря, это вообще не нравится тут N+1, но в дополнительных советах ментора написано, что так и надо
    @Override
    public List<ItemWithBookingsDto> getAllItems(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        Map<Long, List<Comment>> commentsByItemId = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream()
                .map(item -> {
                    Booking last = bookingRepository
                            .findLastBooking(item.getId(), LocalDateTime.now(), PageRequest.of(0, 1))
                            .stream()
                            .findFirst()
                            .orElse(null);

                    Booking next = bookingRepository
                            .findNextBooking(item.getId(), LocalDateTime.now(), PageRequest.of(0, 1))
                            .stream()
                            .findFirst()
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
        comment.setItem(getItemPrivate(itemId));
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Комментарий написал несуществующий пользователь")));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) return List.of();
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
