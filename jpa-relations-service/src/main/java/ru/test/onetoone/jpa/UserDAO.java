package ru.test.onetoone.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.onetoone.entity.Address;
import ru.test.onetoone.entity.User;

import java.util.Optional;

/**
 * DAO для доступа к User
 */
public interface UserDAO extends JpaRepository<User, Long> {

    Optional<User> findByAddress(Address address);
}
