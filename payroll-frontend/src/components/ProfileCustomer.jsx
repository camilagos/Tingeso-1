import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Typography,
  Paper,
  Divider
} from "@mui/material";

const ProfileCustomer = () => {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const userData = localStorage.getItem("user");
    if (!userData) {
      alert("Debes iniciar sesión para ver tu perfil");
      navigate("/login");
    } else {
      setUser(JSON.parse(userData));
    }
  }, [navigate]);

  if (!user) return null;

  return (
    <Box sx={{ maxWidth: 600, margin: "auto", mt: 5 }}>
      <Paper elevation={3} sx={{ p: 3 }}>
        <Typography variant="h5" gutterBottom>
          Perfil del Usuario
        </Typography>
        <Divider sx={{ mb: 2 }} />

        <Typography><strong>Nombre:</strong> {user.name}</Typography>
        <Typography><strong>Email:</strong> {user.email}</Typography>
        <Typography><strong>RUT:</strong> {user.rut}</Typography>
        <Typography><strong>Teléfono:</strong> {user.phone}</Typography>
        <Typography><strong>Fecha de nacimiento:</strong> {user.birthDate}</Typography>
      </Paper>
    </Box>
  );
};

export default ProfileCustomer;
