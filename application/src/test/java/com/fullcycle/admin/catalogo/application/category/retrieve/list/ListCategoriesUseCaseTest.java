package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListCategoriesUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultListCategoriesUseCase useCase;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    public void givenAValidQuery_whenCallsListCategories_shouldReturnCategories() {
        //Given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedCreatedAt = "createdAt";
        final var expectedDirection = "asc";
        final var query = new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedCreatedAt, expectedDirection);

        final var categoryMovie = Category.newCategory("Filme", "A categoria mais assistida filmes", true);
        final var categorySeries = Category.newCategory("Series", "A categoria mais assistida series", true);
        final var categories = List.of(categoryMovie, categorySeries);

        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

        final var expectedItemsCount = 2;
        final var expectedResult = expectedPagination.map(CategoryListOutput::from);

        //When
        doReturn(expectedPagination).when(categoryGateway).findAll(eq(query));
        final var actualCategories = useCase.execute(query);

        //Then
        Assertions.assertDoesNotThrow(() -> useCase.execute(query));
        Assertions.assertEquals(expectedItemsCount, actualCategories.items().size());
        Assertions.assertEquals(expectedResult, actualCategories);
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());

        verify(categoryGateway, times(2)).findAll(eq(query));

    }

    @Test
    public void givenAValidQuery_whenCallsListCategories_shouldReturnEmptyList() {
        //Given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedCreatedAt = "createdAt";
        final var expectedDirection = "asc";
        final var query = new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedCreatedAt, expectedDirection);

        final var categories = List.<Category>of();
        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

        final var expectedItemsCount = 0;
        final var expectedResult = expectedPagination.map(CategoryListOutput::from);

        //When
        doReturn(expectedPagination).when(categoryGateway).findAll(eq(query));
        final var actualCategories = useCase.execute(query);

        //Then
        Assertions.assertDoesNotThrow(() -> useCase.execute(query));
        Assertions.assertEquals(expectedItemsCount, actualCategories.items().size());
        Assertions.assertEquals(expectedResult, actualCategories);
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());

        verify(categoryGateway, times(2)).findAll(eq(query));

    }

    @Test
    public void givenAValidQuery_whenGatewayThrowsRandomException_shouldReturnException() {

        //Given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedCreatedAt = "createdAt";
        final var expectedDirection = "asc";
        final var query = new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedCreatedAt, expectedDirection);
        final var expectedErrorMessage = "Gateway Error";

        //When
        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).findAll(eq(query));
        final var actualException = Assertions.assertThrows(
                IllegalStateException.class, () -> useCase.execute(query));

        //Then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

}
