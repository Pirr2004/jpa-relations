package ru.test.entity;

import lombok.*;

import javax.persistence.*;

/**
 *
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FirstEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "first_entity_seq")
    @SequenceGenerator(name = "first_entity_seq", sequenceName = "FIRST_ENTITY_SEQ", allocationSize = 1)
    Long id;

    String name;
}
