package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("userInMemoryStorage")
public class UserInMemoryStorage implements UserStorage {
    private final List<User> userList = new ArrayList<>();

    @Override
    public User create(User element) {
        checkExistEmail(element);
        userList.add(element);
        return getOne(element.getId());
    }

    @Override
    public User update(User newElement) {
        checkExistEmail(newElement);
        User user = getOne(newElement.getId());

        if (newElement.getName() != null) {
            user.setName(newElement.getName());
        }

        if (newElement.getEmail() != null) {
            user.setEmail(newElement.getEmail());
        }

        return user;
    }

    @Override
    public User getOne(long id) {
        Optional<User> optUser = userList.stream().filter(user -> user.getId() == id).findFirst();
        if (optUser.isEmpty()) throw new NotFoundException("Пользователь не найден");
        return optUser.get();
    }

    @Override
    public void delete(long id) {
        userList.remove(getOne(id));
    }

    private void checkExistEmail(User element) {
        if (userList.stream().anyMatch(user -> user.getEmail().equals(element.getEmail()))) {
            throw new ExistException("Пользователь с таким email уже существует");
        }
    }
}
