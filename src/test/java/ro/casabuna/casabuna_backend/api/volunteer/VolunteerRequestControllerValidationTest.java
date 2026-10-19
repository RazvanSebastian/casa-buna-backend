package ro.casabuna.casabuna_backend.api.volunteer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ro.casabuna.casabuna_backend.model.dto.volunteer.NewVolunteerRequestDto;
import ro.casabuna.casabuna_backend.service.volunteer.VolunteerRequestService;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VolunteerRequestController.class)
public class VolunteerRequestControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VolunteerRequestService volunteerRequestService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @EnumSource(InvalidDtoType.class)
    void shouldReturnValidationError(InvalidDtoType invalidDtoType) throws Exception {
        var requestDto = buildInvalidDto(invalidDtoType);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/volunteer/request")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(invalidDtoType.getJsonPath()).exists());
    }

    @AllArgsConstructor
    @Getter
    private enum InvalidDtoType {
        AGE("selfDescription", "John", "Doe", null, "123456789", "$.age"),
        PHONE("selfDescription", "John", "Doe", 30, null, "$.phone"),
        SELF_DESCRIPTION(null, "John", "Doe", 30, "123456789", "$.selfDescription"),
        FIRST_NAME("selfDescription", null, "Doe", 30, "123456789", "$.firstName"),
        LAST_NAME("selfDescription", "John", null, 30, "123456789", "$.lastName");

        private final String selfDescription;
        private final String firstName;
        private final String lastName;
        private final Integer age;
        private final String phone;
        private final String jsonPath;
    }

    private NewVolunteerRequestDto buildInvalidDto(InvalidDtoType invalidDto) {
        return new NewVolunteerRequestDto(
                invalidDto.selfDescription,
                invalidDto.firstName,
                invalidDto.lastName,
                invalidDto.age,
                "Engineer",
                invalidDto.phone,
                List.of("school"),
                List.of("university"),
                List.of("health"),
                List.of("itSupport"),
                List.of("logistic"),
                "other"
        );
    }

}
