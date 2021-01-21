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
@Table(name ="user2")
public class User2 {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    String name;
}
