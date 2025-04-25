package com.example.demo.Controllers;

import com.example.demo.Entities.KartEntity;
import com.example.demo.Services.KartService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KartController.class)
public class KartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KartService kartService;

    @Test
    public void saveKart_ShouldReturnSavedKart() throws Exception {
        KartEntity kart = new KartEntity();
        kart.setId(1L);
        kart.setAvailable(true);

        given(kartService.saveKart(Mockito.any())).willReturn(kart);

        String json = """
                {
                    "available": true
                }
                """;

        mockMvc.perform(post("/kart/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    public void getKartsByAvailability_ShouldReturnList() throws Exception {
        KartEntity kart1 = new KartEntity();
        kart1.setId(1L);
        kart1.setAvailable(true);

        KartEntity kart2 = new KartEntity();
        kart2.setId(2L);
        kart2.setAvailable(true);

        given(kartService.getKartsByAvailability(true)).willReturn(List.of(kart1, kart2));

        mockMvc.perform(get("/kart/available/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].available", is(true)));
    }

    @Test
    public void updateKart_ShouldReturnUpdatedKart() throws Exception {
        KartEntity updatedKart = new KartEntity();
        updatedKart.setId(1L);
        updatedKart.setAvailable(false);

        given(kartService.updateKart(Mockito.any())).willReturn(updatedKart);

        String json = """
                {
                    "id": 1,
                    "available": false
                }
                """;

        mockMvc.perform(put("/kart/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    public void deleteKart_ShouldReturnTrue() throws Exception {
        given(kartService.deleteKart(1L)).willReturn(true);

        mockMvc.perform(delete("/kart/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
