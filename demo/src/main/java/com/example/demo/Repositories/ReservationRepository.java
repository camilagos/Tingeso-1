package com.example.demo.Repositories;

import com.example.demo.Entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    ReservationEntity findByReservationDate(LocalDateTime reservationDate);

    List<ReservationEntity> findByReservationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
