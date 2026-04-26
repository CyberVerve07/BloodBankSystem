package com.example.bloodbanksystem;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class DonorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testListDonorsPage() throws Exception {
        mockMvc.perform(get("/donors"))
                .andExpect(status().isOk())
                .andExpect(view().name("donors"))
                .andExpect(model().attributeExists("donors"));
    }

    @Test
    void testShowAddForm() throws Exception {
        mockMvc.perform(get("/donors/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-donor"))
                .andExpect(model().attributeExists("donor"));
    }
}
