package com.project.buylist.buylist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import com.project.buylist.ingredients.Ingredient;
import com.project.buylist.ingredients.IngredientsRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class BuyListControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BuyListRepository repository;

    @Autowired
    private IngredientsRepository ingredientsRepository;

    private BuyList sample;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        ingredientsRepository.deleteAll();
        sample = createSampleBuyList();
    }

    @Test
    public void testCreateAndGetById() throws Exception {
        String json = objectMapper.writeValueAsString(sample);
        String response = mockMvc.perform(post("/api/buylist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Weekly"))
                .andReturn().getResponse().getContentAsString();

        BuyList created = objectMapper.readValue(response, BuyList.class);
        mockMvc.perform(get("/api/buylist/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value("Weekly"));
    }

    @Test
    public void testUpdate() throws Exception {
        BuyList saved = repository.save(sample);
        saved.setName("Monthly");
        String json = objectMapper.writeValueAsString(saved);
        mockMvc.perform(put("/api/buylist/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Monthly"));
    }

    @Test
    public void testDelete() throws Exception {
        BuyList saved = repository.save(sample);
        mockMvc.perform(delete("/api/buylist/" + saved.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/buylist/" + saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSearchByName() throws Exception {
        repository.save(sample);
        BuyList other = createSampleBuyList();
        other.setName("Other");
        repository.save(other);

        mockMvc.perform(get("/api/buylist").param("name", "Weekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Weekly"));

        // also test createdAt range
        String from = sample.getCreatedAt().toString();
        String to = sample.getCreatedAt().toString();
        mockMvc.perform(get("/api/buylist").param("createdFrom", from).param("createdTo", to))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    private BuyList createSampleBuyList() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Milk");
        ingredient.setStoreSection("Dairy");
        ingredient.setUnitOfMeasure("L");
        ingredient = ingredientsRepository.save(ingredient);

        BuyList bl = BuyList.builder().name("Weekly").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        BuyListItem item = new BuyListItem();
        item.setQuantity(2.0);
        item.setIngredient(ingredient);
        item.setBuyList(bl);

        bl.setItems(Arrays.asList(item));

        return bl;
    }
}
