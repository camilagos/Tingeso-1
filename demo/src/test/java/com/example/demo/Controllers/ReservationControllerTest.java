package com.example.demo.Controllers;

import com.example.demo.Entities.ReservationEntity;
import com.example.demo.Services.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    public void createReservation_ShouldReturnReservation() throws Exception {
        ReservationEntity mockReservation = new ReservationEntity();
        mockReservation.setId(1L);
        mockReservation.setRutUser("12345678-9");

        given(reservationService.makeReservation(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .willReturn(mockReservation);

        String json = """
                {
                    "rutUser": "12345678-9",
                    "rutsUsers": "87654321-0",
                    "reservationDate": "2025-05-01T12:00:00",
                    "lapsOrTime": 2
                }
                """;

        mockMvc.perform(post("/reservation/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isAdmin", "true")
                        .param("customPrice", "5000")
                        .param("specialDiscount", "10")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void getReservationById_ShouldReturnReservation() throws Exception {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(1L);
        reservation.setRutUser("99999999-9");

        given(reservationService.getReservationById(1L)).willReturn(reservation);

        mockMvc.perform(get("/reservation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rutUser", is("99999999-9")));
    }

    @Test
    public void updateReservation_ShouldReturnUpdatedReservation() throws Exception {
        ReservationEntity updated = new ReservationEntity();
        updated.setId(1L);
        updated.setRutUser("11111111-1");

        given(reservationService.updateReservation(Mockito.any())).willReturn(updated);

        String json = """
                {
                    "id": 1,
                    "rutUser": "11111111-1",
                    "rutsUsers": "22222222-2",
                    "reservationDate": "2025-05-02T15:00:00",
                    "lapsOrTime": 3
                }
                """;

        mockMvc.perform(put("/reservation/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rutUser", is("11111111-1")));
    }

    @Test
    public void deleteReservation_ShouldReturnNoContent() throws Exception {
        Mockito.when(reservationService.deleteReservation(1L)).thenReturn(true);

        mockMvc.perform(delete("/reservation/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    public void getReservationsByDate_ShouldReturnList() throws Exception {
        ReservationEntity res = new ReservationEntity();
        res.setId(1L);

        given(reservationService.getReservationsByDate(Mockito.any())).willReturn(List.of(res));

        mockMvc.perform(get("/reservation/date/2025-05-01T12:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    public void getIncomeFromLapsOrTime_ShouldReturnMap() throws Exception {
        Map<String, Map<String, Double>> mockResponse = Map.of(
                "Enero", Map.of("Tiempo", 10000.0)
        );

        given(reservationService.incomeFromLapsOrTime(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)))
                .willReturn(mockResponse);

        mockMvc.perform(get("/reservation/income-lapsOrTime")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Enero.Tiempo", is(10000.0)));
    }

    @Test
    public void getIncomePerPerson_ShouldReturnMap() throws Exception {
        Map<String, Map<String, Double>> mockResponse = Map.of(
                "Febrero", Map.of("Personas", 5000.0)
        );

        given(reservationService.incomePerPerson(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28)))
                .willReturn(mockResponse);

        mockMvc.perform(get("/reservation/income-persons")
                        .param("startDate", "2025-02-01")
                        .param("endDate", "2025-02-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Febrero.Personas", is(5000.0)));
    }

    @Test
    public void getAllReservations_ShouldReturnList() throws Exception {
        ReservationEntity res1 = new ReservationEntity();
        res1.setId(1L);
        ReservationEntity res2 = new ReservationEntity();
        res2.setId(2L);

        given(reservationService.getAllReservations()).willReturn(List.of(res1, res2));

        mockMvc.perform(get("/reservation/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getAllReservationsByDuration_ShouldReturnList() throws Exception {
        // Simula dos resultados del servicio
        Map<String, Object> reservation1 = new HashMap<>();
        reservation1.put("lapsOrTime", 10);
        reservation1.put("total", 2);

        Map<String, Object> reservation2 = new HashMap<>();
        reservation2.put("lapsOrTime", 15);
        reservation2.put("total", 5);

        List<Map<String, Object>> mockList = List.of(reservation1, reservation2);

        given(reservationService.getAllReservationsByDuration()).willReturn(mockList);

        mockMvc.perform(get("/reservation/allByDuration")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].lapsOrTime", is(10)))
                .andExpect(jsonPath("$[0].total", is(2)))
                .andExpect(jsonPath("$[1].lapsOrTime", is(15)))
                .andExpect(jsonPath("$[1].total", is(5)));
    }

}
