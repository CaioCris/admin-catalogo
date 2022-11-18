package com.fullcycle.admin.catalogo.application.category.create;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Objects;

import static org.mockito.Mockito.*;

@IntegrationTest
public class CreateCategoryUseCaseIntegrationTest {

    @Autowired
    private CreateCategoryUseCase useCase;
    @Autowired
    private CategoryRepository categoryRepository;
    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() {
        //Given
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        Assertions.assertEquals(0, categoryRepository.count());

        //When
        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        final var actualOutput = useCase.execute(command).get();

        //Then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        Assertions.assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryRepository.findById(actualOutput.id().getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());


    }

    @Test
    public void givenAInvalidName_whenCallsCreateCategory_shouldReturnDomainException() {
        //Given
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";
        Assertions.assertEquals(0, categoryRepository.count());

        //When
        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        final var notification = useCase.execute(command).getLeft();

        //Then
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        Assertions.assertEquals(0, categoryRepository.count());

        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallsCreateCategory_shouldReturnInactiveCategoryId() {
        //Given
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        Assertions.assertEquals(0, categoryRepository.count());

        //When
        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        final var actualOutput = useCase.execute(command).get();

        //Then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        Assertions.assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryRepository.findById(actualOutput.id().getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAnException() {
        //Given
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedErrorMessage = "Gateway Error";
        final var expectedErrorCount = 1;
        Assertions.assertEquals(0, categoryRepository.count());

        //When
        //Usado com @SpyBean
        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).create(any());
        //Usado com @Mock
        //when(categoryGateway.create(any())).thenThrow(new IllegalStateException(expectedErrorMessage));

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        final var notification = useCase.execute(command).getLeft();

        //Then
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        Assertions.assertEquals(0, categoryRepository.count());

        verify(categoryGateway, times(1)).create(
                argThat(
                        category -> Objects.equals(expectedName, category.getName())
                                && Objects.equals(expectedDescription, category.getDescription())
                                && Objects.equals(expectedIsActive, category.isActive())
                                && Objects.nonNull(category.getId())
                                && Objects.nonNull(category.getCreatedAt())
                                && Objects.nonNull(category.getUpdatedAt())
                                && Objects.nonNull(category.getDeletedAt())
                )
        );
    }

}
