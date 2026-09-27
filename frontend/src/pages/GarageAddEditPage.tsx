import {Button, Container, MenuItem, Stack, TextField, Typography} from "@mui/material";
import {Link, useNavigate, useParams} from "react-router";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import api from "../api/client.ts";
import {type Vehicle, VehicleType} from "../types/vehicle.ts";
import type {SubmitEvent} from "react";

export default function GarageAddEditPage() {
  const {id} = useParams();
  const vehicleId = Number(id);
  const isEdit = id !== undefined;
  const queryClient = useQueryClient();

  const navigate = useNavigate();

  const {data: vehicle, isPending, isError, error} = useQuery({
    queryKey: ['vehicles', vehicleId],
    queryFn: () => fetchVehicle(vehicleId),
    enabled: isEdit,
    initialData: () => queryClient.getQueryData<Vehicle[]>(['vehicles'])?.find(v => v.id === vehicleId),
  });

  async function fetchVehicle(id: number): Promise<Vehicle> {
    const result = await api.get<Vehicle>(`/vehicle/${id}`);
    return result.data
  }

  const saveVehicle = useMutation({
    mutationFn: async (payload: Omit<Vehicle, "id">) => {
      const response = isEdit
        ? await api.put<Vehicle>(`/vehicle/${vehicleId}`, payload)
        : await api.post<Vehicle>(`/vehicle`, payload)

      return response.data;
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({queryKey: ["vehicles"]});
      navigate("/garage")
    }
  })

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);

    const year = String(formData.get("year") ?? "").trim();
    const type = String(formData.get("type") ?? "").trim();

    saveVehicle.mutate({
      type: (type || undefined) as unknown as Vehicle["type"],
      brand: String(formData.get("brand") ?? "").trim(),
      model: String(formData.get("model") ?? "").trim(),
      year: year ? Number(year) : undefined,
      licensePlate: String(formData.get("licensePlate") ?? "").trim() || undefined,
      vin: String(formData.get("vin") ?? "").trim() || undefined,
    });
  }

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
      <form noValidate onSubmit={handleSubmit}>
        <Stack spacing={2}>
          <TextField select required label="Vehicle Type" name="type" size="small"
                     defaultValue={vehicle ? vehicle.type : ""}  /*error={/*!!actionData?.formErrors?.username}*/
            /*helperText={actionData?.formErrors?.username} */>
            {VehicleType.map((type) => (
              <MenuItem key={type} value={type}>
                {type}
              </MenuItem>
            ))}
          </TextField>
          <TextField required label="Brand" name="brand" size="small"
                     defaultValue={vehicle ? vehicle.brand : ""} /*error={/*!!actionData?.formErrors?.username}*/
            /*helperText={actionData?.formErrors?.username} */ />
          <TextField required label="Model" name="model" size="small"
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
          <Button type="submit" variant="contained" loading={saveVehicle.isPending}>
            {saveVehicle.isPending ? "Saving..." : "Save"}
          </Button>
        </Stack>
      </form>
    </Container>
  </>
}

