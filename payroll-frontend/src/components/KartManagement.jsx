import { useEffect, useState } from "react";
import { Box, Typography, Button, Paper, Divider } from "@mui/material";
import kartService from "../services/kart.service";

const KartManagement = () => {
  const [availableKarts, setAvailableKarts] = useState([]);
  const [unavailableKarts, setUnavailableKarts] = useState([]);

  const loadKarts = async () => {
    try {
      const available = await kartService.getByAvailability(true);
      const unavailable = await kartService.getByAvailability(false);
      setAvailableKarts(available.data);
      setUnavailableKarts(unavailable.data);
    } catch (error) {
      console.error("Error cargando karts", error);
    }
  };

  useEffect(() => {
    loadKarts();
  }, []);

  const toggleAvailability = async (kart) => {
    try {
      const updatedKart = { ...kart, available: !kart.available };
      await kartService.update(updatedKart);
      loadKarts();
    } catch (error) {
      console.error("Error cambiando disponibilidad", error);
    }
  };

  return (
    <Box sx={{ maxWidth: 800, margin: "auto", mt: 5 }}>
      <Paper elevation={3} sx={{ p: 3 }}>
        <Typography variant="h5" gutterBottom>
          Gestión de Karts
        </Typography>
        <Divider sx={{ mb: 3 }} />

        <Box sx={{ mb: 4 }}>
          <Typography variant="h6">
            Karts Disponibles ({availableKarts.length})
          </Typography>
          <Divider sx={{ my: 1 }} />
          {availableKarts.length === 0 ? (
            <Typography>No hay karts disponibles.</Typography>
          ) : (
            availableKarts.map((kart) => (
              <Box key={kart.id} sx={{ mb: 2, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <Typography><strong>Código:</strong> {kart.code}</Typography>
                <Button
                  variant="contained"
                  color="warning"
                  onClick={() => toggleAvailability(kart)}
                >
                  Marcar como No Disponible
                </Button>
              </Box>
            ))
          )}
        </Box>

        <Box>
          <Typography variant="h6">
            Karts No Disponibles ({unavailableKarts.length})
          </Typography>
          <Divider sx={{ my: 1 }} />
          {unavailableKarts.length === 0 ? (
            <Typography>No hay karts no disponibles.</Typography>
          ) : (
            unavailableKarts.map((kart) => (
              <Box key={kart.id} sx={{ mb: 2, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <Typography><strong>Código:</strong> {kart.code}</Typography>
                <Button
                  variant="contained"
                  color="success"
                  onClick={() => toggleAvailability(kart)}
                >
                  Marcar como Disponible
                </Button>
              </Box>
            ))
          )}
        </Box>
      </Paper>
    </Box>
  );
};

export default KartManagement;
