package com.example.demo.Controllers;

import com.example.demo.Entities.ReservationEntity;
import com.example.demo.Services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservation")
@CrossOrigin("*")
public class ReservationController {
    @Autowired
    ReservationService reservationService;

    @PostMapping("/")
    public ResponseEntity<?> createReservation(@RequestBody ReservationEntity reservation,
            @RequestParam(required = false) Boolean isAdmin, @RequestParam(required = false) Double customPrice,
            @RequestParam(required = false) Double specialDiscount) {
        ReservationEntity reservationNew = reservationService.makeReservation(reservation, isAdmin, customPrice,
                specialDiscount);
        return ResponseEntity.ok(reservationNew);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getReservationById(@PathVariable Long id) {
        ReservationEntity reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/")
    public ResponseEntity<ReservationEntity> updateReservation(@RequestBody ReservationEntity reservation) {
        ReservationEntity reservationUpdated = reservationService.updateReservation(reservation);
        return ResponseEntity.ok(reservationUpdated);
    }

    @DeleteMapping("/{reservationDate}")
    public ResponseEntity<Void> deleteReservationByDate(@PathVariable LocalDateTime reservationDate) throws Exception {
        reservationService.deleteReservation(reservationDate);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/date/{reservationDate}")
    public ResponseEntity<ReservationEntity> getReservationsByDate(@PathVariable LocalDateTime reservationDate) {
        ReservationEntity reservation = reservationService.getReservationsByDate(reservationDate);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/income-lapsOrTime")
    public ResponseEntity<Map<String, Map<String, Double>>> getIncomeFromLapsOrTime(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Map<String, Double>> result = reservationService.incomeFromLapsOrTime(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/income-persons")
    public ResponseEntity<Map<String, Map<String, Double>>> getIncomePerPerson(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Map<String, Double>> result = reservationService.incomePerPerson(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReservationEntity>> getAllReservations() {
        List<ReservationEntity> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/allByDuration")
    public ResponseEntity<List<Map<String, Object>>> getAllReservationsByDuration() {
        List<Map<String, Object>> reservations = reservationService.getAllReservationsByDuration();
        return ResponseEntity.ok(reservations);
    }
}

