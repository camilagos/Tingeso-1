package com.example.demo.Repositories;

import com.example.demo.Entities.ReservationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReservationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void whenFindByReservationDate_thenReturnMatchingReservations() {
        // given
        LocalDateTime now = LocalDateTime.of(2025, 4, 21, 15, 0);
        ReservationEntity reservation = new ReservationEntity();
        reservation.setReservationDate(now);
        entityManager.persistAndFlush(reservation);

        // when
        List<ReservationEntity> found = reservationRepository.findByReservationDate(now);

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getReservationDate()).isEqualTo(now);
    }

    @Test
    public void whenFindByReservationDateBetween_thenReturnReservationsInRange() {
        // given
        LocalDateTime date1 = LocalDateTime.of(2025, 4, 20, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2025, 4, 21, 12, 0);
        LocalDateTime date3 = LocalDateTime.of(2025, 4, 22, 14, 0);

        ReservationEntity res1 = new ReservationEntity();
        res1.setReservationDate(date1);

        ReservationEntity res2 = new ReservationEntity();
        res2.setReservationDate(date2);

        ReservationEntity res3 = new ReservationEntity();
        res3.setReservationDate(date3);

        entityManager.persist(res1);
        entityManager.persist(res2);
        entityManager.persist(res3);
        entityManager.flush();

        // when
        List<ReservationEntity> found = reservationRepository.findByReservationDateBetween(
                LocalDateTime.of(2025, 4, 20, 0, 0),
                LocalDateTime.of(2025, 4, 21, 23, 59)
        );

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(ReservationEntity::getReservationDate)
                .containsExactlyInAnyOrder(date1, date2);
    }
}

