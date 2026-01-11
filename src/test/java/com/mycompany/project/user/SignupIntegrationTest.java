package com.mycompany.project.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.project.auth.command.dto.UserRegisterRequest;
import com.mycompany.project.user.command.domain.aggregate.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SignupIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSignup() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("debug_student@test.com");
        request.setPassword("password123");
        request.setName("DebugUser");
        request.setBirthDate("2005-01-01");
        request.setRole(Role.STUDENT);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
