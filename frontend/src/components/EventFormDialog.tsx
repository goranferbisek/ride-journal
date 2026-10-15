import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  Stack,
  TextField
} from "@mui/material";
import {useMutation} from "@tanstack/react-query";
import api from "../api/client.ts";
import {type EventRequest, EventType, type SavedEvent, type VehicleEvent} from "../types/event.ts";
import {type SubmitEvent, useState} from "react";
import axios from "axios";

type FormErrors = Partial<Record<keyof EventRequest, string>>;

// no vehicle has more than 5 milion km
const MAX_ODOMETER_KM = 5_000_000;

interface EventFormDialogProps {
  open: boolean;
  vehicleId: number;
  event?: VehicleEvent;
  onClose: () => void;
  onSuccess: () => void;
}

function getToday() {
  const now = new Date();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  return `${now.getFullYear()}-${month}-${day}`;
}

function extractFormErrors(error: unknown): { formErrors: FormErrors; generalError?: string } {
  if (!axios.isAxiosError<{ message?: string }>(error) || !error.response?.data?.message) {
    if (axios.isAxiosError(error) && error.message) {
      return {formErrors: {}, generalError: error.message};
    }
    return {formErrors: {}};
  }

  const message = error.response.data.message;
  const formErrors: FormErrors = {};
  const unmappedErrors: string[] = [];

  const parts = message.split("; ");
  for (const part of parts) {
    const colonIndex = part.indexOf(": ");
    if (colonIndex !== -1) {
      const field = part.slice(0, colonIndex).trim() as keyof EventRequest;
      const errorMsg = part.slice(colonIndex + 2).trim();
      if (["date", "eventType", "odometerKm", "notes"].includes(field)) {
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

export default function EventFormDialog({open, vehicleId, event, onClose, onSuccess}: EventFormDialogProps) {
  const isEdit = event !== undefined;
  const today = getToday();
  const [clientErrors, setClientErrors] = useState<FormErrors>({});
  const [warning, setWarning] = useState<string>();

  const saveEvent = useMutation({
    mutationFn: async (payload: EventRequest) => {
      const response = isEdit
        ? await api.put<SavedEvent>(`/vehicles/${vehicleId}/events/${event.id}`, payload)
        : await api.post<SavedEvent>(`/vehicles/${vehicleId}/events`, payload);

      return response.data;
    },
    onSuccess: (saved) => {
      onSuccess();
      if (saved.warning) {
        setWarning(saved.warning);
      } else {
        onClose();
      }
    }
  });

  const {formErrors, generalError} = extractFormErrors(saveEvent.error);

  function resetState() {
    saveEvent.reset();
    setWarning(undefined);
    setClientErrors({});
  }

  function handleSubmit(submitEvent: SubmitEvent<HTMLFormElement>) {
    submitEvent.preventDefault();
    if (saveEvent.isPending) return;
    const formData = new FormData(submitEvent.currentTarget);

    const date = String(formData.get("date") ?? "").trim();
    const eventType = String(formData.get("eventType") ?? "").trim();
    const odometerKm = String(formData.get("odometerKm") ?? "").trim();
    const notes = String(formData.get("notes") ?? "").trim();

    const errors: FormErrors = {};
    if (!date) {
      errors.date = "Date is required";
    } else if (date > today) {
      errors.date = "Date must not be in the future";
    }
    if (!eventType) {
      errors.eventType = "Event type is required";
    }
    if (odometerKm && !/^\d+$/.test(odometerKm)) {
      errors.odometerKm = "Odometer must be a whole number >= 0";
    } else if (odometerKm && Number(odometerKm) > MAX_ODOMETER_KM) {
      errors.odometerKm = `Odometer must lower than ${MAX_ODOMETER_KM}`;
    }
    setClientErrors(errors);
    if (Object.keys(errors).length > 0) {
      saveEvent.reset();
      return;
    }

    saveEvent.mutate({
      date,
      eventType: eventType as EventType,
      odometerKm: odometerKm ? Number(odometerKm) : undefined,
      notes: notes || undefined,
    });
  }

  return (
    <Dialog open={open} fullWidth maxWidth="sm"
            onClose={() => !saveEvent.isPending && onClose()}
            slotProps={{transition: {onExited: resetState}}}>
      <form key={event?.id ?? "new"} noValidate onSubmit={handleSubmit}>
        <DialogTitle>{isEdit ? "Edit event" : "Add event"}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{pt: 1}}>
            {warning && (
              <Alert severity="warning">
                Event saved. Warning: {warning}
              </Alert>
            )}
            {generalError && (
              <Alert severity="error">
                {generalError}
              </Alert>
            )}
            <TextField type="date" required name="date" label="Date" size="small" fullWidth
                       defaultValue={event?.date ?? today}
                       disabled={!!warning}
                       slotProps={{inputLabel: {shrink: true}, htmlInput: {max: today}}}
                       error={!!(clientErrors.date ?? formErrors.date)}
                       helperText={clientErrors.date ?? formErrors.date}/>
            <TextField select required name="eventType" label="Event type" size="small" fullWidth
                       defaultValue={event?.eventType ?? ""}
                       disabled={!!warning}
                       error={!!(clientErrors.eventType ?? formErrors.eventType)}
                       helperText={clientErrors.eventType ?? formErrors.eventType}>
              {EventType.map((type) => (
                <MenuItem key={type} value={type}>
                  {type.replace(/_/g, " ")}
                </MenuItem>
              ))}
            </TextField>
            <TextField name="odometerKm" label="Odometer (km)" size="small" fullWidth
                       defaultValue={event?.odometerKm ?? ""}
                       disabled={!!warning}
                       slotProps={{htmlInput: {inputMode: "numeric"}}}
                       error={!!(clientErrors.odometerKm ?? formErrors.odometerKm)}
                       helperText={clientErrors.odometerKm ?? formErrors.odometerKm}/>
            <TextField multiline minRows={3} name="notes" label="Notes" size="small" fullWidth
                       defaultValue={event?.notes ?? ""}
                       disabled={!!warning}
                       error={!!(clientErrors.notes ?? formErrors.notes)}
                       helperText={clientErrors.notes ?? formErrors.notes}/>
          </Stack>
        </DialogContent>
        <DialogActions>
          {warning ? (
            <Button variant="contained" onClick={onClose}>Close</Button>
          ) : (
            <>
              <Button onClick={onClose} disabled={saveEvent.isPending}>Cancel</Button>
              <Button type="submit" variant="contained" loading={saveEvent.isPending}>Save</Button>
            </>
          )}
        </DialogActions>
      </form>
    </Dialog>
  );
}
