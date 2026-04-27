package br.com.fatecads.fatecads;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class FatecadsWebTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void professorPagesRender() throws Exception {
        mockMvc.perform(get("/professor/listar"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/professor/criar"))
                .andExpect(status().isOk());
    }

    @Test
    void disciplinaPagesRender() throws Exception {
        mockMvc.perform(get("/disciplina/listar"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/disciplina/criar"))
                .andExpect(status().isOk());
    }
}
