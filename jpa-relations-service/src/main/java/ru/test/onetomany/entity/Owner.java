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

    @Column
    private String name;
}
