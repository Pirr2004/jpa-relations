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

    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY)
    private User user;

    /*@OneToOne(fetch = FetchType.LAZY)
    private User2 user2;*/

    private String street;

    private String houseNumber;

}
