import {Button, Chip, ListItem, Stack, Typography} from "@mui/material";
import type {Vehicle} from "../types/vehicle.ts";
import {Link} from "react-router";

interface VehicleCardProps {
  vehicle: Vehicle;
}

export default function VehicleCard({vehicle}: VehicleCardProps) {
  return (
    <ListItem sx={{border: 1, borderColor: "divider", borderRadius: 2, p: 2}}>
      <Stack direction="row" spacing={2} sx={{width: "100%", justifyContent: "space-between", alignItems: "center"}}>
        <Stack direction="row" spacing={1.5}>
          <Typography variant="h6">
            {vehicle.brand} {vehicle.model} ({vehicle.year})
          </Typography>
          <Chip label={vehicle.licensePlate}/>
          <Chip label={vehicle.vin}/>
        </Stack>
        <Button component={Link} to={`/garage/vehicle/${vehicle.id}/edit`} variant="contained">Edit</Button>
      </Stack>
    </ListItem>
  );
}
