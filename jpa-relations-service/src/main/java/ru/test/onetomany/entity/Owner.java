package ru.test.onetomany.entity;

import lombok.*;
import ru.test.onetoone.entity.Address;

import javax.persistence.*;
import java.util.Set;

/**
 * Владелец
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany( fetch = FetchType.LAZY)
    @JoinColumn(name="owner_fk")
    private Set<Accaunt> account;
}
