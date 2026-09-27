package si.ferbisek.ride_journal.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import si.ferbisek.ride_journal.dto.request.VehicleRequest;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTypeJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesEmptyStringToNull() throws Exception {
        String json = "{\"brand\":\"Audi\",\"model\":\"A4\",\"type\":\"\"}";
        VehicleRequest request = objectMapper.readValue(json, VehicleRequest.class);
        assertNull(request.getType());
        assertEquals("Audi", request.getBrand());
    }

    @Test
    void deserializesValidEnumString() throws Exception {
        String json = "{\"brand\":\"Audi\",\"model\":\"A4\",\"type\":\"CAR\"}";
        VehicleRequest request = objectMapper.readValue(json, VehicleRequest.class);
        assertEquals(VehicleType.CAR, request.getType());
    }

    @Test
    void deserializesCaseInsensitiveEnumString() throws Exception {
        String json = "{\"brand\":\"Audi\",\"model\":\"A4\",\"type\":\"motorcycle\"}";
        VehicleRequest request = objectMapper.readValue(json, VehicleRequest.class);
        assertEquals(VehicleType.MOTORCYCLE, request.getType());
    }

    @Test
    void deserializesWhitespaceStringToNull() throws Exception {
        String json = "{\"brand\":\"Audi\",\"model\":\"A4\",\"type\":\"   \"}";
        VehicleRequest request = objectMapper.readValue(json, VehicleRequest.class);
        assertNull(request.getType());
    }
}
