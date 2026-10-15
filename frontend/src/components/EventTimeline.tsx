import {keepPreviousData, useQuery} from "@tanstack/react-query";
import {
  MenuItem,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TextField,
  Typography
} from "@mui/material";
import {useState} from "react";
import api from "../api/client.ts";
import type {Page} from "../types/page.ts";
import type {EventType, VehicleEvent} from "../types/event.ts";
import type {Vehicle} from "../types/vehicle.ts";

interface EventTimelineProps {
  vehicle: Vehicle;
  eventType?: EventType;
  onEventClick: (event: VehicleEvent) => void;
}

export default function EventTimeline({vehicle, eventType, onEventClick}: EventTimelineProps) {
  const [fromYear, setFromYear] = useState<number>();
  const [toYear, setToYear] = useState<number>();
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [previousEventType, setPreviousEventType] = useState(eventType);

  if (eventType !== previousEventType) {
    setPreviousEventType(eventType);
    setPage(0);
  }

  const currentYear = new Date().getFullYear();
  const firstYear = vehicle.year ?? currentYear - 30;
  const years = Array.from(
    {length: Math.max(0, currentYear - firstYear + 1)},
    (_, index) => currentYear - index
  );
  const fromYearOptions = years.filter((year) => toYear === undefined || year <= toYear);
  const toYearOptions = years.filter((year) => fromYear === undefined || year >= fromYear);

  const {data, isPending, isError, error} = useQuery({
    queryKey: ["events", vehicle.id, {eventType, fromYear, toYear, page, size: rowsPerPage}],
    queryFn: async () => {
      const result = await api.get<Page<VehicleEvent>>(`/vehicles/${vehicle.id}/events`, {
        params: {eventType, fromYear, toYear, page, size: rowsPerPage}
      });
      return result.data;
    },
    placeholderData: keepPreviousData
  });

  function handleFromYearChange(value: string) {
    setFromYear(value === "" ? undefined : Number(value));
    setPage(0);
  }

  function handleToYearChange(value: string) {
    setToYear(value === "" ? undefined : Number(value));
    setPage(0);
  }

  function handleRowsPerPageChange(value: string) {
    setRowsPerPage(Number(value));
    setPage(0);
  }

  return (
    <Stack spacing={2}>
      <Stack direction="row" spacing={2}>
        <TextField select label="From year" size="small" value={fromYear ?? ""}
                   onChange={(changeEvent) => handleFromYearChange(changeEvent.target.value)}>
          <MenuItem value="">Any</MenuItem>
          {fromYearOptions.map((year) => (
            <MenuItem key={year} value={year}>{year}</MenuItem>
          ))}
        </TextField>
        <TextField select label="To year" size="small" value={toYear ?? ""}
                   onChange={(changeEvent) => handleToYearChange(changeEvent.target.value)}>
          <MenuItem value="">Any</MenuItem>
          {toYearOptions.map((year) => (
            <MenuItem key={year} value={year}>{year}</MenuItem>
          ))}
        </TextField>
      </Stack>
      {isPending ? (
        <Typography>Loading events...</Typography>
      ) : isError ? (
        <Typography>Could not load events: {error.message}</Typography>
      ) : !data || data.content.length === 0 ? (
        <Typography>No events for the selected filters</Typography>
      ) : (
        <>
          <TableContainer component={Paper}>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Date</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Odometer (km)</TableCell>
                  <TableCell>Notes</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.content.map((event) => (
                  <TableRow key={event.id} hover sx={{cursor: "pointer"}}
                            onClick={() => onEventClick(event)}>
                    <TableCell>{event.date}</TableCell>
                    <TableCell>{event.eventType.replace(/_/g, " ")}</TableCell>
                    <TableCell>{event.odometerKm?.toLocaleString() ?? "—"}</TableCell>
                    <TableCell
                      title={event.notes ?? undefined}
                      sx={{maxWidth: 0, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis"}}
                    >
                      {event.notes || "—"}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <TablePagination
            component="div"
            count={data.totalElements}
            page={page}
            rowsPerPage={rowsPerPage}
            rowsPerPageOptions={[10, 25, 50, 100]}
            onPageChange={(_, nextPage) => setPage(nextPage)}
            onRowsPerPageChange={(changeEvent) => handleRowsPerPageChange(changeEvent.target.value)}
          />
        </>
      )}
    </Stack>
  );
}
