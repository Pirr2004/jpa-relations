package ru.test.onetomany.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.onetomany.entity.Owner;

import java.util.List;
import java.util.Optional;

/**
 * DAO для доступа к Student
 */
public interface OwnerDAO extends JpaRepository<Owner, Long> {


}
