package ru.test.manytomany.entity;

import lombok.*;
import javax.persistence.*;
import java.util.List;
import java.util.Set;


/**
 * Курс
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "courseList")
    private Set<Student> studentList;

    private String cname;
}
