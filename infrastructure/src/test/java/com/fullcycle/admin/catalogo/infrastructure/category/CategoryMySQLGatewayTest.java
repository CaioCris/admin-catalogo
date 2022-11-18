package com.fullcycle.admin.catalogo.infrastructure.category;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryMySQLGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallsCreate_shouldReturnANewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryMySQLGateway.create(category);

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(category.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(category.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertEquals(category.getDeletedAt(), actualCategory.getDeletedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

        final var actualEntity = categoryRepository.findById(category.getId().getValue()).get();

        Assertions.assertEquals(category.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(category.getUpdatedAt(), actualEntity.getUpdatedAt());
        Assertions.assertEquals(category.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());

    }

    @Test
    public void givenAValidCategory_whenCallsUpdate_shouldReturnACategoryUpdated() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory("Fil", null, expectedIsActive);
        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));
        Assertions.assertEquals(1, categoryRepository.count());

        final var entity = categoryRepository.findById(category.getId().getValue()).get();
        Assertions.assertEquals(category.getName(), entity.getName());
        Assertions.assertNull(entity.getDescription());
        Assertions.assertEquals(category.isActive(), entity.isActive());

        // When
        final var updatedCategory = category.clone().update(expectedName, expectedDescription, expectedIsActive);
        final var actualCategory = categoryMySQLGateway.update(updatedCategory);
        Assertions.assertEquals(1, categoryRepository.count());

        // Then
        Assertions.assertEquals(category.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertEquals(category.getDeletedAt(), actualCategory.getDeletedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

        final var actualEntity = categoryRepository.findById(category.getId().getValue()).get();

        Assertions.assertEquals(category.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(actualEntity.getUpdatedAt()));
        Assertions.assertEquals(category.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());

    }

    @Test
    public void givenAPrePersistedCategoryAndValidCategoryId_whenTryToDeleteIt_shouldDeleteCategory() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));
        Assertions.assertEquals(1, categoryRepository.count());

        // When
        categoryMySQLGateway.deleteById(category.getId());

        // Then
        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenInvalidCategoryId_whenTryToDeleteIt_shouldDeleteCategory() {
        // Given
        Assertions.assertEquals(0, categoryRepository.count());

        // When
        categoryMySQLGateway.deleteById(CategoryID.from("invalid"));

        // Then
        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAPrePersistedCategoryAndValidCategoryId_whenCallsFindById_shouldReturnACategory() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));
        Assertions.assertEquals(1, categoryRepository.count());

        // When
        final var actualCategory = categoryMySQLGateway.findById(category.getId()).get();
        Assertions.assertEquals(1, categoryRepository.count());

        // Then
        Assertions.assertEquals(category.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(category.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertEquals(category.getDeletedAt(), actualCategory.getDeletedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenValidCategoryIdNotStored_whenCallsFindById_shouldReturnEmpty() {
        // Given
        Assertions.assertEquals(0, categoryRepository.count());

        // When
        final var actualCategory = categoryMySQLGateway.findById(CategoryID.from("empty"));

        // Then
        Assertions.assertTrue(actualCategory.isEmpty());
    }

    @Test
    public void givenAPrePersistedCategories_whenCallsFindAll_shouldReturnPaginated() {
        // Given
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var categoryMovie = Category.newCategory("Filmes", null, true);
        final var categorySeries = Category.newCategory("Série", null, true);
        final var categoryDocumentary = Category.newCategory("Documentários", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        // When
        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(categoryMovie),
                CategoryJpaEntity.from(categorySeries),
                CategoryJpaEntity.from(categoryDocumentary)
        ));

        Assertions.assertEquals(3, categoryRepository.count());

        final var query = new CategorySearchQuery(0, 1, "", "name", "asc");
        final var actualCategories = categoryMySQLGateway.findAll(query);

        // Then
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());
        Assertions.assertEquals(expectedTotal, actualCategories.total());
        Assertions.assertEquals(expectedPerPage, actualCategories.items().size());
        Assertions.assertEquals(categoryDocumentary.getId(), actualCategories.items().get(0).getId());
    }

    @Test
    public void givenEmptyCategories_whenCallsFindAll_shouldReturnEmptyPage() {
        // Given
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        Assertions.assertEquals(0, categoryRepository.count());

        // When
        final var query = new CategorySearchQuery(0, 1, "", "name", "asc");
        final var actualCategories = categoryMySQLGateway.findAll(query);

        // Then
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());
        Assertions.assertEquals(expectedTotal, actualCategories.total());
        Assertions.assertEquals(0, actualCategories.items().size());
    }

    @Test
    public void givenFollowPagination_whenCallsFindAllWithPage1_shouldReturnPaginated() {
        // Given
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var categoryMovie = Category.newCategory("Filmes", null, true);
        final var categorySeries = Category.newCategory("Série", null, true);
        final var categoryDocumentary = Category.newCategory("Documentários", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        // When
        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(categoryMovie),
                CategoryJpaEntity.from(categorySeries),
                CategoryJpaEntity.from(categoryDocumentary)
        ));

        Assertions.assertEquals(3, categoryRepository.count());

        // Page 0
        var query = new CategorySearchQuery(0, 1, "", "name", "asc");
        var actualCategories = categoryMySQLGateway.findAll(query);

        // Then
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());
        Assertions.assertEquals(expectedTotal, actualCategories.total());
        Assertions.assertEquals(expectedPerPage, actualCategories.items().size());
        Assertions.assertEquals(categoryDocumentary.getId(), actualCategories.items().get(0).getId());

        // Page 1
        expectedPage = 1;
        query = new CategorySearchQuery(1, 1, "", "name", "asc");
        actualCategories = categoryMySQLGateway.findAll(query);

        // Then
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());
        Assertions.assertEquals(expectedTotal, actualCategories.total());
        Assertions.assertEquals(expectedPerPage, actualCategories.items().size());
        Assertions.assertEquals(categoryMovie.getId(), actualCategories.items().get(0).getId());

        // Page 2
        expectedPage = 2;
        query = new CategorySearchQuery(2, 1, "", "name", "asc");
        actualCategories = categoryMySQLGateway.findAll(query);

        // Then
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());
        Assertions.assertEquals(expectedTotal, actualCategories.total());
        Assertions.assertEquals(expectedPerPage, actualCategories.items().size());
        Assertions.assertEquals(categorySeries.getId(), actualCategories.items().get(0).getId());
    }

    @Test
    public void givenAPrePersistedCategoriesAndDocAsTerms_whenCallsFindAllAndTermsMatchCategoryName_shouldReturnPaginated() {
        // Given
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var categoryMovie = Category.newCategory("Filmes", null, true);
        final var categorySeries = Category.newCategory("Série", null, true);
        final var categoryDocumentary = Category.newCategory("Documentários", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        // When
        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(categoryMovie),
                CategoryJpaEntity.from(categorySeries),
                CategoryJpaEntity.from(categoryDocumentary)
        ));

        Assertions.assertEquals(3, categoryRepository.count());

        final var query = new CategorySearchQuery(0, 1, "doc", "name", "asc");
        final var actualCategories = categoryMySQLGateway.findAll(query);

        // Then
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());
        Assertions.assertEquals(expectedTotal, actualCategories.total());
        Assertions.assertEquals(expectedPerPage, actualCategories.items().size());
        Assertions.assertEquals(categoryDocumentary.getId(), actualCategories.items().get(0).getId());
    }

    @Test
    public void givenAPrePersistedCategoriesAndMaisAssistidaAsTerms_whenCallsFindAllAndTermsMatchCategoryDescription_shouldReturnPaginated() {
        // Given
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var categoryMovie = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var categorySeries = Category.newCategory("Série", "Uma categoria assistida", true);
        final var categoryDocumentary = Category.newCategory("Documentários", "A categoria menos assistida", true);

        Assertions.assertEquals(0, categoryRepository.count());

        // When
        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(categoryMovie),
                CategoryJpaEntity.from(categorySeries),
                CategoryJpaEntity.from(categoryDocumentary)
        ));

        Assertions.assertEquals(3, categoryRepository.count());

        final var query = new CategorySearchQuery(0, 1, "mais assistida", "name", "asc");
        final var actualCategories = categoryMySQLGateway.findAll(query);

        // Then
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());
        Assertions.assertEquals(expectedTotal, actualCategories.total());
        Assertions.assertEquals(expectedPerPage, actualCategories.items().size());
        Assertions.assertEquals(categoryMovie.getId(), actualCategories.items().get(0).getId());
    }

}
