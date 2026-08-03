package com.example.Ecomm.repository;

import com.example.Ecomm.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserUsernameOrderByDefaultAddressDescCreatedAtDesc(String username);

    List<Address> findByUserIdOrderByDefaultAddressDescCreatedAtDesc(Long userId);

    Optional<Address> findByIdAndUserUsername(Long id, String username);

    Optional<Address> findByIdAndUserId(Long id, Long userId);

    long countByUserUsername(String username);

    long countByUserId(Long userId);

    Optional<Address> findByUserUsernameAndDefaultAddressTrue(String username);
}
