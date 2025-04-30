package com.example.demo.Services;

import com.example.demo.Entities.KartEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.example.demo.Entities.CustomerEntity;
import com.example.demo.Entities.ReservationEntity;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Repositories.ReservationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationService {
    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    KartService kartService;

    @Autowired
    JavaMailSender mailSender;

    public final ObjectMapper mapper = new ObjectMapper();


    public double calculateBasePrice(int lapsOrTime) {
        int price = 0;
        if (lapsOrTime == 10) {
            price = 15000;
        } else if (lapsOrTime == 15) {
            price = 20000;
        } else if (lapsOrTime == 20) {
            price = 25000;
        }
        return price;
    }

    public boolean isHoliday(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();

        // Feriados fijos
        List<MonthDay> feriadosFijos = List.of(
                MonthDay.of(1, 1),   // Año Nuevo
                MonthDay.of(5, 1),   // Día del Trabajador
                MonthDay.of(9, 18),  // Independencia Chile
                MonthDay.of(9, 19),  // Glorias del Ejército
                MonthDay.of(12, 25)  // Navidad
        );

        MonthDay diaActual = MonthDay.from(date);

        return day == DayOfWeek.SATURDAY ||
                day == DayOfWeek.SUNDAY ||
                feriadosFijos.contains(diaActual);
    }


    public int calculateDiscountNumberPeople(int numberPeople) {
        int discount = -1;
        if (numberPeople < 3) {
            discount = 0;
        } else if (numberPeople <= 5) {
            discount = 10;
        } else if (numberPeople <= 10) {
            discount = 20;
        } else if (numberPeople <= 15) {
            discount = 30;
        }
        return discount;
    }

    public Map<CustomerEntity, Integer> calculateDiscountFrequentCustomers(List<CustomerEntity> customers, LocalDateTime reservationDate) {
        Map<CustomerEntity, Integer> discounts = new HashMap<>();

        LocalDateTime startOfMonth = reservationDate.withDayOfMonth(1).toLocalDate().atStartOfDay();

        // Reservaciones del mes
        List<ReservationEntity> reservationsThisMonth = reservationRepository.findByReservationDateBetween(startOfMonth, reservationDate);

        // Contar las reservas de un mes en las que esta cada usuario
        for (CustomerEntity customer : customers) {
            String rut = customer.getRut();
            int visitCount = 0;

            for (ReservationEntity reservation : reservationsThisMonth) {
                boolean isMainCustomer = rut.equals(reservation.getRutUser());

                boolean isInParticipants = false;
                if (reservation.getRutsUsers() != null && !reservation.getRutsUsers().isBlank()) {
                    isInParticipants = Arrays.stream(reservation.getRutsUsers().split(","))
                            .map(String::trim)
                            .anyMatch(rut::equals);
                }

                if (isMainCustomer || isInParticipants) {
                    visitCount++;
                }
            }

            int discount;
            if (visitCount >= 7) {
                discount = 30;
            } else if (visitCount >= 5) {
                discount = 20;
            } else if (visitCount >= 2) {
                discount = 10;
            } else {
                discount = 0;
            }

            discounts.put(customer, discount);
        }

        return discounts;
    }


    public Set<CustomerEntity> getBirthdayCustomers(List<CustomerEntity> customers, LocalDate date) {
        return customers.stream()
                .filter(c -> c.getBirthDate().getMonth() == date.getMonth() && c.getBirthDate().getDayOfMonth() == date.getDayOfMonth())
                .collect(Collectors.toSet());
    }

    public LocalDateTime calculateEndTime(LocalDateTime start, int lapsOrTime) {
        int minutes = 0;
        if (lapsOrTime == 10) {
            minutes = 30;
        } else if (lapsOrTime == 15) {
            minutes = 35;
        } else if (lapsOrTime == 20) {
            minutes = 40;
        }
        return start.plusMinutes(minutes);
    }

    public byte[] generatePDF(ReservationEntity reservation, List<List<Object>> detail) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);

        // Abrir el documento
        document.open();

        // Agregar contenido al documento
        com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD);
        document.add(new Paragraph("Comprobante de Reserva - KartingRM", font));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Código de reserva: RES-" + reservation.getId()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = reservation.getReservationDate().format(formatter);
        document.add(new Paragraph("Fecha y hora: " + fechaFormateada));
        document.add(new Paragraph("N° de vueltas o Tiempo máximo: " + reservation.getLapsOrTime()));
        document.add(new Paragraph("Cantidad de personas: " + reservation.getNumberPeople()));
        document.add(new Paragraph("Persona que hizo la reservación: " + customerRepository.findByRut(reservation.getRutUser()).getName()));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        Stream.of("Nombre", "Tarifa base", "Desc. Grupo", "Desc. Frec.", "Desc. Cumple.", "Desc. Especial",
                        "Desc. Aplicado", "Subtotal", "IVA (19%)", "Total")
                .forEach(h -> {
                    PdfPCell cell = new PdfPCell(new Phrase(h));
                    cell.setBackgroundColor(Color.LIGHT_GRAY);
                    table.addCell(cell);
                });

        // Agregar los datos de los participantes
        for (List<Object> fila : detail) {
            for (Object col : fila) {
                table.addCell(String.valueOf(col));
            }
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    public void sendVoucherByEmail(List<String> emails, byte[] pdf) throws MessagingException {
        for (String email : emails) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Comprobante de Reserva KartingRM");
            helper.setText("Estimado cliente, adjuntamos el comprobante de su reserva en formato PDF. Preséntelo el día de su visita.");
            helper.addAttachment("comprobante_reserva.pdf", new ByteArrayResource(pdf));
            mailSender.send(message);
        }
    }

    private boolean isWithinWorkingHours(LocalDate date, LocalTime startTime, LocalTime endTime) {
        DayOfWeek day = date.getDayOfWeek();

        LocalTime apertura;
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY || isHoliday(date)) {
            apertura = LocalTime.of(10, 0); // Fin de semana o feriado
        } else {
            apertura = LocalTime.of(14, 0); // Lunes a Viernes
        }
        LocalTime cierre = LocalTime.of(22, 0);

        return !startTime.isBefore(apertura) && !endTime.isAfter(cierre);
    }

    public ReservationEntity makeReservation(ReservationEntity reservation, Boolean isAdmin, Double customPrice, Double specialDiscount) {
        boolean isAdminUser = Boolean.TRUE.equals(isAdmin);
        LocalDateTime newStart = reservation.getReservationDate();
        LocalDateTime newEnd = calculateEndTime(newStart, reservation.getLapsOrTime());

        // Verificar que el horario esté dentro del horario de atención
        if (!isWithinWorkingHours(newStart.toLocalDate(), newStart.toLocalTime(), newEnd.toLocalTime())) {
            throw new IllegalArgumentException("La reserva está fuera del horario de atención.");
        }

        // Verificar si hay suficientes karts
        List<KartEntity> kartsDisponibles = kartService.getKartsByAvailability(true);
        if (reservation.getNumberPeople() > kartsDisponibles.size()) {
            throw new IllegalArgumentException("No hay suficientes karts disponibles para esta reserva.");
        }

        // Verificar si ya existe una reserva en el mismo horario
        LocalDate date = newStart.toLocalDate();
        List<ReservationEntity> reservations = reservationRepository.findByReservationDateBetween(
                date.atStartOfDay(),
                date.atTime(23, 59, 59)
        );

        for (ReservationEntity r : reservations) {
            LocalDateTime start = r.getReservationDate();
            LocalDateTime end = calculateEndTime(start, r.getLapsOrTime());
            if (newStart.isBefore(end) && start.isBefore(newEnd)) {
                throw new IllegalArgumentException("Ya existe una reserva que se solapa en ese horario.");
            }
        }

        // Verificar que los RUTs de los participantes estén registrados
        List<String> allRuts = new ArrayList<>();
        allRuts.add(reservation.getRutUser());
        List<String> extraRuts = Arrays.stream(reservation.getRutsUsers().split(","))
                .map(String::trim)
                .filter(r -> !r.isEmpty())
                .toList();
        allRuts.addAll(extraRuts);

        List<CustomerEntity> participants = customerRepository.findAllByRutIn(allRuts);
        if (participants.size() != allRuts.size()) {
            throw new IllegalArgumentException("Uno o más RUTs no están registrados");
        }

        // Determinar el precio base
        double basePrice = (isAdminUser && customPrice != null && customPrice > 0) ? customPrice : calculateBasePrice(reservation.getLapsOrTime());

        boolean isWeekend = isHoliday(LocalDate.from(reservation.getReservationDate()));
        int groupDiscount = calculateDiscountNumberPeople(reservation.getNumberPeople());
        Map<CustomerEntity, Integer> visitDiscounts = calculateDiscountFrequentCustomers(participants, reservation.getReservationDate());
        Set<CustomerEntity> birthdayClients = getBirthdayCustomers(participants, LocalDate.from(reservation.getReservationDate()));

        // Calcular el número de descuentos por cumpleaños permitidos
        int numberPeople = reservation.getNumberPeople();
        int birthdayDiscountsAllowed = 0;
        if (numberPeople >= 3 && numberPeople <= 5) {
            birthdayDiscountsAllowed = 1;
        } else if (numberPeople >= 6 && numberPeople <= 15) {
            birthdayDiscountsAllowed = 2;
        }


        List<List<Object>> detailParticipants = new ArrayList<>();
        double totalPaymentWithTax = 0;
        int birthdayApplied = 0;

        // Aplicar el recargo del fin de semana
        if (isWeekend) basePrice *= 1.15;

        // Determinar que descuentos aplicar
        for (CustomerEntity c : participants) {
            double price = basePrice;

            int visitDiscount = visitDiscounts.getOrDefault(c, 0);
            boolean birthday = birthdayClients.contains(c) && birthdayApplied < birthdayDiscountsAllowed;


            int discountApplied;

            // Calcular el descuento aplicado
            discountApplied = Math.max(groupDiscount, visitDiscount);

            if (birthday) {
                discountApplied = 50; // cumpleaños tiene prioridad
                birthdayApplied++;
            }

            // Comparar con el descuento del administrador
            if (isAdminUser && specialDiscount != null && specialDiscount > 0) {
                if (specialDiscount > discountApplied) {
                    discountApplied = specialDiscount.intValue();
                }
            }

            // Aplicar el descuento final
            price *= (1 - discountApplied / 100.0);


            double iva = price * 0.19;
            double totalWithTax = price + iva;
            totalPaymentWithTax += totalWithTax;

            // Hacer una lista con los datos de cada participante
            detailParticipants.add(List.of(
                    c.getName(),
                    (int) Math.round(basePrice * 100) / 100,
                    groupDiscount,
                    visitDiscount,
                    birthday ? "Sí" : "No",
                    (specialDiscount != null) ? specialDiscount.intValue() : 0,
                    discountApplied,
                    (int) Math.round(price * 100) / 100,
                    (int) Math.round(iva * 100) / 100,
                    (int) Math.round(totalWithTax * 100) / 100
            ));
        }

        ReservationEntity reservationNew = new ReservationEntity(
                reservation.getRutUser(),
                reservation.getRutsUsers(),
                reservation.getReservationDate(),
                reservation.getLapsOrTime(),
                reservation.getNumberPeople(),
                null
        );

        // Hacer un json con los detalles de la reserva de cada participante
        ObjectMapper mapper = new ObjectMapper();
        String detailJson = null;
        try {
            detailJson = mapper.writeValueAsString(detailParticipants);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        reservationNew.setGroupDetail(detailJson);

        reservationNew = reservationRepository.save(reservationNew);

        // Generar el PDF
        byte[] pdf = generatePDF(reservationNew, detailParticipants);

        // Enviar el voucher por correo electrónico a los participantes
        List<String> emails = participants.stream()
                .map(CustomerEntity::getEmail)
                .filter(email -> email != null && !email.isBlank())
                .toList();

        try {
            sendVoucherByEmail(emails, pdf);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return reservationNew;
    }

    public String getMonth(String yyyyMM) {
        Month month = Month.of(Integer.parseInt(yyyyMM.substring(5)));
        return month.getDisplayName(TextStyle.FULL, new Locale("es"));
    }

    public Map<String, Map<String, Double>> incomeFromLapsOrTime(LocalDate startDate, LocalDate endDate) {
        List<ReservationEntity> reservations = reservationRepository.findByReservationDateBetween(
                startDate.atStartOfDay(), endDate.atTime(23, 59));

        Map<String, Map<String, Double>> intermediate = new TreeMap<>();
        for (ReservationEntity r : reservations) {
            String monthReservation = r.getReservationDate().getYear() + "-" + String.format("%02d", r.getReservationDate().getMonthValue());
            String lapsOrTimeReservation = r.getLapsOrTime() + " vueltas o máx. " + r.getLapsOrTime() + " minutos";
            double totalReservation = 0;
            try {
                List<List<Object>> detail = mapper.readValue(r.getGroupDetail(), new TypeReference<List<List<Object>>>() {
                });
                for (List<Object> row : detail) {
                    if (!row.isEmpty()) {
                        Object tarifa = row.get(row.size() - 1);
                        if (tarifa instanceof Number total) {
                            totalReservation += total.doubleValue();
                        }
                    }
                }
            } catch (Exception e) {
                continue;
            }

            intermediate.computeIfAbsent(lapsOrTimeReservation, k -> new TreeMap<>());
            intermediate.get(lapsOrTimeReservation).put(monthReservation,
                    intermediate.get(lapsOrTimeReservation).getOrDefault(monthReservation, 0.0) + totalReservation);
        }

        // Generar lista de todos los meses entre startDate y endDate
        Set<String> allMonths = new TreeSet<>();
        LocalDate current = startDate.withDayOfMonth(1);
        while (!current.isAfter(endDate.withDayOfMonth(1))) {
            allMonths.add(current.getYear() + "-" + String.format("%02d", current.getMonthValue()));
            current = current.plusMonths(1);
        }

        Map<String, Map<String, Double>> result = new LinkedHashMap<>();
        Map<String, Double> totalPerMonth = new TreeMap<>();

        // Agregar explícitamente todas las categorías de vueltas/tiempo
        List<String> allLapsCategories = List.of(
                "10 vueltas o máx. 10 minutos",
                "15 vueltas o máx. 15 minutos",
                "20 vueltas o máx. 20 minutos"
        );

        for (String category : allLapsCategories) {
            intermediate.computeIfAbsent(category, k -> new TreeMap<>());
        }


        for (String row : intermediate.keySet()) {
            Map<String, Double> rowData = new LinkedHashMap<>();
            double totalRow = 0;
            for (String month : allMonths) {
                double value = intermediate.get(row).getOrDefault(month, 0.0);
                rowData.put(getMonth(month), value);
                totalPerMonth.put(getMonth(month), totalPerMonth.getOrDefault(getMonth(month), 0.0) + value);
                totalRow += value;
            }
            rowData.put("Total", totalRow);
            result.put(row, rowData);
        }

        Map<String, Double> totalRows = new LinkedHashMap<>();
        double total = 0;
        for (String nameMonth : allMonths.stream().map(this::getMonth).toList()) {
            double valorMes = totalPerMonth.getOrDefault(nameMonth, 0.0);
            totalRows.put(nameMonth, valorMes);
            total += valorMes;
        }
        totalRows.put("Total", total);
        result.put("TOTAL", totalRows);

        return result;
    }

    public Map<String, Map<String, Double>> incomePerPerson(LocalDate startDate, LocalDate endDate) {
        List<ReservationEntity> reservations = reservationRepository.findByReservationDateBetween(
                startDate.atStartOfDay(), endDate.atTime(23, 59));

        Map<String, Map<String, Double>> intermediate = new TreeMap<>();
        for (ReservationEntity r : reservations) {
            String monthReservation = r.getReservationDate().getYear() + "-" + String.format("%02d", r.getReservationDate().getMonthValue());
            int numberPeople = r.getNumberPeople();
            String range;
            if (numberPeople <= 2) range = "1-2 personas";
            else if (numberPeople <= 5) range = "3-5 personas";
            else if (numberPeople <= 10) range = "6-10 personas";
            else range = "11-15 personas";

            double totalReservation = 0;
            try {
                List<List<Object>> detail = mapper.readValue(r.getGroupDetail(), new TypeReference<List<List<Object>>>() {
                });
                for (List<Object> row : detail) {
                    if (!row.isEmpty()) {
                        Object tarifa = row.get(row.size() - 1);
                        if (tarifa instanceof Number total) {
                            totalReservation += total.doubleValue();
                        }
                    }
                }
            } catch (Exception e) {
                continue;
            }

            intermediate.computeIfAbsent(range, k -> new TreeMap<>());
            intermediate.get(range).put(monthReservation,
                    intermediate.get(range).getOrDefault(monthReservation, 0.0) + totalReservation);
        }

        Set<String> allMonths = new TreeSet<>();
        LocalDate current = startDate.withDayOfMonth(1);
        while (!current.isAfter(endDate.withDayOfMonth(1))) {
            allMonths.add(current.getYear() + "-" + String.format("%02d", current.getMonthValue()));
            current = current.plusMonths(1);
        }

        Map<String, Map<String, Double>> result = new LinkedHashMap<>();
        Map<String, Double> totalPerMonth = new TreeMap<>();

        List<String> allGroupCategories = List.of(
                "1-2 personas",
                "3-5 personas",
                "6-10 personas",
                "11-15 personas"
        );

        for (String row : allGroupCategories) {
            Map<String, Double> rowData = new LinkedHashMap<>();
            double totalRow = 0;
            for (String month : allMonths) {
                double value = intermediate.getOrDefault(row, new TreeMap<>()).getOrDefault(month, 0.0);
                rowData.put(getMonth(month), value);
                totalRow += value;
            }
            rowData.put("Total", totalRow);
            result.put(row, rowData);
        }

        Map<String, Double> totalRows = new LinkedHashMap<>();
        double total = 0;
        for (String nameMonth : allMonths.stream().map(this::getMonth).toList()) {
            double monthValue = totalPerMonth.getOrDefault(nameMonth, 0.0);
            totalRows.put(nameMonth, monthValue);
            total += monthValue;
        }
        totalRows.put("Total", total);
        result.put("TOTAL", totalRows);

        return result;
    }


    public ReservationEntity getReservationById(Long id) {
        return reservationRepository.findById(id).get();
    }

    public ReservationEntity updateReservation(ReservationEntity reservation) {
        return reservationRepository.save(reservation);
    }

    public boolean deleteReservation(LocalDateTime date) throws Exception {
        try {
            ReservationEntity reservation = reservationRepository.findByReservationDate(date);
            if (reservation == null) {
                throw new Exception("No se encontró la reserva con la fecha proporcionada.");
            }
            Long id = reservation.getId();
            reservationRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ReservationEntity getReservationsByDate(LocalDateTime reservationDate) {
        return reservationRepository.findByReservationDate(reservationDate);
    }

    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Map<String, Object>> getAllReservationsByDuration() {
        List<ReservationEntity> reservations = reservationRepository.findAll();

        List<Map<String, Object>> result = new ArrayList<>();

        for (ReservationEntity r : reservations) {
            LocalDateTime start = r.getReservationDate();
            int laps = r.getLapsOrTime();
            int duration;

            if (laps == 10) duration = 30;
            else if (laps == 15) duration = 35;
            else if (laps == 20) duration = 40;
            else duration = laps + 20;

            LocalDateTime end = start.plusMinutes(duration);

            Optional<CustomerEntity> user = Optional.ofNullable(customerRepository.findByRut(r.getRutUser()));

            Map<String, Object> reservation = new HashMap<>();
            reservation.put("start", start.toString());
            reservation.put("end", end.toString());
            reservation.put("title", user.map(CustomerEntity::getName).orElse(r.getRutUser()));

            result.add(reservation);
        }

        return result;
    }
}
