import * as React from "react";
import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import HomeIcon from "@mui/icons-material/Home";
import { useNavigate } from "react-router-dom";

import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import EventIcon from "@mui/icons-material/Event";
import BarChartIcon from "@mui/icons-material/BarChart";
import GroupIcon from "@mui/icons-material/Group";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import Typography from "@mui/material/Typography";
import DirectionsCarIcon from "@mui/icons-material/DirectionsCar";




export default function Sidemenu({ open, toggleDrawer }) {
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem("user"));
  const isAdmin = user?.admin || false;

  const listOptions = () => (
    <Box
      role="presentation"
      onClick={toggleDrawer(false)}
    >
      <List>
        <ListItemButton onClick={() => navigate("/")}>
          <ListItemIcon><HomeIcon /></ListItemIcon>
          <ListItemText primary="Inicio" />
        </ListItemButton>

        <Divider />

        <ListItemButton onClick={() => navigate("/profile")}>
          <ListItemIcon><AccountCircleIcon /></ListItemIcon>
          <ListItemText primary="Mi perfil" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/reservation")}>
          <ListItemIcon><EventIcon /></ListItemIcon>
          <ListItemText primary="Hacer una reserva" />
        </ListItemButton>

        {isAdmin && (
          <>
            <Divider />
            <Typography sx={{ pl: 2, pt: 1 }} variant="caption" color="text.secondary">
              Administrador
            </Typography>

            <ListItemButton onClick={() => navigate("/reportByLapsOrTime")}>
              <ListItemIcon><BarChartIcon /></ListItemIcon>
              <ListItemText primary="Reporte por vueltas o tiempo" />
            </ListItemButton>

            <ListItemButton onClick={() => navigate("/reportByPerson")}>
              <ListItemIcon><GroupIcon /></ListItemIcon>
              <ListItemText primary="Reporte por cantidad de personas" />
            </ListItemButton>

            <ListItemButton onClick={() => navigate("/Rack")}>
              <ListItemIcon><CalendarMonthIcon /></ListItemIcon>
              <ListItemText primary="Rack semanal" />
            </ListItemButton>

            <Divider />
            <ListItemButton onClick={() => navigate("/karts")}>
              <ListItemIcon><DirectionsCarIcon /></ListItemIcon>
              <ListItemText primary="Gestión de Karts" />
            </ListItemButton>
          </>
        )}
      </List>

    </Box>
  );

  return (
    <div>
      <Drawer anchor={"left"} open={open} onClose={toggleDrawer(false)}>
        {listOptions()}
      </Drawer>
    </div>
  );
}
