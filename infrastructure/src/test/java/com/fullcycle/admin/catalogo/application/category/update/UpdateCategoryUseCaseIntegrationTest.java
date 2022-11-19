package com.fullcycle.admin.catalogo.application.category.update;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@IntegrationTest
class UpdateCategoryUseCaseIntegrationTest {

    @SpyBean
    private CategoryGateway categoryGateway;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UpdateCategoryUseCase useCase;

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        //Given
        final var oldCategory = Category.newCategory("Film", null, true);
        final var expectedId = oldCategory.getId();
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        save(oldCategory);
        Assertions.assertEquals(1, categoryRepository.count());

        //When
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
        final var actualOutput = useCase.execute(command).get();

        //Then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var actualCategory = categoryRepository.findById(actualOutput.id().getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(oldCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS), actualCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        Assertions.assertTrue(oldCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNull(actualCategory.getDeletedAt());
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
        save(oldCategory);
        Assertions.assertEquals(1, categoryRepository.count());

        //When
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
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
        save(oldCategory);
        Assertions.assertEquals(1, categoryRepository.count());

        //When
        Assertions.assertTrue(oldCategory.isActive());
        Assertions.assertNull(oldCategory.getDeletedAt());

        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
        final var actualOutput = useCase.execute(command).get();

        //Then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());


        final var actualCategory = categoryRepository.findById(actualOutput.id().getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(oldCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS), actualCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        Assertions.assertTrue(oldCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNotNull(actualCategory.getDeletedAt());
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
        save(oldCategory);
        Assertions.assertEquals(1, categoryRepository.count());

        //When
        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).update(any());
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);
        final var notification = useCase.execute(command).getLeft();

        //Then
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());

        final var actualCategory = categoryRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(oldCategory.getName(), actualCategory.getName());
        Assertions.assertEquals(oldCategory.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(oldCategory.isActive(), actualCategory.isActive());
        Assertions.assertEquals(oldCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS), actualCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        Assertions.assertEquals(oldCategory.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS), actualCategory.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS));
        Assertions.assertEquals(oldCategory.getDeletedAt(), actualCategory.getDeletedAt());
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
        final var command = UpdateCategoryCommand.with(expectedId, expectedName, expectedDescription, expectedIsActive);
        final var actualException = Assertions.assertThrows(DomainException.class, () -> useCase.execute(command));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    private void save(final Category... category) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(category)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }
}
