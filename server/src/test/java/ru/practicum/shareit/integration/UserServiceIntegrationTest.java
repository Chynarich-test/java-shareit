package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Test
    void updateUser_Success() {
        UserDto userDto = createTestUser("Вася пупкин", "vasiliy@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setName("Обновленный васька");
        updateDto.setEmail("vasiliy.updated@example.com");

        UserDto updatedUser = userService.updateUser(userDto.getId(), updateDto);

        assertEquals("Обновленный васька", updatedUser.getName());
        assertEquals("vasiliy.updated@example.com", updatedUser.getEmail());
        assertEquals(userDto.getId(), updatedUser.getId());
    }

    @Test
    void createAndGetUser_Success() {
        UserDto userDto = createTestUser("Тестовый пользователь", "test@example.com");

        assertNotNull(userDto.getId());
        assertEquals("Тестовый пользователь", userDto.getName());
        assertEquals("test@example.com", userDto.getEmail());

        UserDto retrievedUser = userService.getUser(userDto.getId());
        assertEquals(userDto.getId(), retrievedUser.getId());
        assertEquals(userDto.getName(), retrievedUser.getName());
        assertEquals(userDto.getEmail(), retrievedUser.getEmail());
    }

    @Test
    void deleteUser_Success() {
        UserDto userDto = createTestUser("Удаляемый пользователь", "delete@example.com");

        userService.deleteUser(userDto.getId());

        assertThrows(NotFoundException.class, () -> userService.getUser(userDto.getId()));
    }

    @Test
    void updateUser_PartialUpdate_Success() {
        UserDto userDto = createTestUser("Исходный пользователь", "original@example.com");

        UserDto nameUpdateDto = new UserDto();
        nameUpdateDto.setName("Новое имя");

        UserDto nameUpdated = userService.updateUser(userDto.getId(), nameUpdateDto);
        assertEquals("Новое имя", nameUpdated.getName());
        assertEquals("original@example.com", nameUpdated.getEmail());

        UserDto emailUpdateDto = new UserDto();
        emailUpdateDto.setEmail("new@example.com");

        UserDto emailUpdated = userService.updateUser(userDto.getId(), emailUpdateDto);
        assertEquals("Новое имя", emailUpdated.getName());
        assertEquals("new@example.com", emailUpdated.getEmail());
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        createTestUser("Первый пользователь", "duplicate@example.com");

        final UserDto duplicateUser = new UserDto();
        duplicateUser.setName("Второй пользователь");
        duplicateUser.setEmail("duplicate@example.com");

        assertThrows(ExistException.class, () -> userService.createUser(duplicateUser));
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Призрак");
        updateDto.setEmail("ghost@example.com");

        final long nonExistentUserId = 999L;

        assertThrows(NotFoundException.class, () -> userService.updateUser(nonExistentUserId, updateDto));
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsException() {
        createTestUser("Пользователь 1", "existing.email@example.com");
        UserDto userToUpdate = createTestUser("Пользователь 2", "another.email@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setEmail("existing.email@example.com");

        assertThrows(ExistException.class, () -> userService.updateUser(userToUpdate.getId(), updateDto));
    }

    @Test
    void updateUser_WithSameEmail_Success() {
        UserDto userDto = createTestUser("Исходный пользователь", "same.email@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setName("Имя обновлено");
        updateDto.setEmail("same.email@example.com");

        UserDto updatedUser = userService.updateUser(userDto.getId(), updateDto);

        assertEquals("Имя обновлено", updatedUser.getName());
        assertEquals("same.email@example.com", updatedUser.getEmail());
    }
}