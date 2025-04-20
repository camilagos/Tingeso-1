package com.example.demo.Services;

import com.example.demo.Entities.CustomerEntity;
import com.example.demo.Repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    public CustomerEntity saveUser(CustomerEntity customer) {
        // CustomerEntity existingUserEmail = customerRepository.findByEmail(customer.getEmail());
        CustomerEntity existingUserRut = customerRepository.findByRut(customer.getRut());
        // if (existingUserEmail != null) {
            // return null; // User already exists
        // }
        if (existingUserRut != null) {
            return null; // User already exists
        }

        CustomerEntity user = new CustomerEntity(customer.getName(), customer.getEmail(), customer.getRut(),
                customer.getPassword(), customer.getPhone(), customer.getBirthDate(), customer.getMonthVisits(),
                customer.isAdmin());
        return customerRepository.save(user);
    }

    public CustomerEntity getUserById(Long id) {
        return customerRepository.findById(id).get();
    }

    public CustomerEntity updateUser(CustomerEntity user) {
        return customerRepository.save(user);
    }

    public boolean deleteUser(Long id) throws Exception {
        try {
            customerRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public CustomerEntity getUserByRut(String rut) {
        return customerRepository.findByRut(rut);
    }

    public int login(String email, String password) {
        CustomerEntity customer = customerRepository.findByEmail(email);
        if (customer != null) {
            if (password.equals(customer.getPassword())) {
                if (customer.isAdmin()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
        return -1;
    }
}
