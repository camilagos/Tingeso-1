import { useState } from "react";
import { useNavigate } from "react-router-dom";
import customerService from "../services/customer.service";
import {
  Box,
  TextField,
  Button,
  Typography
} from "@mui/material";
import LoginIcon from "@mui/icons-material/Login";

const Login = () => {
  // Precarga desde registro
  const [email, setEmail] = useState(localStorage.getItem("preLoginEmail") || "");
  const [password, setPassword] = useState(localStorage.getItem("preLoginPassword") || "");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await customerService.login({ email, password });

      if (response.status === 200) {
        // Limpia datos temporales de registro
        localStorage.removeItem("preLoginEmail");
        localStorage.removeItem("preLoginPassword");

        // Guarda los datos del usuario logueado
        localStorage.setItem("user", JSON.stringify(response.data));

        alert("Inicio de sesión exitoso");
        window.location.href = "/";
      } 
    } catch (error) {
      if (error.response && error.response.status === 401) {
        alert("Email o contraseña incorrectos");
      } else {
        console.error(error);
        alert("Error al conectarse al servidor. Por favor, intenta más tarde.");
      }
    }
  };

  return (
    <Box sx={{ maxWidth: 400, margin: "auto", mt: 10 }}>
      <Typography variant="h5" gutterBottom>
        Iniciar Sesión
      </Typography>
      <form onSubmit={handleLogin}>
        <TextField
          fullWidth
          margin="normal"
          label="Correo electrónico"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <TextField
          fullWidth
          margin="normal"
          label="Contraseña"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <Button
          type="submit"
          variant="contained"
          color="primary"
          fullWidth
          startIcon={<LoginIcon />}
          sx={{ mt: 2 }}
        >
          Entrar
        </Button>
      </form>
    </Box>
  );
};

export default Login;
