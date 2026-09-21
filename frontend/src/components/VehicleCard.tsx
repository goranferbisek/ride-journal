import {Button, Chip, ListItem, Stack} from "@mui/material";
import type {Vehicle} from "../types/vehicle.ts";
import {Link} from "react-router";

interface VehicleCardProps {
  vehicle: Vehicle;
}

export default function VehicleCard({vehicle}: VehicleCardProps) {
  return (
    <ListItem sx={{border: 1, borderColor: "divider", borderRadius: 2, p: 2}}>
      <Stack>
        <Stack direction="row" sx={{justifyContent: "space-between"}}>
          <Stack>
            {vehicle.brand} {vehicle.model} ({vehicle.year})
          </Stack>
          <Button component={Link} to="/garage/vehicle/edit" variant="contained">Edit</Button>
        </Stack>
        <Stack spacing={2}>
          <Chip label={vehicle.licensePlate}/>
          <Chip label={vehicle.vin}/>
        </Stack>
      </Stack>
    </ListItem>
  );
}
