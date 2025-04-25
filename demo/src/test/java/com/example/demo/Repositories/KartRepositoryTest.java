package com.example.demo.Repositories;


import com.example.demo.Entities.KartEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class KartRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private KartRepository kartRepository;

    @Test
    public void whenFindByAvailableTrue_thenReturnAvailableKarts() {
        KartEntity kart1 = new KartEntity("K001", true, "Modelo A");
        KartEntity kart2 = new KartEntity("K002", false, "Modelo B");
        entityManager.persist(kart1);
        entityManager.persist(kart2);
        entityManager.flush();

        List<KartEntity> availableKarts = kartRepository.findByAvailable(true);

        assertThat(availableKarts).hasSize(1);
        assertThat(availableKarts.get(0).getCode()).isEqualTo("K001");
    }

    @Test
    public void whenFindByAvailableFalse_thenReturnUnavailableKarts() {
        KartEntity kart1 = new KartEntity("K003", true, "Modelo C");
        KartEntity kart2 = new KartEntity("K004", false, "Modelo D");
        entityManager.persist(kart1);
        entityManager.persist(kart2);
        entityManager.flush();

        List<KartEntity> unavailableKarts = kartRepository.findByAvailable(false);

        assertThat(unavailableKarts).hasSize(1);
        assertThat(unavailableKarts.get(0).getCode()).isEqualTo("K004");
    }
}
