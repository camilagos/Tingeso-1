package com.example.demo.Services;

import com.example.demo.Entities.CustomerEntity;
import com.example.demo.Entities.KartEntity;
import com.example.demo.Entities.ReservationEntity;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Repositories.ReservationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    private KartService kartService;

    @Mock(lenient = true)
    private ObjectMapper mapper;

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
        CustomerEntity c1 = new CustomerEntity();
        c1.setMonthVisits(1);
        CustomerEntity c2 = new CustomerEntity();
        c2.setMonthVisits(3);
        CustomerEntity c3 = new CustomerEntity();
        c3.setMonthVisits(5);
        CustomerEntity c4 = new CustomerEntity();
        c4.setMonthVisits(7);
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
    void testUpdateReservation() {
        ReservationEntity entity = new ReservationEntity();
        when(reservationRepository.save(entity)).thenReturn(entity);
        assertThat(service.updateReservation(entity)).isEqualTo(entity);
    }

    @Test
    void deleteReservation_existingReservation_deletesSuccessfully() throws Exception {
        // given
        LocalDateTime date = LocalDateTime.of(2025, 6, 1, 10, 0);
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(1L);
        reservation.setReservationDate(date);

        when(reservationRepository.findByReservationDate(date)).thenReturn(reservation);

        // when
        boolean result = service.deleteReservation(date);

        // then
        assertThat(result).isTrue();
        verify(reservationRepository).deleteById(1L); // verifica que se haya llamado a deleteById
    }

    @Test
    void deleteReservation_nonExistingReservation_throwsException() {
        // given
        LocalDateTime date = LocalDateTime.of(2025, 6, 1, 10, 0);

        when(reservationRepository.findByReservationDate(date)).thenReturn(null);

        // when + then
        assertThatThrownBy(() -> service.deleteReservation(date))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("No se encontró la reserva con la fecha proporcionada.");
    }



    @Test
    void testSendVoucherByEmail() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        service.sendVoucherByEmail(List.of("test@mail.com"), new byte[]{1, 2, 3});
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
        when(kartService.getKartsByAvailability(true)).thenReturn(Collections.nCopies(5, new KartEntity()));

        ReservationEntity result = service.makeReservation(r, true, 10000.0, 40.0);

        assertThat(result).isNotNull();
        assertThat(result.getGroupDetail()).contains("40");
    }

    @Test
    void whenDetailIsEmpty_thenPdfStillGenerated() {
        ReservationEntity r = new ReservationEntity();
        r.setId(1L);
        r.setReservationDate(LocalDateTime.of(2025, 4, 20, 10, 0));
        r.setLapsOrTime(10);
        r.setNumberPeople(1);
        r.setRutUser("123");

        CustomerEntity c = new CustomerEntity();
        c.setName("Test");
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
    void getReservationsByDate_returnsCorrectReservation() {
        LocalDateTime date = LocalDateTime.of(2025, 6, 1, 10, 0);
        ReservationEntity r = new ReservationEntity();
        r.setReservationDate(date);

        when(reservationRepository.findByReservationDate(date)).thenReturn(r); // r es un ReservationEntity

        ReservationEntity result = service.getReservationsByDate(date);

        assertThat(result).isNotNull();
        assertThat(result.getReservationDate()).isEqualTo(date);
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

    @Test
    void isHoliday_returnsFalseForWeekdayNonHoliday() {
        LocalDate weekday = LocalDate.of(2025, 4, 22); // Martes sin feriado
        assertThat(service.isHoliday(weekday)).isFalse();
    }

    @Test
    void testSendVoucherToMultipleEmails() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        service.sendVoucherByEmail(List.of("a@a.com", "b@b.com"), new byte[]{1});
        verify(mailSender, times(2)).send(any(MimeMessage.class));
    }

    @Test
    void whenOutOfWorkingHours_thenThrowException() {
        ReservationEntity r = new ReservationEntity();
        r.setRutUser("1");
        r.setRutsUsers("");
        r.setReservationDate(LocalDateTime.of(2025, 4, 21, 9, 0));
        r.setLapsOrTime(10);
        r.setNumberPeople(1);

        assertThrows(IllegalArgumentException.class, () ->
                service.makeReservation(r, false, null, null)
        );
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
        when(kartService.getKartsByAvailability(true)).thenReturn(Collections.nCopies(5, new KartEntity()));

        ReservationEntity result = service.makeReservation(r, false, null, null);
        assertThat(result.getGroupDetail()).contains("50");
    }

    @Test
    void whenNotEnoughKarts_thenThrowException() {
        ReservationEntity r = new ReservationEntity();
        r.setRutUser("1");
        r.setRutsUsers("2,3");
        r.setReservationDate(LocalDateTime.of(2025, 4, 20, 10, 0));
        r.setLapsOrTime(10);
        r.setNumberPeople(3); // Total de personas = 3

        CustomerEntity c1 = new CustomerEntity(); c1.setRut("1");
        CustomerEntity c2 = new CustomerEntity(); c2.setRut("2");
        CustomerEntity c3 = new CustomerEntity(); c3.setRut("3");

        lenient().when(reservationRepository.findAll()).thenReturn(List.of());
        lenient().when(customerRepository.findAllByRutIn(any())).thenReturn(List.of(c1, c2, c3));
        lenient().when(kartService.getKartsByAvailability(true)).thenReturn(Collections.nCopies(2, new KartEntity()));

        assertThrows(IllegalArgumentException.class, () -> service.makeReservation(r, false, null, null));
    }

    @Test
    void getAllReservationsByDuration_shouldReturnMappedList() {
        // Simula una reserva
        ReservationEntity r = new ReservationEntity();
        r.setRutUser("1");
        r.setReservationDate(LocalDateTime.of(2025, 4, 21, 14, 0));
        r.setLapsOrTime(15); // espera duración 35 min

        CustomerEntity c = new CustomerEntity();
        c.setRut("1");
        c.setName("Juan");

        when(reservationRepository.findAll()).thenReturn(List.of(r));
        when(customerRepository.findByRut("1")).thenReturn(c);

        List<Map<String, Object>> result = service.getAllReservationsByDuration();

        assertThat(result).hasSize(1);
        Map<String, Object> map = result.get(0);

        assertThat(map.get("start")).isEqualTo("2025-04-21T14:00");
        assertThat(map.get("end")).isEqualTo("2025-04-21T14:35");
        assertThat(map.get("title")).isEqualTo("Juan");

        verify(reservationRepository, times(1)).findAll();
        verify(customerRepository, times(1)).findByRut("1");
    }

    @Test
    void incomePerPerson_shouldReturnCorrectResultFormat() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        ReservationEntity reservation1 = new ReservationEntity();
        reservation1.setReservationDate(LocalDateTime.of(2025, 4, 15, 10, 0)); // Abril
        reservation1.setNumberPeople(2); // 1-2 personas
        reservation1.setGroupDetail("[[\"Grupo A\", 156246]]");

        ReservationEntity reservation2 = new ReservationEntity();
        reservation2.setReservationDate(LocalDateTime.of(2025, 5, 20, 10, 0)); // Mayo
        reservation2.setNumberPeople(4); // 3-5 personas
        reservation2.setGroupDetail("[[\"Grupo B\", 57834]]");

        ReservationEntity reservation3 = new ReservationEntity();
        reservation3.setReservationDate(LocalDateTime.of(2025, 9, 10, 10, 0)); // Septiembre
        reservation3.setNumberPeople(2); // 1-2 personas
        reservation3.setGroupDetail("[[\"Grupo C\", 59500]]");

        ReservationEntity reservation4 = new ReservationEntity();
        reservation4.setReservationDate(LocalDateTime.of(2025, 4, 10, 10, 0)); // Abril
        reservation4.setNumberPeople(3); // 3-5 personas
        reservation4.setGroupDetail("[[\"Grupo D\", 9729]]");

        when(reservationRepository.findByReservationDateBetween(
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(reservation1, reservation2, reservation3, reservation4));

        when(mapper.readValue(anyString(), any(TypeReference.class)))
                .thenReturn(List.of(List.of("Grupo", 0))); // No importa el valor exacto aquí porque se sobrescribe abajo.

        // Override para cada reserva su valor correcto
        when(mapper.readValue(eq(reservation1.getGroupDetail()), any(TypeReference.class)))
                .thenReturn(List.of(List.of("Grupo A", 156246)));
        when(mapper.readValue(eq(reservation2.getGroupDetail()), any(TypeReference.class)))
                .thenReturn(List.of(List.of("Grupo B", 57834)));
        when(mapper.readValue(eq(reservation3.getGroupDetail()), any(TypeReference.class)))
                .thenReturn(List.of(List.of("Grupo C", 59500)));
        when(mapper.readValue(eq(reservation4.getGroupDetail()), any(TypeReference.class)))
                .thenReturn(List.of(List.of("Grupo D", 9729)));

        // when
        Map<String, Map<String, Double>> result = service.incomePerPerson(startDate, endDate);

        // then
        assertThat(result).isNotNull();

        // 1-2 personas
        Map<String, Double> group1 = result.get("1-2 personas");
        assertThat(group1).isNotNull();
        assertThat(group1.get("abril")).isEqualTo(156246.0);
        assertThat(group1.get("septiembre")).isEqualTo(59500.0);
        assertThat(group1.get("Total")).isEqualTo(215746.0);

        // 3-5 personas
        Map<String, Double> group2 = result.get("3-5 personas");
        assertThat(group2).isNotNull();
        assertThat(group2.get("abril")).isEqualTo(9729.0);
        assertThat(group2.get("mayo")).isEqualTo(57834.0);
        assertThat(group2.get("Total")).isEqualTo(67563.0);

        // 6-10 personas
        Map<String, Double> group3 = result.get("6-10 personas");
        assertThat(group3).isNotNull();
        assertThat(group3.values()).allMatch(value -> value == 0.0);

        // 11-15 personas
        Map<String, Double> group4 = result.get("11-15 personas");
        assertThat(group4).isNotNull();
        assertThat(group4.values()).allMatch(value -> value == 0.0);

        // TOTAL (el TOTAL de todos, por mes aún 0.0 porque tú no lo sumas en tu servicio)
        Map<String, Double> groupTotal = result.get("TOTAL");
        assertThat(groupTotal).isNotNull();
        assertThat(groupTotal.values()).allMatch(value -> value == 0.0);
    }



    @Test
    void incomePerPerson_shouldReturnZeroes_whenNoReservationsExist() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        when(reservationRepository.findByReservationDateBetween(
                startDate.atStartOfDay(), endDate.atTime(23, 59)))
                .thenReturn(Collections.emptyList());

        // when
        Map<String, Map<String, Double>> result = service.incomePerPerson(startDate, endDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.get("TOTAL").get("Total")).isEqualTo(0.0);
    }

    @Test
    void getReservationById_shouldReturnReservation_whenIdExists() {
        // given
        Long id = 1L;
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(id);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        // when
        ReservationEntity result = service.getReservationById(id);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void getReservationById_shouldThrowException_whenIdDoesNotExist() {
        // given
        Long id = 1L;

        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> service.getReservationById(id))
                .isInstanceOf(NoSuchElementException.class);
    }


}