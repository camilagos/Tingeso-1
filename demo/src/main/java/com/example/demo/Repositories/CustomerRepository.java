package com.example.demo.Repositories;

import com.example.demo.Entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    CustomerEntity findByRut(String rut);
    CustomerEntity findByEmailAndPassword(String email, String password);
    List<CustomerEntity> findAllByRutIn(List<String> ruts);
}
