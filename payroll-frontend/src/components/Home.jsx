const Home = () => {
  return (
    <div>
      <h1>KartingRM: Sistema de Gestión de Reservas de Karting</h1>
      <p>
        KartingRM es una aplicación web diseñada para facilitar la gestión de reservas, control de horarios y generación de reportes en un kartódromo.
        El sistema permite registrar usuarios, aplicar descuentos automáticos por grupo, frecuencia o cumpleaños, y generar comprobantes de pago detallados.
      </p>
      <p>
        <strong>Horario de Atención:</strong><br/>
        - Lunes a Viernes: 14:00 a 22:00 horas.<br/>
        - Sábados, Domingos y Feriados: 10:00 a 22:00 horas.
      </p>
      <p>
        <strong>Tarifas y Duración de Reservas:</strong><br/>
        - 10 vueltas o máximo 10 minutos en pista: $15.000<br/>
        - 15 vueltas o máximo 15 minutos en pista: $20.000<br/>
        - 20 vueltas o máximo 20 minutos en pista: $25.000
      </p>
      <p>
        <strong>Descuentos Disponibles:</strong><br/>
        <u>Por número de personas:</u><br/>
        - 3 a 5 personas: 10%<br/>
        - 6 a 10 personas: 20%<br/>
        - 11 a 15 personas: 30%<br/><br/>
        <u>Por frecuencia de visitas:</u><br/>
        - 2 a 4 visitas al mes: 10%<br/>
        - 5 a 6 visitas al mes: 20%<br/>
        - 7 o más visitas al mes: 30%<br/><br/>
        <u>Promociones especiales:</u><br/>
        - 50% de descuento para clientes que celebran su cumpleaños el día de la reserva (aplicable en grupos de 3 a 5 personas para 1 cumpleañero, y en grupos de 6 a 10 personas para hasta 2 cumpleañeros).
      </p>
      <p>
        La aplicación ha sido desarrollada utilizando{" "}
        <a href="https://spring.io/projects/spring-boot" target="_blank" rel="noreferrer">Spring Boot</a> para el backend,{" "}
        <a href="https://react.dev" target="_blank" rel="noreferrer">React</a> para el frontend y{" "}
        <a href="https://www.postgresql.org/" target="_blank" rel="noreferrer">PostgreSQL</a> como base de datos.
      </p>
    </div>
  );
};

export default Home;
