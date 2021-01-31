package ru.test.onetoone.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Пользователь.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "user")
    private Address address;

    String name;
}
