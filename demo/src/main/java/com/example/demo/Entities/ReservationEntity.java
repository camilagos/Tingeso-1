package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reservation")
@Data
@AllArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String rutUser;
    private String rutsUsers;
    private LocalDateTime reservationDate;
    private int lapsOrTime;
    private int numberPeople;

    @Column(name = "group_detail", columnDefinition = "TEXT")
    private String groupDetail;

    public ReservationEntity() {
    }

    public ReservationEntity(String rutUser, String rutsUsers, LocalDateTime reservationDate, int lapsOrTime, int numberPeople, String groupDetail) {
        this.rutUser = rutUser;
        this.rutsUsers = rutsUsers;
        this.reservationDate = reservationDate;
        this.lapsOrTime = lapsOrTime;
        this.numberPeople = numberPeople;
        this.groupDetail = groupDetail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRutUser() {
        return rutUser;
    }

    public void setRutUser(String rutUser) {
        this.rutUser = rutUser;
    }

    public String getRutsUsers() {
        return rutsUsers;
    }

    public void setRutsUsers(String rutsUsers) {
        this.rutsUsers = rutsUsers;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public int getLapsOrTime() {
        return lapsOrTime;
    }

    public void setLapsOrTime(int lapsOrTime) {
        this.lapsOrTime = lapsOrTime;
    }

    public int getNumberPeople() {
        return numberPeople;
    }

    public void setNumberPeople(int numberPeople) {
        this.numberPeople = numberPeople;
    }

    public String getGroupDetail() {
        return groupDetail;
    }

    public void setGroupDetail(String groupDetail) {
        this.groupDetail = groupDetail;
    }
}