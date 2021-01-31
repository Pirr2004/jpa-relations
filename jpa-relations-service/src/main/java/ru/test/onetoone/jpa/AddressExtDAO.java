package ru.test.onetoone.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.onetoone.entity.Address;
import ru.test.onetoone.entity.AddressExt;

import java.util.Optional;

/**
 * DAO для доступа к Address
 */
public interface AddressExtDAO extends JpaRepository<AddressExt, Long> {


}
