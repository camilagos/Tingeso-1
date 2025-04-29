package com.example.demo.Services;

import com.example.demo.Entities.CustomerEntity;
import com.example.demo.Repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


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
        System.out.println("Valor admin recibido: " + customer.isAdmin());

        CustomerEntity user = new CustomerEntity(customer.getName(), customer.getEmail(), customer.getRut(),
                customer.getPassword(), customer.getPhone(), customer.getBirthDate(),
                customer.isAdmin());

        System.out.println("Valor admin guardado: " + user.isAdmin());

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

    public CustomerEntity login(CustomerEntity user) {
        CustomerEntity customer = customerRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()
        );

        if (customer == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos"
            );
        }

        return customer;
    }
}
