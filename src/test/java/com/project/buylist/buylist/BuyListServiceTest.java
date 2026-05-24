package com.project.buylist.buylist;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import com.project.buylist.ingredients.Ingredient;
import com.project.buylist.ingredients.IngredientsRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class BuyListServiceTest {

    @Autowired
    private BuyListRepository repository;

    @Autowired
    private BuyListService service;

    @Autowired
    private IngredientsRepository ingredientsRepository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        ingredientsRepository.deleteAll();
    }

    @Test
    void create_delegatesToRepository() {
        BuyList bl = createBuyListWithItem("List1");
        BuyList result = service.save(bl, "test-user");
        assertThat(result).isEqualTo(bl);
    }

    @Test
    void getById_returnsOptional() {
        BuyList bl = createBuyListWithItem("List2");
        repository.save(bl);
        Optional<BuyList> result = service.getById(bl.getId(), "test-user");
        assertThat(result).isPresent();
        assertThat(result.get())
                .usingRecursiveComparison()
                .withComparatorForType(
                        (d1, d2) -> d1.truncatedTo(ChronoUnit.MILLIS).equals(d2.truncatedTo(ChronoUnit.MILLIS)) ? 0 : 1,
                        LocalDateTime.class)
                .isEqualTo(bl);
    }

    @Test
    void update_setsNameAndSaves() {
        BuyList bl = createBuyListWithItem("Updated");
        bl = service.save(bl, "test-user");
        bl.setName("Updated Name");
        BuyList updated = service.save(bl, "test-user");

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getUpdatedAt()).isNotNull();
        assertThat(updated.getUpdatedAt()).isNotEqualTo(bl.getUpdatedAt());
        assertThat(updated)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes("(^|.*\\.)updatedAt$")
                .isEqualTo(bl);
    }

    @Test
    void existsById_delegates() {
        BuyList bl = createBuyListWithItem("Exists");
        service.save(bl, "test-user");
        assertThat(service.existsById(bl.getId(), "test-user")).isTrue();
        assertThat(service.existsById(bl.getId(), "test-user1")).isFalse();
    }

    @Test
    void delete_delegates() {
        BuyList bl = createBuyListWithItem("Exists");
        service.save(bl, "test-user");
        service.delete(bl.getId());
        assertThat(service.existsById(bl.getId(), "test-user")).isFalse();
    }

    @Test
    void search_noCriteria_returnsAll() {
        List<BuyList> all = Arrays.asList(createBuyListWithItem("A"), createBuyListWithItem("B"));
        all.get(0).setCreatedAt(LocalDateTime.now());
        all.get(1).setCreatedAt(LocalDateTime.now().plusSeconds(1));
        service.save(all.get(0), "test-user");
        service.save(all.get(1), "test-user");
        Page<BuyList> result = service.search(BuyListFilterDto.builder().build());
        assertThat(result.getContent()).usingRecursiveComparison()
                .withComparatorForType(
                        (d1, d2) -> d1.truncatedTo(ChronoUnit.MILLIS).equals(d2.truncatedTo(ChronoUnit.MILLIS)) ? 0 : 1,
                        LocalDateTime.class)
                .isEqualTo(all);
    }

    @Test
    void search_withName_buildsSpec() {
        List<BuyList> all = Arrays.asList(createBuyListWithItem("foo"));
        service.save(all.get(0), "test-user");

        BuyListFilterDto filter = BuyListFilterDto.builder().name("foo").build();
        Page<BuyList> result = service.search(filter);
        assertThat(result.getContent()).usingRecursiveComparison()
                .withComparatorForType(
                        (d1, d2) -> d1.truncatedTo(ChronoUnit.MILLIS).equals(d2.truncatedTo(ChronoUnit.MILLIS)) ? 0 : 1,
                        LocalDateTime.class)
                .isEqualTo(all);
    }

    @Test
    void search_withCreatedRange_buildsSpec() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(1);
        LocalDateTime to = now.plusDays(1);
        List<BuyList> all = Arrays.asList(createBuyListWithItem("A"));
        service.save(all.get(0), "test-user");
        BuyListFilterDto filter = BuyListFilterDto.builder().createdFrom(from).createdTo(to).build();
        Page<BuyList> result = service.search(filter);
        assertThat(result.getContent()).usingRecursiveComparison()
                .withComparatorForType(
                        (d1, d2) -> d1.truncatedTo(ChronoUnit.MILLIS).equals(d2.truncatedTo(ChronoUnit.MILLIS)) ? 0 : 1,
                        LocalDateTime.class)
                .isEqualTo(all);
    }

    @Test
    void search_withUpdatedRange_buildsSpec() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(1);
        LocalDateTime to = now.plusDays(1);
        List<BuyList> all = Arrays.asList(createBuyListWithItem("A"));
        BuyList saved = service.save(all.get(0), "test-user");
        saved.setName("Updated");
        service.save(saved, "test-user");
        BuyListFilterDto filter = BuyListFilterDto.builder().updatedFrom(from).updatedTo(to).build();
        Page<BuyList> result = service.search(filter);
        assertThat(result.getContent()).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes("(^|.*\\.)updatedAt$")
                .withComparatorForType(
                        (d1, d2) -> d1.truncatedTo(ChronoUnit.MILLIS).equals(d2.truncatedTo(ChronoUnit.MILLIS)) ? 0 : 1,
                        LocalDateTime.class)
                .isEqualTo(all);
    }

    private BuyList createBuyListWithItem(String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Test Ingredient");
        ingredient.setStoreSection("Test Section");
        ingredient.setUnitOfMeasure("kg");
        ingredient = ingredientsRepository.save(ingredient);

        BuyList bl = BuyList.builder().name(name).userId("test-user").build();

        BuyListItem item = new BuyListItem();
        item.setQuantity(1.0);
        item.setIngredient(ingredient);
        item.setBuyList(bl);

        bl.setItems(Arrays.asList(item));

        return bl;
    }
}
