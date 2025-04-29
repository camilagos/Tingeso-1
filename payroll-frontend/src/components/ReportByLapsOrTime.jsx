import { useState } from "react";
import { Box, TextField, Button, Typography, Table, TableHead, TableRow, TableCell, TableBody, Paper } from "@mui/material";
import reservationService from "../services/reservation.service";

const ReportByLapsOrTime = () => {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [data, setData] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await reservationService.getIncomeFromLapsOrTime(startDate, endDate);
      setData(res.data);
    } catch (error) {
      console.error(error);
      alert("Error al obtener el reporte");
    }
  };

  const columnas = data ? Object.keys(data[Object.keys(data)[0]] || {}) : [];

  return (
    <Box sx={{ maxWidth: 1000, margin: "auto", mt: 5 }}>
      <Typography variant="h5" gutterBottom>
        Reporte de Ingresos por Vueltas o Tiempo
      </Typography>
      <form onSubmit={handleSubmit} style={{ display: "flex", gap: "1rem", marginBottom: "1rem" }}>
        <TextField
          label="Fecha Inicio"
          type="date"
          InputLabelProps={{ shrink: true }}
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
          required
        />
        <TextField
          label="Fecha Fin"
          type="date"
          InputLabelProps={{ shrink: true }}
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
          required
        />
        <Button type="submit" variant="contained" color="primary">Consultar</Button>
      </form>

      {data && (
        <Paper sx={{ overflowX: "auto" }}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell><strong>Tipo</strong></TableCell>
                {columnas.map((col) => (
                  <TableCell key={col} align="right"><strong>{col}</strong></TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {Object.entries(data).map(([tipo, valores]) => (
                <TableRow key={tipo}>
                  <TableCell>{tipo}</TableCell>
                  {columnas.map((col) => (
                    <TableCell key={col} align="right">
                      {valores[col] ? `$${valores[col].toFixed(0)}` : "$0"}
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      )}
    </Box>
  );
};

export default ReportByLapsOrTime;
