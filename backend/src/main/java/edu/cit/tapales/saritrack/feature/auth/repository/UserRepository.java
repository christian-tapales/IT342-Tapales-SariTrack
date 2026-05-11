package edu.cit.tapales.saritrack.feature.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.cit.tapales.saritrack.feature.auth.entity.User; 

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Boot will automatically generate the SQL to find users by email
    java.util.Optional<User> findByEmail(String email);
}