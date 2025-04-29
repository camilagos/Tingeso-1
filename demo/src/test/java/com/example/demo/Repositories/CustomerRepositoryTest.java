package com.example.demo.Repositories;

import com.example.demo.Entities.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void whenFindByRut_thenReturnCustomer() {
        // given
        CustomerEntity customer = new CustomerEntity();
        customer.setName("Carlos");
        customer.setRut("12.345.678-9");
        customer.setEmail("carlos@example.com");
        customer.setPassword("1234");
        customer.setPhone("999999999");
        customer.setBirthDate(LocalDate.of(1990, 1, 1));
        customer.setAdmin(false);
        entityManager.persistAndFlush(customer);

        // when
        CustomerEntity found = customerRepository.findByRut("12.345.678-9");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("carlos@example.com");
    }

    @Test
    public void whenFindByEmail_thenReturnCustomer() {
        // given
        CustomerEntity customer = new CustomerEntity();
        customer.setName("Lucía");
        customer.setRut("22.222.222-2");
        customer.setEmail("lucia@example.com");
        customer.setPassword("abcd");
        customer.setPhone("888888888");
        customer.setBirthDate(LocalDate.of(1995, 5, 15));
        customer.setAdmin(false);
        entityManager.persistAndFlush(customer);

        // when
        CustomerEntity found = customerRepository.findByEmailAndPassword("lucia@example.com", "abcd");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getRut()).isEqualTo("22.222.222-2");
    }

    @Test
    public void whenFindAllByRutIn_thenReturnMatchingCustomers() {
        // given
        CustomerEntity c1 = new CustomerEntity();
        c1.setName("Mario");
        c1.setRut("11.111.111-1");
        c1.setEmail("mario@example.com");
        c1.setPassword("pass");
        c1.setPhone("777777777");
        c1.setBirthDate(LocalDate.of(1992, 3, 3));
        c1.setAdmin(false);

        CustomerEntity c2 = new CustomerEntity();
        c2.setName("Ana");
        c2.setRut("22.222.222-2");
        c2.setEmail("ana@example.com");
        c2.setPassword("pass");
        c2.setPhone("666666666");
        c2.setBirthDate(LocalDate.of(1988, 8, 8));
        c2.setAdmin(false);

        entityManager.persist(c1);
        entityManager.persist(c2);
        entityManager.flush();

        // when
        List<CustomerEntity> result = customerRepository.findAllByRutIn(List.of("11.111.111-1", "22.222.222-2"));

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CustomerEntity::getName).containsExactlyInAnyOrder("Mario", "Ana");
    }
}
