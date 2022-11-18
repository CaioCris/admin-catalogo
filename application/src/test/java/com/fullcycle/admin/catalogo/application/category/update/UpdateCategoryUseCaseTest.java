package com.fullcycle.admin.catalogo.application.category.update;

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

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        //Given
        final var oldCategory = Category.newCategory("Film", null, true);
        final var expectedId = oldCategory.getId();
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        //When
        when(categoryGateway.findById(expectedId)).thenReturn(Optional.of(oldCategory.clone()));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
        final var actualOutput = useCase.execute(command).get();

        //Then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).findById(expectedId);
        verify(categoryGateway, times(1)).update(
                argThat(
                        updatedCategory -> Objects.equals(expectedId, updatedCategory.getId())
                                && Objects.equals(expectedName, updatedCategory.getName())
                                && Objects.equals(expectedDescription, updatedCategory.getDescription())
                                && Objects.equals(expectedIsActive, updatedCategory.isActive())
                                && Objects.equals(oldCategory.getCreatedAt(), updatedCategory.getCreatedAt())
                                && updatedCategory.getUpdatedAt().isAfter(oldCategory.getUpdatedAt())
                                && Objects.isNull(updatedCategory.getDeletedAt())
                )
        );
    }

    @Test
    void givenAInvalidName_whenCallsUpdateCategory_shouldReturnDomainException() {
        //Given
        final var oldCategory = Category.newCategory("Film", null, true);
        final var expectedId = oldCategory.getId();
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        //When
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
        when(categoryGateway.findById(expectedId)).thenReturn(Optional.of(oldCategory.clone()));
        final var notification = useCase.execute(command).getLeft();

        //Then
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    void givenAValidCommandWithInactiveCategory_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        //Given
        final var oldCategory = Category.newCategory("Film", null, true);
        final var expectedId = oldCategory.getId();
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        //When
        when(categoryGateway.findById(expectedId)).thenReturn(Optional.of(oldCategory.clone()));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        Assertions.assertTrue(oldCategory.isActive());
        Assertions.assertNull(oldCategory.getDeletedAt());

        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
        final var actualOutput = useCase.execute(command).get();

        //Then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).findById(expectedId);
        verify(categoryGateway, times(1)).update(
                argThat(
                        updatedCategory -> Objects.equals(expectedId, updatedCategory.getId())
                                && Objects.equals(expectedName, updatedCategory.getName())
                                && Objects.equals(expectedDescription, updatedCategory.getDescription())
                                && Objects.equals(expectedIsActive, updatedCategory.isActive())
                                && Objects.equals(oldCategory.getCreatedAt(), updatedCategory.getCreatedAt())
                                && updatedCategory.getUpdatedAt().isAfter(oldCategory.getUpdatedAt())
                                && Objects.nonNull(updatedCategory.getDeletedAt())
                )
        );
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAException() {
        //Given
        final var oldCategory = Category.newCategory("Film", null, true);
        final var expectedId = oldCategory.getId();
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Gateway Error";
        final var expectedErrorCount = 1;

        //When
        when(categoryGateway.findById(expectedId)).thenReturn(Optional.of(oldCategory.clone()));
        when(categoryGateway.update(any())).thenThrow(new IllegalStateException(expectedErrorMessage));
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
        final var notification = useCase.execute(command).getLeft();

        //Then
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        verify(categoryGateway, times(1)).update(
                argThat(
                        updatedCategory -> Objects.equals(expectedId, updatedCategory.getId())
                                && Objects.equals(expectedName, updatedCategory.getName())
                                && Objects.equals(expectedDescription, updatedCategory.getDescription())
                                && Objects.equals(expectedIsActive, updatedCategory.isActive())
                                && Objects.equals(oldCategory.getCreatedAt(), updatedCategory.getCreatedAt())
                                && updatedCategory.getUpdatedAt().isAfter(oldCategory.getUpdatedAt())
                                && Objects.isNull(updatedCategory.getDeletedAt())
                )
        );
    }

    @Test
    void givenACommandWithInvalidId_whenCallsUpdateCategory_shouldReturnNotFoundException() {
        //Given
        final var expectedId = "123";
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId);
        final var expectedErrorCount = 1;

        //When
        when(categoryGateway.findById(CategoryID.from(expectedId))).thenReturn(Optional.empty());
        final var command = UpdateCategoryCommand.with(expectedId, expectedName, expectedDescription, expectedIsActive);
        final var actualException = Assertions.assertThrows(DomainException.class, () -> useCase.execute(command));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(categoryGateway, times(1)).findById(CategoryID.from(expectedId));
        verify(categoryGateway, times(0)).update(any());
    }

}
