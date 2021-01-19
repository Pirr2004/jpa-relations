package ru.test.onetoone.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.onetoone.entity.Address;

import java.util.Optional;

/**
 * DAO для доступа к Address
 */
public interface AddressDAO extends JpaRepository<Address, Long> {

    Optional<Address> findByStreet(String street);
}
