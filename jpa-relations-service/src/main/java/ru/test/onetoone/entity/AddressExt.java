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
@Table(name ="addressext")
public class AddressExt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    String name;
}
