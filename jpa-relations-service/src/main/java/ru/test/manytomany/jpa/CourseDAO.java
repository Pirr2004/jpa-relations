package ru.test.manytomany.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.manytomany.entity.Course;

import java.util.Optional;

/**
 * DAO для доступа к Course
 */
public interface CourseDAO extends JpaRepository<Course, Long> {

    Optional<Course> findByCname(String cname);
}
