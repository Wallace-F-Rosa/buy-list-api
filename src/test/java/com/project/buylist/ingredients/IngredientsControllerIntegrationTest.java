package com.project.buylist.ingredients;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class IngredientsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IngredientsRepository ingredientsRepository;

    private Ingredient testIngredient;

    private JwtRequestPostProcessor jwt(String userId, String... roles) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(userId).claim("buylist/roles", Arrays.asList(roles)));
    }

    @BeforeEach
    public void setUp() {
        // Clean up repository before each test
        ingredientsRepository.deleteAll();

        // Create test ingredient
        testIngredient = new Ingredient();
        testIngredient.setName("Tomato");
        testIngredient.setStoreSection("Produce");
        testIngredient.setUnitOfMeasure("kg");
        testIngredient.setStock(2.5);
    }

    @Test
    public void testCreateIngredient_Success() throws Exception {
        String ingredientJson = objectMapper.writeValueAsString(testIngredient);

        mockMvc.perform(post("/api/ingredient")
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt("test-user", "ROLE_USER"))
                .content(ingredientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tomato"))
                .andExpect(jsonPath("$.storeSection").value("Produce"))
                .andExpect(jsonPath("$.unitOfMeasure").value("kg"))
                .andExpect(jsonPath("$.stock").value(2.5));
    }

    @Test
    public void testCreateIngredient_ValidationError() throws Exception {
        Ingredient invalidIngredient = new Ingredient();
        invalidIngredient.setName(""); // Empty name should fail validation
        invalidIngredient.setStoreSection("Produce");
        invalidIngredient.setUnitOfMeasure("kg");

        String ingredientJson = objectMapper.writeValueAsString(invalidIngredient);

        mockMvc.perform(post("/api/ingredient")
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt("test-user", "ROLE_USER"))
                .content(ingredientJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }

    @Test
    public void testGetAllIngredients() throws Exception {
        ingredientsRepository.save(testIngredient);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Potato");
        ingredient2.setStoreSection("Produce");
        ingredient2.setUnitOfMeasure("kg");
        ingredient2.setStock(5.0);
        ingredientsRepository.save(ingredient2);

        mockMvc.perform(get("/api/ingredient")
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt("test-user", "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.content[0].name", anyOf(is("Tomato"), is("Potato"))))
                .andExpect(jsonPath("$.content[1].name", anyOf(is("Tomato"), is("Potato"))));
    }

    @Test
    public void testGetIngredientById_Success() throws Exception {
        Ingredient saved = ingredientsRepository.save(testIngredient);

        mockMvc.perform(get("/api/ingredient/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt("test-user", "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Tomato"))
                .andExpect(jsonPath("$.storeSection").value("Produce"));
    }

    @Test
    public void testGetIngredientById_NotFound() throws Exception {
        mockMvc.perform(get("/api/ingredient/9999")
                .with(jwt("test-user", "ROLE_USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateIngredient_Success() throws Exception {
        Ingredient saved = ingredientsRepository.save(testIngredient);

        Ingredient updatedIngredient = new Ingredient();
        updatedIngredient.setName("Updated Tomato");
        updatedIngredient.setStoreSection("Produce");
        updatedIngredient.setUnitOfMeasure("g");
        updatedIngredient.setStock(1500.0);

        String ingredientJson = objectMapper.writeValueAsString(updatedIngredient);

        mockMvc.perform(put("/api/ingredient/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt("test-user", "ROLE_USER"))
                .content(ingredientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Tomato"))
                .andExpect(jsonPath("$.unitOfMeasure").value("g"))
                .andExpect(jsonPath("$.stock").value(1500.0));
    }

    @Test
    public void testUpdateIngredient_NotFound() throws Exception {
        Ingredient updatedIngredient = new Ingredient();
        updatedIngredient.setName("Updated Tomato");
        updatedIngredient.setStoreSection("Produce");
        updatedIngredient.setUnitOfMeasure("kg");

        String ingredientJson = objectMapper.writeValueAsString(updatedIngredient);

        mockMvc.perform(put("/api/ingredient/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt("test-user", "ROLE_USER"))
                .content(ingredientJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteIngredient_Success() throws Exception {
        Ingredient saved = ingredientsRepository.save(testIngredient);

        mockMvc.perform(delete("/api/ingredient/" + saved.getId())
                .with(jwt("test-user", "ROLE_USER")))
                .andExpect(status().isNoContent());

        // Verify it's deleted
        mockMvc.perform(get("/api/ingredient/" + saved.getId())
                .with(jwt("test-user", "ROLE_USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteIngredient_NotFound() throws Exception {
        mockMvc.perform(delete("/api/ingredient/9999")
                .with(jwt("test-user", "ROLE_USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetByFields_ByName() throws Exception {
        ingredientsRepository.save(testIngredient);

        mockMvc.perform(get("/api/ingredient")
                .with(jwt("test-user", "ROLE_USER"))
                .param("name", "Tomato")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Tomato"));
    }

    @Test
    public void testGetByFields_ByStoreSection() throws Exception {
        ingredientsRepository.save(testIngredient);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Milk");
        ingredient2.setStoreSection("Dairy");
        ingredient2.setUnitOfMeasure("liter");
        ingredient2.setStock(1.0);
        ingredientsRepository.save(ingredient2);

        mockMvc.perform(get("/api/ingredient")
                .param("storeSection", "Produce")
                .with(jwt("test-user", "ROLE_USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].storeSection").value("Produce"));
    }

    @Test
    public void testGetByFields_ByNameAndStoreSection() throws Exception {
        ingredientsRepository.save(testIngredient);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Tomato");
        ingredient2.setStoreSection("Canned");
        ingredient2.setUnitOfMeasure("unit");
        ingredient2.setStock(10.0);
        ingredientsRepository.save(ingredient2);

        mockMvc.perform(get("/api/ingredient")
                .param("name", "Tomato")
                .param("storeSection", "Produce")
                .with(jwt("test-user", "ROLE_USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Tomato"))
                .andExpect(jsonPath("$.content[0].storeSection").value("Produce"));
    }

    @Test
    public void testGetByFields_NoParameters() throws Exception {
        ingredientsRepository.save(testIngredient);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Potato");
        ingredient2.setStoreSection("Produce");
        ingredient2.setUnitOfMeasure("kg");
        ingredient2.setStock(5.0);
        ingredientsRepository.save(ingredient2);

        mockMvc.perform(get("/api/ingredient")
                .with(jwt("test-user", "ROLE_USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }
}
