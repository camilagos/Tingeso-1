package com.example.demo.Controllers;

import com.example.demo.Entities.CustomerEntity;
import com.example.demo.Services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @PostMapping("/")
    public ResponseEntity<CustomerEntity> saveUser(@RequestBody CustomerEntity customer) {
        CustomerEntity user = customerService.saveUser(customer);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerEntity> getUserById(@PathVariable Long id) {
        CustomerEntity user = customerService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/")
    public ResponseEntity<CustomerEntity> updateUser(@RequestBody CustomerEntity user) {
        CustomerEntity userUpdated = customerService.updateUser(user);
        return ResponseEntity.ok(userUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUserById(@PathVariable Long id) throws Exception {
        var isDeleted = customerService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<CustomerEntity> getUserByRut(@PathVariable String rut) {
        CustomerEntity user = customerService.getUserByRut(rut);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerEntity> login(@RequestBody CustomerEntity customer) {
        return ResponseEntity.ok(customerService.login(customer));
    }
}
