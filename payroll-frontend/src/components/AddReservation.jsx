import { useState } from "react";
import { useNavigate } from "react-router-dom";
import reservationService from "../services/reservation.service";
import {
  Box,
  TextField,
  Button,
  Typography,
  MenuItem
} from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";

const AddReservation = () => {
  const user = JSON.parse(localStorage.getItem("user"));
  const isAdmin = user?.admin || false;

  const [form, setForm] = useState({
    rutUser: !isAdmin ? user?.rut || "" : "",
    rutsUsers: "",
    reservationDate: "",
    lapsOrTime: 10,
    numberPeople: 1,
  });
  

  const [customPrice, setCustomPrice] = useState(null);
  const [specialDiscount, setSpecialDiscount] = useState(null);

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: name === "numberPeople" || name === "lapsOrTime" ? parseInt(value) : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await reservationService.create(
        form,
        isAdmin,
        isAdmin ? customPrice : null,
        isAdmin ? specialDiscount : null
      );
      alert("Reserva realizada con éxito");
      navigate("/");
    } catch (err) {
        console.error("Error al crear la reserva:", err);
      
        const message =
          "No se pudo realizar la reserva. Verifica los datos ingresados o intenta con otro horario.";
      
        alert(message);
      }      
  };

  return (
    <Box sx={{ maxWidth: 600, margin: "auto", mt: 5 }}>
      <Typography variant="h5" gutterBottom>
        Crear Nueva Reserva
      </Typography>
      <form onSubmit={handleSubmit}>
        <TextField
          fullWidth
          margin="normal"
          name="rutUser"
          label="RUT del Usuario Principal"
          value={form.rutUser}
          onChange={handleChange}
          required
        />
        <TextField
          fullWidth
          margin="normal"
          name="rutsUsers"
          label="RUTs Adicionales (separados por coma)"
          value={form.rutsUsers}
          onChange={handleChange}
          helperText="Los RUTs ingresados deben corresponder a usuarios registrados en el sistema."
        />
        <TextField
          fullWidth
          margin="normal"
          type="datetime-local"
          name="reservationDate"
          label="Fecha y Hora"
          InputLabelProps={{ shrink: true }}
          value={form.reservationDate}
          onChange={handleChange}
          required
          helperText="Las reservas deben realizarse dentro del horario de atención: lunes a viernes de 14:00 a 22:00 h, fines de semana y feriados de 10:00 a 22:00 h."

        />
        <TextField
          fullWidth
          select
          margin="normal"
          name="lapsOrTime"
          label="Vueltas o Tiempo (min)"
          value={form.lapsOrTime}
          onChange={handleChange}
        >
          <MenuItem value={10}>10 vueltas o 10 min</MenuItem>
          <MenuItem value={15}>15 vueltas o 15 min</MenuItem>
          <MenuItem value={20}>20 vueltas o 20 min</MenuItem>
        </TextField>
        <TextField
          fullWidth
          margin="normal"
          type="number"
          name="numberPeople"
          label="Número de Personas"
          value={form.numberPeople}
          onChange={handleChange}
          inputProps={{ min: 1, max: 15 }}
          required
        />

        {isAdmin && (
          <>
            <TextField
              fullWidth
              margin="normal"
              label="Precio Personalizado (opcional)"
              type="number"
              value={customPrice || ""}
              onChange={(e) => setCustomPrice(parseFloat(e.target.value))}
            />
            <TextField
              fullWidth
              margin="normal"
              label="Descuento Especial % (opcional)"
              type="number"
              value={specialDiscount || ""}
              inputProps={{ min: 0, max: 100 }}
              onChange={(e) => setSpecialDiscount(parseFloat(e.target.value))}
            />
          </>
        )}

        <Button
          type="submit"
          variant="contained"
          color="primary"
          startIcon={<SaveIcon />}
          sx={{ mt: 2 }}
        >
          Reservar
        </Button>
      </form>
    </Box>
  );
};

export default AddReservation;
