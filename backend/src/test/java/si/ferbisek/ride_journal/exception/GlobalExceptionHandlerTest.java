package si.ferbisek.ride_journal.exception;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void validationErrorReturns400WithErrorShape() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("name: name is required"))
                .andExpect(jsonPath("$.path").value("/test/validate"));
    }

    @Test
    void notFoundReturns404WithErrorShape() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Vehicle with id 42 not found"))
                .andExpect(jsonPath("$.path").value("/test/not-found"));
    }

    @Test
    void unexpectedExceptionReturns500WithoutLeakingDetails() throws Exception {
        mockMvc.perform(get("/test/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.path").value("/test/boom"))
                .andExpect(content().string(not(containsString("secret internal detail"))))
                .andExpect(content().string(not(containsString("IllegalStateException"))));
    }

    @Test
    void malformedJsonReturns400WithErrorShape() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed request body"))
                .andExpect(jsonPath("$.path").value("/test/validate"));
    }

    @RestController
    static class TestController {

        @PostMapping("/test/validate")
        public String validate(@Valid @RequestBody TestRequest request) {
            return "ok";
        }

        @GetMapping("/test/not-found")
        public String notFound() {
            throw new ResourceNotFoundException("Vehicle", 42);
        }

        @GetMapping("/test/boom")
        public String boom() {
            throw new IllegalStateException("secret internal detail");
        }
    }

    @Getter
    @Setter
    static class TestRequest {
        @NotBlank(message = "name is required")
        private String name;
    }
}
