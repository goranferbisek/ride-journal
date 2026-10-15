import {useQuery, useQueryClient} from "@tanstack/react-query";
import {Button, Container, MenuItem, Stack, TextField, Typography} from "@mui/material";
import {Link} from "react-router";
import {useState} from "react";
import {fetchVehicles} from "../api/vehicles.ts";
import EventFormDialog from "../components/EventFormDialog.tsx";
import EventTimeline from "../components/EventTimeline.tsx";
import type {EventType, VehicleEvent} from "../types/event.ts";

export default function DashboardPage() {
  const queryClient = useQueryClient();
  const {data: vehicles = [], isPending, isError, error} = useQuery({
    queryKey: ["vehicles"],
    queryFn: fetchVehicles
  });
  const [selectedVehicleId, setSelectedVehicleId] = useState<number>();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingEvent, setEditingEvent] = useState<VehicleEvent>();
  const [eventType] = useState<EventType | undefined>();

  if (isPending) {
    return <p>Loading vehicles...</p>;
  }

  if (isError) {
    return <p>Could not load vehicles: {error.message}</p>;
  }

  if (vehicles.length === 0) {
    return (
      <Container maxWidth="md" sx={{py: 4}}>
        <Stack spacing={2} sx={{alignItems: "flex-start"}}>
          <Typography variant="h4">Dashboard</Typography>
          <Typography>You have no vehicles. Add a vehicle to your garage.</Typography>
          <Button component={Link} to="/garage" variant="contained">Go to garage</Button>
        </Stack>
      </Container>
    );
  }

  const vehicle = vehicles.find((item) => item.id === selectedVehicleId) ?? vehicles[0];

  function handleAddEvent() {
    setEditingEvent(undefined);
    setDialogOpen(true);
  }

  function handleEventClick(event: VehicleEvent) {
    setEditingEvent(event);
    setDialogOpen(true);
  }

  return (
    <Container maxWidth="md" sx={{py: 4}}>
      <Stack spacing={3}>
        <Stack direction="row" spacing={2} sx={{alignItems: "center", justifyContent: "space-between"}}>
          <Typography variant="h4">Dashboard</Typography>
          <TextField select label="Vehicle" size="small" value={vehicle.id}
                     onChange={(changeEvent) => setSelectedVehicleId(Number(changeEvent.target.value))}>
            {vehicles.map((item) => (
              <MenuItem key={item.id} value={item.id}>
                {item.brand} {item.model} ({item.year ?? "—"})
              </MenuItem>
            ))}
          </TextField>
          <Button variant="contained" onClick={handleAddEvent}>Add event</Button>
        </Stack>
        <EventTimeline
          key={vehicle.id}
          vehicle={vehicle}
          eventType={eventType}
          onEventClick={handleEventClick}
        />
      </Stack>
      <EventFormDialog
        open={dialogOpen}
        vehicleId={vehicle.id}
        event={editingEvent}
        onClose={() => setDialogOpen(false)}
        onSuccess={() => queryClient.invalidateQueries({queryKey: ["events", vehicle.id]})}
      />
    </Container>
  );
}
