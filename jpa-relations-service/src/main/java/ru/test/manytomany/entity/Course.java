package ru.test.manytomany.entity;

import lombok.*;
import javax.persistence.*;


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

    private String cname;
}
