import {useQuery} from '@tanstack/react-query';
import api from "../api/client.ts";
import type {Vehicle} from "../types/vehicle.ts";
import {Button, Container} from "@mui/material";
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
    <Container>
      <h2>Garage</h2>
      <Button component={Link} to="/garage/add" variant="outlined">Add</Button>

      {vehicles.length == 0 ? (
        <div>You have no vehicles. Add a vehicle to your garage</div>
      ) : (
        vehicles.map((vehicle) => (
          <div key={vehicle.vin}>
            {vehicle.brand} {vehicle.model} ({vehicle.year})
          </div>
        ))
      )}
    </Container>
  </>
}

async function fetchVehicles(): Promise<Vehicle[]> {
  const result = await api.get<Vehicle[]>("/vehicle");
  return result.data
}