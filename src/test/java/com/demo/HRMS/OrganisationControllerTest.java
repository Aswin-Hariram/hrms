package com.demo.HRMS;

import com.demo.HRMS.Controllers.OrganisationController;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Services.OrganisationService;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrganisationController.class)
class OrganisationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrganisationService orgService;

    private OrganisationEntity organisation;


    @BeforeEach
    void setUp() {

        organisation = new OrganisationEntity();

        organisation.setOrgName("Test Corp");
        organisation.setOrgEmail("test@corp.com");
        organisation.setOrgPhone("1234567890");
    }


    // =========================================================
    // GET /api/organisation/check
    // =========================================================

    @Test
    @DisplayName("GET /api/organisation/check - Health check")
    void health_Success() throws Exception {

        mockMvc.perform(
                        get("/api/organisation/check")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Status").value("Active"))
                .andExpect(jsonPath("$.Message").value("Hello from HRMS"));
    }


    // =========================================================
    // POST /api/organisation/register
    // Success
    // =========================================================

    @Test
    @DisplayName("POST /api/organisation/register - Success")
    void register_Success() throws Exception {

        // Arrange
        when(orgService.register(any(OrganisationEntity.class)))
                .thenReturn(
                        Map.of(
                                "Status",
                                "Successful"
                        )
                );


        // Act + Assert
        mockMvc.perform(
                        post("/api/organisation/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(organisation)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.Status").value("Successful"));


        // Verify that controller called service
        verify(orgService)
                .register(any(OrganisationEntity.class));
    }


    // =========================================================
    // POST /api/organisation/register
    // Invalid Email
    // =========================================================

    @Test
    @DisplayName("POST /api/organisation/register - Validation failure for invalid email")
    void register_ValidationFailure() throws Exception {

        // Arrange
        OrganisationEntity invalidOrg = new OrganisationEntity();

        invalidOrg.setOrgName("Test Corp");
        invalidOrg.setOrgEmail("invalid-email");
        invalidOrg.setOrgPhone("1234567890");


        // Act + Assert
        mockMvc.perform(
                        post("/api/organisation/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(invalidOrg)
                                )
                )
                .andExpect(status().isBadRequest());
    }
}