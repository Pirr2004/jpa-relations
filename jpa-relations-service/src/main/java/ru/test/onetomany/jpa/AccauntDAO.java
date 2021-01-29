package ru.test.onetomany.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.onetomany.entity.Accaunt;

/**
 * DAO для доступа к Student
 */
public interface AccauntDAO extends JpaRepository<Accaunt, Long> {


}
