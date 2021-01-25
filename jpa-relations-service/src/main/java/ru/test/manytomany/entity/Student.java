package ru.test.manytomany.entity;

import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Студент
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_course",
            joinColumns = { @JoinColumn(name = "fk_student") },
            inverseJoinColumns = { @JoinColumn(name = "fk_course") })
    private Set<Course> courseList;

    private String sname;
}
