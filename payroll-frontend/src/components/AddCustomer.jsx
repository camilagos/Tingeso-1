import { useState } from "react";
import { useNavigate } from "react-router-dom";
import customerService from "../services/customer.service";

import {
  Box,
  TextField,
  Button,
  Typography
} from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";

const AddUser = () => {
  const [user, setUser] = useState({
    name: "",
    email: "",
    rut: "",
    password: "",
    phone: "",
    birthDate: "",
    monthVisits: 0,
    isAdmin: false,
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setUser((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {
      const res = await customerService.save(user);
  
      if (res.status === 200) {
        localStorage.setItem("preLoginEmail", user.email);
        localStorage.setItem("preLoginPassword", user.password);
        alert("Usuario registrado con éxito");
        navigate("/login");
      } else {
        alert("No se pudo registrar el usuario.");
      }
    } catch (err) {
      console.error(err);
      alert("Error al registrar usuario");
    }
  };
  

  return (
    <Box sx={{ maxWidth: 600, margin: "auto", mt: 5 }}>
      <Typography variant="h5" gutterBottom>
        Registrar Usuario
      </Typography>
      <form onSubmit={handleSubmit}>
        <TextField fullWidth margin="normal" name="name" label="Nombre" onChange={handleChange} required />
        <TextField fullWidth margin="normal" name="email" label="Email" onChange={handleChange} required />
        <TextField fullWidth margin="normal" name="rut" label="RUT" onChange={handleChange} required />
        <TextField fullWidth margin="normal" name="password" type="password" label="Contraseña" onChange={handleChange} required />
        <TextField fullWidth margin="normal" name="phone" label="Teléfono" onChange={handleChange} required />
        <TextField fullWidth margin="normal" name="birthDate" type="date" InputLabelProps={{ shrink: true }} label="Fecha de nacimiento" onChange={handleChange} required />
        <Button type="submit" variant="contained" color="primary" startIcon={<SaveIcon />}>
          Registrar
        </Button>
      </form>
    </Box>
  );
};

export default AddUser;
