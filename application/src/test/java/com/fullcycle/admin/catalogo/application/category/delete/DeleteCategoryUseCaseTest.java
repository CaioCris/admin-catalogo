package com.fullcycle.admin.catalogo.application.category.delete;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteCategoryUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultDeleteCategoryUseCase useCase;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategory_shouldBeOk() {
        //Given
        final var name = "Filme";
        final var description = "A categoria mais assistida";
        final var isActive = true;
        final var category = Category.newCategory(name, description, isActive);
        final var expectedId = category.getId();

        //When
        doNothing().when(categoryGateway).deleteById(eq(expectedId));

        //Then
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        verify(categoryGateway, times(1)).deleteById(eq(expectedId));
    }

    @Test
    public void givenAInvalidId_whenCallsDeleteCategory_shouldBeOk() {
        //Given
        final var expectedId = CategoryID.from("123");

        //When
        doNothing().when(categoryGateway).deleteById(eq(expectedId));

        //Then
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        verify(categoryGateway, times(1)).deleteById(eq(expectedId));
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
        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).deleteById(eq(expectedId));

        //Then
        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));
        verify(categoryGateway, times(1)).deleteById(eq(expectedId));
    }

}
