import api from "./client.ts";
import type {Vehicle} from "../types/vehicle.ts";

export async function fetchVehicles(): Promise<Vehicle[]> {
  const result = await api.get<Vehicle[]>("/vehicles");
  return result.data;
}
