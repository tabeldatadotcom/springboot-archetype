package com.maryanto.dimas.archetype.test;

import com.maryanto.dimas.archetype.MainApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = MainApplication.class)
@AutoConfigureMockMvc
public class TestSwaggerEnabled {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSwaggerApi() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/v2/api-docs")
                        .accept("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        log.info("{}", resultString);
    }
}
