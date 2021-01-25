package ru.test.manytomany.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.test.manytomany.entity.Course;
import ru.test.manytomany.entity.Student;

import java.util.List;
import java.util.Optional;

/**
 * DAO для доступа к Student
 */
public interface StudentDAO extends JpaRepository<Student, Long> {

    Optional<Student> findBySname(String sname);

    @Query("select st from Student st join st.courseList scl where scl.id in :courseListIds")
    List<Student> findAllByCourseIds(@Param("courseListIds") Iterable<Long> courseListIds);

}
