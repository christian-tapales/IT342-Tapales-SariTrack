package edu.cit.tapales.saritrack.feature.auth.repository;

import edu.cit.tapales.saritrack.feature.auth.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail_ShouldReturnUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setName("Test User");
        user.setPassword("hashed_pwd");
        userRepository.save(user);

        // Act
        Optional<User> found = userRepository.findByEmail("test@gmail.com");
        Optional<User> notFound = userRepository.findByEmail("other@gmail.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
        assertFalse(notFound.isPresent());
    }

    @Test
    void testExistsByEmail_ShouldReturnBoolean() {
        // Arrange
        User user = new User();
        user.setEmail("exists@gmail.com");
        user.setName("Exists");
        user.setPassword("pwd");
        userRepository.save(user);

        // Act & Assert
        assertTrue(userRepository.existsByEmail("exists@gmail.com"));
        assertFalse(userRepository.existsByEmail("notexists@gmail.com"));
    }
}
