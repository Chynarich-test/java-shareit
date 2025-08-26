package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.basestorage.IdGenerator;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final IdGenerator idGenerator;

    public UserServiceImpl(@Qualifier("userInMemoryStorage") UserStorage userStorage,
                           @Qualifier("inMemoryIdGenerator") IdGenerator idGenerator) {
        this.userStorage = userStorage;
        this.idGenerator = idGenerator;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = UserMapper.dtoToUser(userDto);
        newUser.setId(idGenerator.nextId());
        return UserMapper.userToDto(userStorage.create(newUser));
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.userToDto(userStorage.getOne(id));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        user.setId(userId);
        return UserMapper.userToDto(userStorage.update(user));
    }

    @Override
    public void deleteUser(long id) {
        userStorage.delete(id);
    }
}
