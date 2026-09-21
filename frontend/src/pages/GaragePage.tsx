import {useQuery} from '@tanstack/react-query';
import api from "../api/client.ts";
import type {Vehicle} from "../types/vehicle.ts";
import {Button, Container, List, Stack, Typography} from "@mui/material";
import {Link} from "react-router";
import VehicleCard from "../components/VehicleCard.tsx";

export default function GaragePage() {
  const {data: vehicles = [], isPending, isError, error} = useQuery({queryKey: ['vehicles'], queryFn: fetchVehicles})

  if (isPending) {
    return <p>Loading vehicles...</p>;
  }

  if (isError) {
    return <p>Could not load vehicles: {error.message}</p>;
  }

  return <>
    <Container maxWidth="md" sx={{py: 4}}>
      <Stack direction="row" spacing={2} sx={{alignItems: "center", justifyContent: "space-between"}}>
        <Typography variant="h4">Garage</Typography>
        <Button component={Link} to="/garage/vehicle/add" variant="contained">Add</Button>
      </Stack>
      <Stack>
        {vehicles.length == 0 ? (
          <div>You have no vehicles. Add a vehicle to your garage</div>
        ) : (
          <List sx={{display: "grid", gap: 2}}>
            {vehicles.map((vehicle) => (
              <VehicleCard key={vehicle.id} vehicle={vehicle}/>
            ))}
          </List>
        )}
      </Stack>
    </Container>
  </>
}

async function fetchVehicles(): Promise<Vehicle[]> {
  const result = await api.get<Vehicle[]>("/vehicle");
  return result.data
}