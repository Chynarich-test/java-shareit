package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ExistException("Пользователь с таким email уже существует");
        }
        User newUser = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.save(newUser));
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.userToDto(getUserPrivate(id));
    }

    private User getUserPrivate(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User existingUser = getUserPrivate(userId);

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        String newEmail = userDto.getEmail();
        if (newEmail != null) {
            if (!newEmail.equals(existingUser.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new ExistException("Пользователь с таким email уже существует");
                }
            }
            existingUser.setEmail(newEmail);
        }

        return UserMapper.userToDto(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
