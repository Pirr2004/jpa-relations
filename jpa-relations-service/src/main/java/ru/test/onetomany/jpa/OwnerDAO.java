package ru.test.onetomany.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.test.onetomany.entity.Accaunt;
import ru.test.onetomany.entity.Owner;

import java.util.List;
import java.util.Optional;

/**
 * DAO для доступа к Student
 */
public interface OwnerDAO extends JpaRepository<Owner, Long> {


}
