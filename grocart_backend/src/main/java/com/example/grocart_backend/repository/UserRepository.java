package com.example.grocart_backend.repository;



import com.example.grocart_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    /**
     * Finds a user by their username.
     * @param username The username to search for.
     * @return Optional containing the User if found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user exists with the given username.
     * @param username The username to check.
     * @return True if exists, false otherwise.
     */
    Boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email.
     * @param email The email to check.
     * @return True if exists, false otherwise.
     */
    Boolean existsByEmail(String email);
}