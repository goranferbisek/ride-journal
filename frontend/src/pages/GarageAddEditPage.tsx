import {Alert, Button, Container, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, MenuItem, Stack, TextField, Typography} from "@mui/material";
import {Link, useNavigate, useParams} from "react-router";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import api from "../api/client.ts";
import {type Vehicle, VehicleType} from "../types/vehicle.ts";
import {type SubmitEvent, useState} from "react";
import axios from "axios";

type VehicleFormData = Omit<Vehicle, "id">;
type FormErrors = Partial<Record<keyof VehicleFormData, string>>;

function extractFormErrors(error: unknown): { formErrors: FormErrors; generalError?: string } {
  if (!axios.isAxiosError<{ message?: string }>(error) || !error.response?.data?.message) {
    if (axios.isAxiosError(error) && error.message) {
      return { formErrors: {}, generalError: error.message };
    }
    return { formErrors: {} };
  }

  const message = error.response.data.message;
  const formErrors: FormErrors = {};
  const unmappedErrors: string[] = [];

  const parts = message.split("; ");
  for (const part of parts) {
    const colonIndex = part.indexOf(": ");
    if (colonIndex !== -1) {
      const field = part.slice(0, colonIndex).trim() as keyof VehicleFormData;
      const errorMsg = part.slice(colonIndex + 2).trim();
      if (["type", "brand", "model", "year", "licensePlate", "vin"].includes(field)) {
        formErrors[field] = errorMsg;
        continue;
      }
    }
    unmappedErrors.push(part);
  }

  return {
    formErrors,
    generalError: unmappedErrors.length > 0 ? unmappedErrors.join("; ") : undefined,
  };
}

export default function GarageAddEditPage() {
  const {id} = useParams();
  const vehicleId = Number(id);
  const isEdit = id !== undefined;
  const queryClient = useQueryClient();

  const navigate = useNavigate();
  const [yearError, setYearError] = useState<string>();
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);

  const {data: vehicle, isFetchedAfterMount, isError, error} = useQuery({
    queryKey: ['vehicles', vehicleId],
    queryFn: () => fetchVehicle(vehicleId),
    enabled: isEdit,
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

  const deleteVehicle = useMutation({
    mutationFn: async () => {
      await api.delete(`/vehicle/${vehicleId}`);
    },
    onSuccess: async () => {
      queryClient.removeQueries({queryKey: ["vehicles", vehicleId]});
      await queryClient.invalidateQueries({queryKey: ["vehicles"]});
      navigate("/garage");
    }
  })

  const {formErrors, generalError} = extractFormErrors(saveVehicle.error);

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);

    const year = String(formData.get("year") ?? "").trim();
    const type = String(formData.get("type") ?? "").trim();

    if (year && !/^\d+$/.test(year)) {
      saveVehicle.reset();
      setYearError("Year must be a whole number");
      return;
    }
    setYearError(undefined);

    saveVehicle.mutate({
      type: (type || undefined) as unknown as Vehicle["type"],
      brand: String(formData.get("brand") ?? "").trim(),
      model: String(formData.get("model") ?? "").trim(),
      year: year ? Number(year) : undefined,
      licensePlate: String(formData.get("licensePlate") ?? "").trim() || undefined,
      vin: String(formData.get("vin") ?? "").trim() || undefined,
    });
  }

  if (isEdit && !isFetchedAfterMount) {
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
          {deleteVehicle.isError && (
            <Alert severity="error">
              Could not delete vehicle: {deleteVehicle.error.message}
            </Alert>
          )}
          {generalError && (
            <Alert severity="error">
              {generalError}
            </Alert>
          )}
          <TextField select required label="Vehicle Type" name="type" size="small"
                     defaultValue={vehicle ? vehicle.type : ""}
                     error={!!formErrors.type}
                     helperText={formErrors.type}>
            {VehicleType.map((type) => (
              <MenuItem key={type} value={type}>
                {type}
              </MenuItem>
            ))}
          </TextField>
          <TextField required label="Brand" name="brand" size="small"
                     defaultValue={vehicle ? vehicle.brand : ""}
                     error={!!formErrors.brand}
                     helperText={formErrors.brand} />
          <TextField required label="Model" name="model" size="small"
                     defaultValue={vehicle ? vehicle.model : ""}
                     error={!!formErrors.model}
                     helperText={formErrors.model} />
          <TextField label="Year" name="year" size="small"
                     defaultValue={vehicle ? vehicle.year : ""}
                     error={!!(yearError ?? formErrors.year)}
                     helperText={yearError ?? formErrors.year} />
          <TextField label="License plate" name="licensePlate" size="small"
                     defaultValue={vehicle ? vehicle.licensePlate : ""}
                     error={!!formErrors.licensePlate}
                     helperText={formErrors.licensePlate} />
          <TextField label="VIN number" name="vin" size="small"
                     defaultValue={vehicle ? vehicle.vin : ""}
                     error={!!formErrors.vin}
                     helperText={formErrors.vin} />
        </Stack>
        <Stack direction="row" sx={{mt: 4, justifyContent: "space-between"}}>
          {isEdit ? (
            <Button color="warning" variant="outlined"
                    onClick={() => {
                      deleteVehicle.reset();
                      setConfirmDeleteOpen(true);
                    }}>
              Delete
            </Button>
          ) : <span />}
          <Stack direction="row" spacing={1}>
            <Button component={Link} to="/garage" variant="outlined">Cancel</Button>
            <Button type="submit" variant="contained" loading={saveVehicle.isPending}>
              {saveVehicle.isPending ? "Saving..." : "Save"}
            </Button>
          </Stack>
        </Stack>
      </form>
      <Dialog open={confirmDeleteOpen} onClose={() => !deleteVehicle.isPending && setConfirmDeleteOpen(false)}>
        <DialogTitle>Delete vehicle?</DialogTitle>
        <DialogContent>
          <DialogContentText>
            This will permanently delete this vehicle and all of its events. This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmDeleteOpen(false)} disabled={deleteVehicle.isPending}>Cancel</Button>
          <Button color="warning" variant="contained" loading={deleteVehicle.isPending}
                  onClick={() => deleteVehicle.mutate(undefined, {onSettled: () => setConfirmDeleteOpen(false)})}>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  </>
}

