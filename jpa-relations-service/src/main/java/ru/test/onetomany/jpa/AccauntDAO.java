package ru.test.onetomany.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.test.manytomany.entity.Student;
import ru.test.onetomany.entity.Accaunt;
import ru.test.onetomany.entity.Owner;

import java.util.List;

/**
 * DAO для доступа к Student
 */
public interface AccauntDAO extends JpaRepository<Accaunt, Long> {

    List<Accaunt> findAllByOwner(Owner owner);

}
