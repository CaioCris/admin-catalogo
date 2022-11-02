package com.fullcycle.admin.catalogo.application.category.update;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import org.junit.jupiter.api.Assertions;
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
public class UpdateCategoryUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        //Given
        final var oldCategory = Category.newCategory("Film", null, true);
        final var expectedId = oldCategory.getId();
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        //When
        when(categoryGateway.findById(eq(expectedId))).thenReturn(Optional.of(oldCategory));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
        final var actualOutput = useCase.execute(command).get();

        //Then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).update(
                argThat(
                        updatedCategory -> Objects.equals(expectedId, updatedCategory.getId())
                                && Objects.equals(expectedName, updatedCategory.getName())
                                && Objects.equals(expectedDescription, updatedCategory.getDescription())
                                && Objects.equals(expectedIsActive, updatedCategory.isActive())
                                && Objects.equals(oldCategory.getCreatedAt(), updatedCategory.getCreatedAt())
                                && Objects.equals(oldCategory.getUpdatedAt(), updatedCategory.getUpdatedAt())
                                && Objects.isNull(updatedCategory.getDeletedAt())
                )
        );
    }

//    @Test
//    public void givenAInvalidName_whenCallsUpdateCategory_shouldReturnDomainException() {
//        //Given
//        final var oldCategory = Category.newCategory("Film", null, true);
//        final var expectedId = oldCategory.getId();
//        final String expectedName = null;
//        final var expectedDescription = "A categoria mais assistida";
//        final var expectedIsActive = true;
//        final var expectedErrorCount = 1;
//        final var expectedErrorMessage = "'name' should not be null";
//
//        //When
//        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
//        final var notification = useCase.execute(command).getLeft();
//
//        //Then
//        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
//        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
//        verify(categoryGateway, times(0)).create(any());
//    }
//
//    @Test
//    public void givenAValidCommandWithInactiveCategory_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
//        //Given
//        final var oldCategory = Category.newCategory("Film", null, true);
//        final var expectedId = oldCategory.getId();
//        final var expectedName = "Filme";
//        final var expectedDescription = "A categoria mais assistida";
//        final var expectedIsActive = false;
//
//        //When
//        when(categoryGateway.create(any())).thenAnswer(returnsFirstArg());
//        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
//        final var actualOutput = useCase.execute(command).get();
//
//        //Then
//        Assertions.assertNotNull(actualOutput);
//        Assertions.assertNotNull(actualOutput.id());
//
//        verify(categoryGateway, times(1)).create(
//                argThat(
//                        category -> Objects.equals(expectedName, category.getName())
//                                && Objects.equals(expectedDescription, category.getDescription())
//                                && Objects.equals(expectedIsActive, category.isActive())
//                                && Objects.nonNull(category.getId())
//                                && Objects.nonNull(category.getCreatedAt())
//                                && Objects.nonNull(category.getUpdatedAt())
//                                && Objects.nonNull(category.getDeletedAt())
//                )
//        );
//    }
//
//    @Test
//    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAException() {
//        //Given
//        final var oldCategory = Category.newCategory("Film", null, true);
//        final var expectedId = oldCategory.getId();
//        final var expectedName = "Filme";
//        final var expectedDescription = "A categoria mais assistida";
//        final var expectedIsActive = false;
//        final var expectedErrorMessage = "Gateway Error";
//        final var expectedErrorCount = 1;
//
//        //When
//        when(categoryGateway.create(any())).thenThrow(new IllegalStateException("Gateway Error"));
//        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
//        final var notification = useCase.execute(command).getLeft();
//
//        //Then
//        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
//        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
//        verify(categoryGateway, times(1)).create(
//                argThat(
//                        category -> Objects.equals(expectedName, category.getName())
//                                && Objects.equals(expectedDescription, category.getDescription())
//                                && Objects.equals(expectedIsActive, category.isActive())
//                                && Objects.nonNull(category.getId())
//                                && Objects.nonNull(category.getCreatedAt())
//                                && Objects.nonNull(category.getUpdatedAt())
//                                && Objects.nonNull(category.getDeletedAt())
//                )
//        );
//    }

}
