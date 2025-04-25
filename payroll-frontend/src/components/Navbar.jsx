import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import Sidemenu from "./Sidemenu";

export default function Navbar() {
  const [open, setOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // Verifica si hay usuario logueado en localStorage
    const user = localStorage.getItem("user");
    setIsLoggedIn(!!user);
  }, []);

  const toggleDrawer = (open) => (event) => {
    setOpen(open);
  };

  const handleLogin = () => navigate("/login");
  const handleRegister = () => navigate("/register");

  const handleLogout = () => {
    localStorage.removeItem("user");
    setIsLoggedIn(false);
    navigate("/");
  };
  

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
        {isLoggedIn && (
        <Box sx={{ display: "flex", gap: 1 }}>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            sx={{ mr: 2 }}
            onClick={toggleDrawer(true)}
          >
            <MenuIcon />
          </IconButton>
        </Box>
      )}


          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            KartingRM: Sistema de Gestión de Karting
          </Typography>

          {/* Botones solo si NO está logueado */}
          {!isLoggedIn && (
            <>
              <Button color="inherit" onClick={handleLogin}>
                Iniciar sesión
              </Button>
              <Button color="inherit" onClick={handleRegister}>
                Registrarse
              </Button>
            </>
          )}
          {isLoggedIn && (
            <>
            <Button color="inherit" onClick={handleLogout}>
            Cerrar sesión
          </Button>
            </>
            )}
        </Toolbar>
      </AppBar>

      {/* Drawer solo si hay sesión iniciada */}
      {isLoggedIn && <Sidemenu open={open} toggleDrawer={toggleDrawer} />}
    </Box>
  );
}