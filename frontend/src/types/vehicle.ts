
export type Vehicle = {
  id: number,
  brand: string,
  model: string,
  type: VehicleType,
  year?: number,
  licensePlate?: string,
  vin?: string,
}

export const VehicleType = ['CAR', 'MOTORCYCLE', 'TRUCK', "MOTORHOME", 'OTHER'] as const;
export type VehicleType = (typeof VehicleType)[number];