
export type Vehicle = {
  brand: string,
  model: string,
  type: VehicleType,
  year: number,
  licensePlate: string,
  vin: string,
}

export type VehicleType = 'CAR' | 'MOTORCYCLE' | 'TRUCK' | 'OTHER';