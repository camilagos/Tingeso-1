package com.example.demo.Services;

import com.example.demo.Entities.CustomerEntity;
import com.example.demo.Entities.ReservationEntity;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Repositories.ReservationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ReservationService service;

    @Test
    void testCalculateBasePrice() {
        assertThat(service.calculateBasePrice(10)).isEqualTo(15000);
        assertThat(service.calculateBasePrice(15)).isEqualTo(20000);
        assertThat(service.calculateBasePrice(20)).isEqualTo(25000);
    }

    @Test
    void testCalculateDiscountNumberPeople() {
        assertThat(service.calculateDiscountNumberPeople(2)).isEqualTo(0);
        assertThat(service.calculateDiscountNumberPeople(5)).isEqualTo(10);
        assertThat(service.calculateDiscountNumberPeople(10)).isEqualTo(20);
        assertThat(service.calculateDiscountNumberPeople(15)).isEqualTo(30);
    }

    @Test
    void testCalculateDiscountFrecuentorDays() {
        CustomerEntity c1 = new CustomerEntity(); c1.setMonthVisits(1);
        CustomerEntity c2 = new CustomerEntity(); c2.setMonthVisits(3);
        CustomerEntity c3 = new CustomerEntity(); c3.setMonthVisits(5);
        CustomerEntity c4 = new CustomerEntity(); c4.setMonthVisits(7);
        Map<CustomerEntity, Integer> discounts = service.calculateDiscountFrecuentorDays(List.of(c1, c2, c3, c4));
        assertThat(discounts.get(c1)).isEqualTo(0);
        assertThat(discounts.get(c2)).isEqualTo(10);
        assertThat(discounts.get(c3)).isEqualTo(20);
        assertThat(discounts.get(c4)).isEqualTo(30);
    }

    @Test
    void testGetBirthdayCustomers() {
        LocalDate today = LocalDate.of(2025, 4, 20);
        CustomerEntity bday = new CustomerEntity();
        bday.setBirthDate(LocalDate.of(2000, 4, 20));
        CustomerEntity other = new CustomerEntity();
        other.setBirthDate(LocalDate.of(2001, 1, 1));
        Set<CustomerEntity> result = service.getBirthdayCustomers(List.of(bday, other), today);
        assertThat(result).containsExactly(bday);
    }

    @Test
    void testCalculateEndTime() {
        LocalDateTime start = LocalDateTime.of(2025, 4, 20, 14, 0);
        assertThat(service.calculateEndTime(start, 10)).isEqualTo(start.plusMinutes(30));
        assertThat(service.calculateEndTime(start, 15)).isEqualTo(start.plusMinutes(35));
        assertThat(service.calculateEndTime(start, 20)).isEqualTo(start.plusMinutes(40));
    }

    @Test
    void testGetMonth() {
        assertThat(service.getMonth("2025-02")).isEqualTo("febrero");
        assertThat(service.getMonth("2025-04")).isEqualTo("abril");
    }

    @Test
    void testGetReservationById() {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(1L);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(entity));
        ReservationEntity found = service.getReservationById(1L);
        assertThat(found.getId()).isEqualTo(1L);
    }

    @Test
    void testUpdateReservation() {
        ReservationEntity entity = new ReservationEntity();
        when(reservationRepository.save(entity)).thenReturn(entity);
        assertThat(service.updateReservation(entity)).isEqualTo(entity);
    }

    @Test
    void testDeleteReservation() throws Exception {
        doNothing().when(reservationRepository).deleteById(1L);
        assertThat(service.deleteReservation(1L)).isTrue();
    }

    @Test
    void testSendVoucherByEmail() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        service.sendVoucherByEmail(List.of("test@mail.com"), new byte[]{1,2,3});
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void whenReservationOverlaps_thenThrowException() {
        ReservationEntity existing = new ReservationEntity();
        existing.setReservationDate(LocalDateTime.of(2025, 4, 20, 12, 0));
        existing.setLapsOrTime(10);

        ReservationEntity nueva = new ReservationEntity();
        nueva.setReservationDate(LocalDateTime.of(2025, 4, 20, 12, 15));
        nueva.setLapsOrTime(10);
        nueva.setRutUser("1");
        nueva.setRutsUsers("");

        when(reservationRepository.findAll()).thenReturn(List.of(existing));

        assertThrows(IllegalArgumentException.class, () ->
                service.makeReservation(nueva, false, null, null)
        );
    }

    @Test
    void whenRutsAreNotFound_thenThrowException() {
        ReservationEntity r = new ReservationEntity();
        r.setRutUser("1");
        r.setRutsUsers("2,3");
        r.setReservationDate(LocalDateTime.of(2025, 4, 20, 10, 0));
        r.setLapsOrTime(10);
        r.setNumberPeople(3);

        when(reservationRepository.findAll()).thenReturn(List.of());
        when(customerRepository.findAllByRutIn(any())).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () ->
                service.makeReservation(r, false, null, null)
        );
    }


    @Test
    void whenSpecialDiscountIsHigher_thenItIsApplied() {
        CustomerEntity c = new CustomerEntity();
        c.setName("Juan");
        c.setRut("1");
        c.setBirthDate(LocalDate.of(1990, 1, 1));
        c.setEmail("test@mail.com");
        c.setMonthVisits(1);

        ReservationEntity r = new ReservationEntity();
        r.setRutUser("1");
        r.setRutsUsers("");
        r.setReservationDate(LocalDateTime.of(2025, 4, 20, 10, 0));
        r.setLapsOrTime(10);
        r.setNumberPeople(1);

        when(reservationRepository.findAll()).thenReturn(List.of());
        when(customerRepository.findAllByRutIn(any())).thenReturn(List.of(c));
        when(customerRepository.findByRut("1")).thenReturn(c);
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        ReservationEntity result = service.makeReservation(r, true, 10000.0, 40.0);

        assertThat(result).isNotNull();
        assertThat(result.getGroupDetail()).contains("40");
    }


    @Test
    void whenCustomerHasBirthday_then50PercentDiscountApplied() {
        CustomerEntity c = new CustomerEntity();
        c.setName("Ana");
        c.setRut("1");
        c.setBirthDate(LocalDate.of(1995, 4, 20)); // cumpleaños
        c.setEmail("ana@mail.com");
        c.setMonthVisits(5); // también tiene 20%

        ReservationEntity r = new ReservationEntity();
        r.setRutUser("1");
        r.setRutsUsers("");
        r.setReservationDate(LocalDateTime.of(2025, 4, 20, 10, 0)); // hoy cumple
        r.setLapsOrTime(10);
        r.setNumberPeople(5); // permite 1 cumpleaños

        when(reservationRepository.findAll()).thenReturn(List.of());
        when(customerRepository.findAllByRutIn(any())).thenReturn(List.of(c));
        when(customerRepository.findByRut("1")).thenReturn(c);
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        ReservationEntity result = service.makeReservation(r, false, null, null);
        assertThat(result.getGroupDetail()).contains("50"); // 50% aplicado
    }


    @Test
    void whenMailFails_thenThrowRuntimeException() throws MessagingException {
        CustomerEntity c = new CustomerEntity();
        c.setName("Luisa");
        c.setRut("1");
        c.setBirthDate(LocalDate.of(2000, 1, 1));
        c.setEmail("luisa@mail.com");
        c.setMonthVisits(0);

        ReservationEntity r = new ReservationEntity();
        r.setRutUser("1");
        r.setRutsUsers("");
        r.setReservationDate(LocalDateTime.of(2025, 4, 20, 10, 0));
        r.setLapsOrTime(10);
        r.setNumberPeople(1);

        when(reservationRepository.findAll()).thenReturn(List.of());
        when(customerRepository.findAllByRutIn(any())).thenReturn(List.of(c));
        when(customerRepository.findByRut("1")).thenReturn(c);
        when(reservationRepository.save(any())).thenReturn(r);
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail error"));

        assertThrows(RuntimeException.class, () ->
                service.makeReservation(r, false, null, null)
        );
    }

    @Test
    void whenDetailIsEmpty_thenPdfStillGenerated() {
        ReservationEntity r = new ReservationEntity();
        r.setId(1L);
        r.setReservationDate(LocalDateTime.of(2025, 4, 20, 10, 0));
        r.setLapsOrTime(10);
        r.setNumberPeople(1);
        r.setRutUser("123");

        CustomerEntity c = new CustomerEntity(); c.setName("Test");
        when(customerRepository.findByRut("123")).thenReturn(c);

        byte[] pdf = service.generatePDF(r, new ArrayList<>());
        assertThat(pdf).isNotNull();
        assertThat(pdf.length).isGreaterThan(0);
    }


    @Test
    void incomeFromLapsOrTime_handlesMultipleLapsVariants() throws Exception {
        ReservationEntity r1 = new ReservationEntity();
        r1.setReservationDate(LocalDateTime.of(2025, 3, 10, 10, 0));
        r1.setLapsOrTime(10);
        r1.setGroupDetail("[[\\\"Carlos\\\",15000]]".replace("\\\"", "\""));

        ReservationEntity r2 = new ReservationEntity();
        r2.setReservationDate(LocalDateTime.of(2025, 3, 15, 15, 0));
        r2.setLapsOrTime(15);
        r2.setGroupDetail("[[\\\"Luisa\\\",20000]]".replace("\\\"", "\""));

        when(reservationRepository.findByReservationDateBetween(
                LocalDate.of(2025, 3, 1).atStartOfDay(), LocalDate.of(2025, 3, 31).atTime(23, 59)
        )).thenReturn(List.of(r1, r2));

        Map<String, Map<String, Double>> result = service.incomeFromLapsOrTime(
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 31)
        );

        assertThat(result).containsKeys("10 vueltas o máx. 10 minutos", "15 vueltas o máx. 15 minutos");
        assertThat(result.get("10 vueltas o máx. 10 minutos").get("marzo")).isEqualTo(15000.0);
        assertThat(result.get("15 vueltas o máx. 15 minutos").get("marzo")).isEqualTo(20000.0);
        assertThat(result.get("TOTAL").get("marzo")).isEqualTo(35000.0);
    }

    @Test
    void incomePerPerson_allRangesCovered() throws Exception {
        ReservationEntity r1 = new ReservationEntity();
        r1.setReservationDate(LocalDateTime.of(2025, 5, 1, 10, 0));
        r1.setNumberPeople(2);
        r1.setGroupDetail("[[\\\"A\\\",10000]]".replace("\\\"", "\""));

        ReservationEntity r2 = new ReservationEntity();
        r2.setReservationDate(LocalDateTime.of(2025, 5, 2, 11, 0));
        r2.setNumberPeople(4);
        r2.setGroupDetail("[[\\\"B\\\",15000]]".replace("\\\"", "\""));

        ReservationEntity r3 = new ReservationEntity();
        r3.setReservationDate(LocalDateTime.of(2025, 5, 3, 12, 0));
        r3.setNumberPeople(7);
        r3.setGroupDetail("[[\\\"C\\\",18000]]".replace("\\\"", "\""));

        ReservationEntity r4 = new ReservationEntity();
        r4.setReservationDate(LocalDateTime.of(2025, 5, 4, 13, 0));
        r4.setNumberPeople(12);
        r4.setGroupDetail("[[\\\"D\\\",21000]]".replace("\\\"", "\""));

        when(reservationRepository.findByReservationDateBetween(
                LocalDate.of(2025, 5, 1).atStartOfDay(), LocalDate.of(2025, 5, 31).atTime(23, 59)
        )).thenReturn(List.of(r1, r2, r3, r4));

        Map<String, Map<String, Double>> result = service.incomePerPerson(
                LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)
        );

        assertThat(result.get("1-2 personas").get("mayo")).isEqualTo(10000.0);
        assertThat(result.get("3-5 personas").get("mayo")).isEqualTo(15000.0);
        assertThat(result.get("6-10 personas").get("mayo")).isEqualTo(18000.0);
        assertThat(result.get("11-15 personas").get("mayo")).isEqualTo(21000.0);
        assertThat(result.get("TOTAL").get("mayo")).isEqualTo(64000.0);
    }

    @Test
    void getReservationsByDate_returnsCorrectList() {
        LocalDateTime date = LocalDateTime.of(2025, 6, 1, 10, 0);
        ReservationEntity r = new ReservationEntity();
        r.setReservationDate(date);

        when(reservationRepository.findByReservationDate(date)).thenReturn(List.of(r));

        List<ReservationEntity> result = service.getReservationsByDate(date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReservationDate()).isEqualTo(date);
    }

    @Test
    void getAllReservations_returnsAll() {
        ReservationEntity r1 = new ReservationEntity();
        ReservationEntity r2 = new ReservationEntity();

        when(reservationRepository.findAll()).thenReturn(List.of(r1, r2));

        List<ReservationEntity> result = service.getAllReservations();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(r1, r2);
    }
}
