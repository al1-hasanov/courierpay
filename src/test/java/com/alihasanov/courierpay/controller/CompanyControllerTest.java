package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.dto.CompanyDtos.CompanyResponse;
import com.alihasanov.courierpay.dto.CompanyDtos.CreateCompanyRequest;
import com.alihasanov.courierpay.security.JwtService;
import com.alihasanov.courierpay.service.CompanyService;
import com.alihasanov.courierpay.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc(addFilters = false)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CompanyService companyService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void create_shouldReturnCreatedCompany() throws Exception {
        var request = new CreateCompanyRequest("Bolt Food", BigDecimal.valueOf(12.5));
        var response = new CompanyResponse(1L, "Bolt Food", BigDecimal.valueOf(12.5));

        when(companyService.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/companies")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bolt Food"))
                .andExpect(jsonPath("$.commissionRate").value(12.5));
    }

    @Test
    void create_shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        var request = new CreateCompanyRequest(" ", BigDecimal.TEN);

        mockMvc.perform(post("/api/v1/companies")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_shouldPassFiltersAndPageableToService() throws Exception {
        var pageable = PageRequest.of(0, 20, Sort.by("id"));
        var response = new CompanyResponse(2L, "Wolt", BigDecimal.valueOf(10));

        when(companyService.findAll(eq("wol"), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/api/v1/companies")
                        .param("name", "wol")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Wolt"))
                .andExpect(jsonPath("$.content[0].commissionRate").value(10));

        verify(companyService).findAll(eq("wol"), eq(pageable));
    }
}
