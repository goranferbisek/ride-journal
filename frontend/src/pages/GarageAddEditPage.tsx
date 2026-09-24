import {Button, Container, Stack, TextField, Typography} from "@mui/material";
import {Link, useParams} from "react-router";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import api from "../api/client.ts";
import type {Vehicle} from "../types/vehicle.ts";

export default function GarageAddEditPage() {
  const {id} = useParams();
  const vehicleId = Number(id);
  const isEdit = id !== undefined;
  const queryClient = useQueryClient();

  const {data: vehicle, isPending, isError, error} = useQuery({
    queryKey: ['vehicles', vehicleId],
    queryFn: () => fetchVehicle(vehicleId),
    enabled: isEdit,
    initialData: () => queryClient.getQueryData<Vehicle[]>(['vehicles'])?.find(v => v.id === vehicleId),
  });

  if (isEdit && isPending) {
    return <p>Loading vehicle...</p>;
  }

  if (isEdit && isError) {
    return <p>Could not load vehicle: {error.message}</p>;
  }

  return <>
    <Container maxWidth="md" sx={{py: 4}}>
      <Typography variant="h4" sx={{mb: 4}}>
        {isEdit ? "Edit" : "Add"}
      </Typography>
      <Stack spacing={2}>
        <TextField label="Vehicle Type" name="type" size="small"
                               defaultValue={vehicle ? vehicle.type : ""}  /*error={/*!!actionData?.formErrors?.username}*/
          /*helperText={actionData?.formErrors?.username} */ />
        <TextField label="Brand" name="brand" size="small"
                               defaultValue={vehicle ? vehicle.brand : ""} /*error={/*!!actionData?.formErrors?.username}*/
          /*helperText={actionData?.formErrors?.username} */ />
        <TextField label="Model" name="model" size="small"
                               defaultValue={vehicle ? vehicle.model : ""} /*error={/*!!actionData?.formErrors?.username}*/
          /*helperText={actionData?.formErrors?.username} */ />
        <TextField label="Year" name="year" size="small"
                               defaultValue={vehicle ? vehicle.year : ""} /*error={/*!!actionData?.formErrors?.username}*/
          /*helperText={actionData?.formErrors?.username} */ />
        <TextField label="License plate" name="licensePlate" size="small"
                               defaultValue={vehicle ? vehicle.licensePlate : ""} /*error={/*!!actionData?.formErrors?.username}*/
          /*helperText={actionData?.formErrors?.username} */ />
        <TextField label="VIN number" name="vin" size="small"
                               defaultValue={vehicle ? vehicle.vin : ""} /*error={/*!!actionData?.formErrors?.username}*/
          /*helperText={actionData?.formErrors?.username} */ />
      </Stack>
      <Stack direction="row" spacing={1} sx={{mt: 4, justifyContent: "flex-end"}}>
        <Button component={Link} to="/garage" variant="outlined">Cancel</Button>
        <Button component={Link} to="/garage" variant="contained">Save</Button>
      </Stack>
    </Container>
  </>
}

async function fetchVehicle(id: number): Promise<Vehicle> {
  const result = await api.get<Vehicle>(`/vehicle/${id}`);
  return result.data
}
