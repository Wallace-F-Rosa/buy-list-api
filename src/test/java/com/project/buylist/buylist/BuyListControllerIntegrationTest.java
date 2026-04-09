package com.project.buylist.buylist;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.buylist.ingredients.Ingredient;
import com.project.buylist.ingredients.IngredientsRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Import(TestConfig.class)
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

    private JwtRequestPostProcessor jwt(String userId, String... roles) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(userId).claim("buylist/roles", Arrays.asList(roles)));
    }

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        ingredientsRepository.deleteAll();
        sample = createSampleBuyList();
    }

    @Test
    public void testCreate() throws Exception {
        String json = objectMapper.writeValueAsString(sample);
        mockMvc.perform(post("/api/buylist")
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt("test-user", "ROLE_USER"))
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Weekly"))
                .andExpect(jsonPath("$.userId").value("test-user"))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void testCreate_unauthorized() throws Exception {
        String json = objectMapper.writeValueAsString(sample);
        mockMvc.perform(post("/api/buylist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetById_success() throws Exception {
        BuyList saved = repository.save(sample);
        mockMvc.perform(get("/api/buylist/" + saved.getId())
                .with(jwt("test-user", "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Weekly"))
                .andExpect(jsonPath("$.userId").value("test-user"));
    }

    @Test
    void testGetById_unauthorized() throws Exception {
        BuyList saved = repository.save(sample);
        mockMvc.perform(get("/api/buylist/" + saved.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetById_notFound() throws Exception {
        sample.setUserId("test-user2");
        BuyList saved = repository.save(sample);
        mockMvc.perform(get("/api/buylist/" + saved.getId())
                .with(jwt("test-user", "ROLE_USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdate() throws Exception {
        BuyList saved = repository.save(sample);
        saved.setName("Monthly");
        String json = objectMapper.writeValueAsString(saved);
        mockMvc.perform(put("/api/buylist/" + saved.getId())
                .with(jwt("test-user", "ROLE_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Monthly"));
    }

    @Test
    public void testUpdate_notFound() throws Exception {
        sample.setId(9999L);
        String json = objectMapper.writeValueAsString(sample);
        mockMvc.perform(put("/api/buylist/" + sample.getId())
                .with(jwt("test-user", "ROLE_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdate_unauthorized() throws Exception {
        BuyList saved = repository.save(sample);
        saved.setName("Monthly");
        String json = objectMapper.writeValueAsString(saved);
        mockMvc.perform(put("/api/buylist/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDelete() throws Exception {
        BuyList saved = repository.save(sample);
        mockMvc.perform(delete("/api/buylist/" + saved.getId())
                .with(jwt("test-user", "ROLE_USER")))
                .andExpect(status().isNoContent());
        BuyList deleted = repository.findById(saved.getId()).orElse(null);
        assertNull(deleted);
    }

    @Test
    public void testDelete_notFound() throws Exception {
        mockMvc.perform(delete("/api/buylist/9999")
                .with(jwt("test-user", "ROLE_USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDelete_unauthorized() throws Exception {
        BuyList saved = repository.save(sample);
        mockMvc.perform(delete("/api/buylist/" + saved.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testSearchByName() throws Exception {
        repository.save(sample);
        BuyList other = createSampleBuyList();
        other.setName("Other");
        repository.save(other);

        mockMvc.perform(get("/api/buylist").param("name", "Weekly")
                .with(jwt("test-user", "USER_ROLE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Weekly"));

        // also test createdAt range
        String from = sample.getCreatedAt().toString();
        String to = sample.getCreatedAt().toString();
        mockMvc.perform(get("/api/buylist").param("createdFrom", from).param("createdTo", to)
                .with(jwt("test-user", "USER_ROLE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    public void testSearchByName_unauthorized() throws Exception {
        repository.save(sample);
        mockMvc.perform(get("/api/buylist").param("name", "Weekly"))
                .andExpect(status().isUnauthorized());
    }

    private BuyList createSampleBuyList() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Milk");
        ingredient.setStoreSection("Dairy");
        ingredient.setUnitOfMeasure("L");
        ingredient = ingredientsRepository.save(ingredient);

        BuyList bl = BuyList.builder().name("Weekly").userId("test-user").createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        BuyListItem item = new BuyListItem();
        item.setQuantity(2.0);
        item.setIngredient(ingredient);
        item.setBuyList(bl);

        bl.setItems(Arrays.asList(item));

        return bl;
    }
}
