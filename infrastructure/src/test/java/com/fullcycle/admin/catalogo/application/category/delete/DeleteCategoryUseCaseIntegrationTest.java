package com.fullcycle.admin.catalogo.application.category.delete;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@IntegrationTest
class DeleteCategoryUseCaseIntegrationTest {

    @SpyBean
    private CategoryGateway categoryGateway;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private DeleteCategoryUseCase useCase;

    @Test
    void givenAValidId_whenCallsDeleteCategory_shouldBeOk() {
        //Given
        final var name = "Filme";
        final var description = "A categoria mais assistida";
        final var isActive = true;
        final var category = Category.newCategory(name, description, isActive);
        final var expectedId = category.getId();

        //When
        save(category);
        Assertions.assertEquals(1, categoryRepository.count());

        //Then
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenAInvalidId_whenCallsDeleteCategory_shouldBeOk() {
        //Given
        final var expectedId = CategoryID.from("123");

        //When
        Assertions.assertEquals(0, categoryRepository.count());

        //Then
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        Assertions.assertEquals(0, categoryRepository.count());
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

        //When
        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).deleteById(expectedId);

        //Then
        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedIdValue));
        Assertions.assertEquals(0, categoryRepository.count());
        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    private void save(final Category... category) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(category)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }

}
