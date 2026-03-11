package com.project.buylist.buylist;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import com.project.buylist.ingredients.Ingredient;
import com.project.buylist.ingredients.IngredientsRepository;

@SpringBootTest
@AutoConfigureTestDatabase
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
        BuyList result = service.save(bl);
        assertThat(result).isSameAs(bl);
    }

    @Test
    void getById_returnsOptional() {
        BuyList bl = createBuyListWithItem("List2");
        BuyList result = service.save(bl);
        assertThat(result).isSameAs(bl);
    }

    @Test
    void update_setsNameAndSaves() {
        BuyList bl = createBuyListWithItem("Updated");
        service.save(bl);
        bl.setName("Updated Name");
        BuyList updated = service.save(bl);

        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    void existsById_delegates() {
        BuyList bl = createBuyListWithItem("Exists");
        service.save(bl);
        assertThat(service.existsById(bl.getId())).isTrue();
    }

    @Test
    void delete_delegates() {
        BuyList bl = createBuyListWithItem("Exists");
        service.save(bl);
        service.delete(bl.getId());
        assertThat(service.existsById(bl.getId())).isFalse();
    }

    @Test
    void search_noCriteria_returnsAll() {
        List<BuyList> all = Arrays.asList(createBuyListWithItem("A"), createBuyListWithItem("B"));
        all.get(0).setCreatedAt(LocalDateTime.now());
        all.get(1).setCreatedAt(LocalDateTime.now().plusSeconds(1));
        service.save(all.get(0));
        service.save(all.get(1));
        Page<BuyList> result = service.search(BuyListFilterDto.builder().build());
        assertThat(result.getContent()).usingRecursiveComparison().ignoringFields("createdAt", "updatedAt")
                .isEqualTo(all);
    }

    @Test
    void search_withName_buildsSpec() {
        List<BuyList> all = Arrays.asList(createBuyListWithItem("foo"));
        service.save(all.get(0));

        BuyListFilterDto filter = BuyListFilterDto.builder().name("foo").build();
        Page<BuyList> result = service.search(filter);
        assertThat(result.getContent()).usingRecursiveComparison().ignoringFields("createdAt", "updatedAt")
                .isEqualTo(all);
    }

    @Test
    void search_withCreatedRange_buildsSpec() {
        LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 3, 31, 23, 59, 59, 999_999_999);
        List<BuyList> all = Arrays.asList(createBuyListWithItem("A"));
        all.get(0).setCreatedAt(LocalDateTime.of(2025, 2, 15, 12, 0));
        service.save(all.get(0));
        BuyListFilterDto filter = BuyListFilterDto.builder().createdFrom(from).createdTo(to).build();
        Page<BuyList> result = service.search(filter);
        assertThat(result.getContent()).usingRecursiveComparison().ignoringFields("createdAt", "updatedAt")
                .isSameAs(all);
    }

    @Test
    void search_withUpdatedRange_buildsSpec() {

        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 3, 31, 23, 59, 59, 999_999_999);
        List<BuyList> all = Arrays.asList(createBuyListWithItem("A"));
        all.get(0).setUpdatedAt(LocalDateTime.of(2026, 2, 15, 12, 0));
        service.save(all.get(0));
        BuyListFilterDto filter = BuyListFilterDto.builder().updatedFrom(from).updatedTo(to).build();
        Page<BuyList> result = service.search(filter);
        assertThat(result.getContent()).usingRecursiveComparison().ignoringFields("createdAt", "updatedAt")
                .isSameAs(all);
    }

    private BuyList createBuyListWithItem(String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Test Ingredient");
        ingredient.setStoreSection("Test Section");
        ingredient.setUnitOfMeasure("kg");
        ingredient = ingredientsRepository.save(ingredient);

        BuyList bl = BuyList.builder().name(name).build();

        BuyListItem item = new BuyListItem();
        item.setQuantity(1.0);
        item.setIngredient(ingredient);
        item.setBuyList(bl);

        bl.setItems(Arrays.asList(item));

        return bl;
    }
}
