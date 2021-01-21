package ru.test.manytomany.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.manytomany.entity.Course;
import ru.test.manytomany.entity.Student;

import java.util.Optional;

/**
 * DAO для доступа к Student
 */
public interface StudentDAO extends JpaRepository<Student, Long> {

    Optional<Student> findBySname(String sname);
}
