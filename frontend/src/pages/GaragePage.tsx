import {useQuery} from '@tanstack/react-query';
import api from "../api/client.ts";
import type {Vehicle} from "../types/vehicle.ts";
import {Button, Chip, Container, List, ListItem, Stack, Typography} from "@mui/material";
import {Link} from "react-router";

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
        <Button component={Link} to="/garage/add" variant="contained">Add</Button>
      </Stack>
      <Stack>
        {vehicles.length == 0 ? (
          <div>You have no vehicles. Add a vehicle to your garage</div>
        ) : (
          <List sx={{display: "grid", gap: 2}}>
            {vehicles.map((vehicle) => (
              <ListItem key={vehicle.id} sx={{border: 1, borderColor: "divider", borderRadius: 2, p: 2}}>
                <Stack>
                  <Stack>
                    {vehicle.brand} {vehicle.model} ({vehicle.year})
                  </Stack>
                  <Stack spacing={2}>
                    <Chip label={vehicle.licensePlate}/>
                    <Chip label={vehicle.vin}/>
                  </Stack>
                </Stack>
              </ListItem>
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