export type VehicleEvent = {
  id: number,
  date: string,
  eventType: EventType,
  odometerKm?: number,
  notes?: string,
}

export type EventRequest = Omit<VehicleEvent, "id">;

export type SavedEvent = VehicleEvent & { warning?: string };

export const EventType = [
  'OIL_CHANGE',
  'TIRE_CHANGE',
  'REPAIR',
  'REGISTRATION_RENEWAL',
  'INSPECTION',
  'INSURANCE',
  'ODOMETER_READING',
  'OTHER',
] as const;
export type EventType = (typeof EventType)[number];
