import {useQuery} from '@tanstack/react-query';
import api from "../api/client.ts";
import type {Vehicle} from "../types/vehicle.ts";

export default function GaragePage() {
  const {data: vehicles = [], isPending, isError, error} = useQuery({queryKey: ['vehicles'], queryFn: fetchVehicles})

  if (isPending) {
    return <p>Loading vehicles...</p>;
  }

  if (isError) {
    return <p>Could not load vehicles: {error.message}</p>;
  }

  return <>
    {vehicles.map((vehicle) => (
      <div key={vehicle.vin}>
        {vehicle.brand} {vehicle.model} ({vehicle.year})
      </div>
    ))}
  </>
}

async function fetchVehicles(): Promise<Vehicle[]> {
  const result = await api.get<Vehicle[]>("/vehicle");
  return result.data
}