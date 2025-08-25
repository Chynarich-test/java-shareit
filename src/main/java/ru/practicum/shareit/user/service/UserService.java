package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUser(long id);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long id);
}
