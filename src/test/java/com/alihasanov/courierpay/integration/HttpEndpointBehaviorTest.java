package com.alihasanov.courierpay.integration;

import com.alihasanov.courierpay.dto.CompanyDtos.CreateCompanyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class HttpEndpointBehaviorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void publicHealthEndpoint_shouldReturnJsonWithoutStartingRealServer() throws Exception {
        mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.service").value("courierpay"))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void protectedCompanyEndpoint_shouldRejectAnonymousRequest() throws Exception {
        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void companyEndpoints_shouldBehaveLikeHttpApiUsingMockMvc() throws Exception {
        var request = new CreateCompanyRequest("MockMvc Logistics", BigDecimal.valueOf(7.50));

        mockMvc.perform(post("/api/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("MockMvc Logistics"))
                .andExpect(jsonPath("$.commissionRate").value(7.50));

        mockMvc.perform(get("/api/v1/companies")
                        .param("name", "mockmvc")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].name").value("MockMvc Logistics"))
                .andExpect(jsonPath("$.content[0].commissionRate").value(7.50));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCompany_shouldReturnBadRequestForInvalidJsonBody() throws Exception {
        var invalidJson = """
                {
                  "name": "",
                  "commissionRate": 101.00
                }
                """;

        mockMvc.perform(post("/api/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
