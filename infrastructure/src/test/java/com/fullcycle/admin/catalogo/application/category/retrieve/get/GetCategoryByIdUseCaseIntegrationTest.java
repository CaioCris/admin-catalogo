package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.mockito.Mockito.doThrow;

@IntegrationTest
class GetCategoryByIdUseCaseIntegrationTest {

    @SpyBean
    private CategoryGateway categoryGateway;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private GetCategoryByIdUseCase useCase;

    @Test
    void givenAValidId_whenCallsGetCategoryById_shouldReturnCategory() {
        //Given
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId();
        Assertions.assertEquals(0, categoryRepository.count());

        //When
        save(category);
        final var actualCategory = useCase.execute(expectedId.getValue());

        //Then
        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        Assertions.assertEquals(expectedId, actualCategory.categoryID());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt().truncatedTo(ChronoUnit.MILLIS), actualCategory.createdAt().truncatedTo(ChronoUnit.MILLIS));
        Assertions.assertEquals(category.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS), actualCategory.updatedAt().truncatedTo(ChronoUnit.MILLIS));
        Assertions.assertEquals(category.getDeletedAt(), actualCategory.deletedAt());
    }

    @Test
    void givenAInvalidId_whenCallsGetCategoryById_shouldReturnNotFound() {
        //Given
        final var expectedId = CategoryID.from("123").getValue();
        final var expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId);
        Assertions.assertEquals(0, categoryRepository.count());

        //When
        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> useCase.execute(expectedId));

        //Then
        Assertions.assertEquals(0, categoryRepository.count());
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    void givenAValidId_whenGatewayThrowsRandomException_shouldReturnException() {
        //Given
        final var name = "Filme";
        final var description = "A categoria mais assistida";
        final var isActive = true;
        final var category = Category.newCategory(name, description, isActive);
        final var expectedId = category.getId();
        final var expectedIdValue = expectedId.getValue();
        final var expectedErrorMessage = "Gateway Error";
        Assertions.assertEquals(0, categoryRepository.count());


        //When
        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).findById(expectedId);
        final var actualException = Assertions.assertThrows(
                IllegalStateException.class, () -> useCase.execute(expectedIdValue));

        //Then
        Assertions.assertEquals(0, categoryRepository.count());
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    private void save(final Category... category) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(category)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }

}
