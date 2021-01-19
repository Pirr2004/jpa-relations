package ru.test.onetoone.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Адрес
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "address")
    private User user;

    private String street;

    private String houseNumber;

}
