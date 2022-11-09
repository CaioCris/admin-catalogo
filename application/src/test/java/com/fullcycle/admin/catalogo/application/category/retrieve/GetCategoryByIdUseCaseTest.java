package com.fullcycle.admin.catalogo.application.category.retrieve;

import com.fullcycle.admin.catalogo.application.category.retrieve.get.DefaultGetCategoryByIdUseCase;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetCategoryByIdUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    public void givenAValidId_whenCallsDGetCategoryById_shouldCategory() {
        //Given
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId();

        //When
        doReturn(Optional.of(category.clone())).when(categoryGateway).findById(eq(expectedId));
        final var actualCategory = useCase.execute(expectedId.getValue());

        //Then
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        Assertions.assertEquals(expectedId, actualCategory.categoryID());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.createdAt());
        Assertions.assertEquals(category.getUpdatedAt(), actualCategory.updatedAt());
        Assertions.assertEquals(category.getDeletedAt(), actualCategory.deletedAt());

        verify(categoryGateway, times(2)).findById(eq(expectedId));

    }

    @Test
    public void givenAInvalidId_whenCallsGetCategoryById_shouldReturnNotFound() {
        //Given
        final var expectedId = CategoryID.from("123");
        final var expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId.getValue());

        //When
        doReturn(Optional.empty()).when(categoryGateway).findById(eq(expectedId));
        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> useCase.execute(expectedId.getValue()));

        //Then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    public void givenAValidId_whenGatewayThrowsRandomException_shouldReturnException() {
        //Given
        final var name = "Filme";
        final var description = "A categoria mais assistida";
        final var isActive = true;
        final var category = Category.newCategory(name, description, isActive);
        final var expectedId = category.getId();
        final var expectedErrorMessage = "Gateway Error";

        //When
        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).findById(eq(expectedId));
        final var actualException = Assertions.assertThrows(
                IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        //Then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

}
