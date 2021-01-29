package ru.test.onetomany.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Счет
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="accaunt")
public class Accaunt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Owner owner;

}
