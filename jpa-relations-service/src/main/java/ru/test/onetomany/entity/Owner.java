package ru.test.onetomany.entity;

import lombok.*;
import ru.test.onetoone.entity.Address;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
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

    @OneToMany
    private Set<Accaunt> accaunts;

    @Column
    private String name;
}
