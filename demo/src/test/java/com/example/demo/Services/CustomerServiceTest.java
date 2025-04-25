package com.example.demo.Services;

import com.example.demo.Entities.CustomerEntity;
import com.example.demo.Repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void saveUser_returnsSavedUser_whenRutIsUnique() {
        CustomerEntity customer = new CustomerEntity("Test", "test@mail.com", "123", "pass", "12345678",
                LocalDate.of(2000, 1, 1), 0, false);

        when(customerRepository.findByRut("123")).thenReturn(null);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customer);

        CustomerEntity saved = customerService.saveUser(customer);
        assertThat(saved).isEqualTo(customer);
    }

    @Test
    void saveUser_returnsNull_whenRutAlreadyExists() {
        CustomerEntity existing = new CustomerEntity();
        when(customerRepository.findByRut("123")).thenReturn(existing);

        CustomerEntity result = customerService.saveUser(new CustomerEntity("Test", "test@mail.com", "123", "pass", "12345678",
                LocalDate.of(2000, 1, 1), 0, false));

        assertThat(result).isNull();
    }

    @Test
    void getUserById_returnsUserIfExists() {
        CustomerEntity user = new CustomerEntity();
        user.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(user));

        CustomerEntity result = customerService.getUserById(1L);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void updateUser_returnsSavedUser() {
        CustomerEntity user = new CustomerEntity();
        user.setId(2L);

        when(customerRepository.save(user)).thenReturn(user);

        CustomerEntity result = customerService.updateUser(user);
        assertThat(result).isEqualTo(user);
    }

    @Test
    void deleteUser_returnsTrueIfDeleted() throws Exception {
        doNothing().when(customerRepository).deleteById(1L);
        assertThat(customerService.deleteUser(1L)).isTrue();
    }

    @Test
    void deleteUser_throwsExceptionIfFails() {
        doThrow(new RuntimeException("Error")).when(customerRepository).deleteById(99L);
        assertThrows(Exception.class, () -> customerService.deleteUser(99L));
    }

    @Test
    void getUserByRut_returnsUser() {
        CustomerEntity user = new CustomerEntity();
        when(customerRepository.findByRut("123")).thenReturn(user);
        assertThat(customerService.getUserByRut("123")).isEqualTo(user);
    }

    @Test
    void login_returns1_whenAdminCredentialsAreCorrect() {
        CustomerEntity input = new CustomerEntity();
        input.setEmail("admin@mail.com");
        input.setPassword("admin123");

        CustomerEntity stored = new CustomerEntity();
        stored.setEmail("admin@mail.com");
        stored.setPassword("admin123");
        stored.setAdmin(true);

        when(customerRepository.findByEmailAndPassword("admin@mail.com", "admin123")).thenReturn(stored);

        CustomerEntity result = customerService.login(input);
        assertThat(result).isEqualTo(stored);
    }

    @Test
    void login_returns0_whenUserCredentialsAreCorrectAndNotAdmin() {
        CustomerEntity input = new CustomerEntity();
        input.setEmail("user@mail.com");
        input.setPassword("userpass");

        CustomerEntity stored = new CustomerEntity();
        stored.setEmail("user@mail.com");
        stored.setPassword("userpass");
        stored.setAdmin(false);

        when(customerRepository.findByEmailAndPassword("user@mail.com", "userpass")).thenReturn(stored);

        CustomerEntity result = customerService.login(input);
        assertThat(result).isEqualTo(stored);
    }

    @Test
    void login_throwsException_whenUserNotFound() {
        CustomerEntity input = new CustomerEntity();
        input.setEmail("ghost@mail.com");
        input.setPassword("whatever");

        when(customerRepository.findByEmailAndPassword("ghost@mail.com", "whatever"))
                .thenReturn(null);

        assertThatThrownBy(() -> customerService.login(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email o contraseña incorrectos");
    }
}
