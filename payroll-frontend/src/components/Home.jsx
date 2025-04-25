const Home = () => {
  return (
    <div>
      <h1>KartingRM: Sistema de Gestión de Reservas de Karting</h1>
      <p>
        KartingRM es una aplicación web diseñada para facilitar la gestión de reservas, control de horarios y generación de reportes en un kartódromo. 
        El sistema permite registrar usuarios, aplicar descuentos automáticos por grupo, frecuencia o cumpleaños, y generar comprobantes de pago detallados.
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
