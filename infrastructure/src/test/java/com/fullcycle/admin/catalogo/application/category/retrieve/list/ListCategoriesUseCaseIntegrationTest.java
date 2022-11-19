package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

@IntegrationTest
class ListCategoriesUseCaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DefaultListCategoriesUseCase useCase;

    @BeforeEach
    void mockUp() {
        final var category01 = Category.newCategory("Filmes", "A categoria mais assistida filmes", true);
        final var category02 = Category.newCategory("Netflix Originals", "Títulos de autoria da Netflix", true);
        final var category03 = Category.newCategory("Amazon Originals", "Títulos de autoria da Amazon", true);
        final var category04 = Category.newCategory("Documentários", null, true);
        final var category05 = Category.newCategory("Sports", null, true);
        final var category06 = Category.newCategory("Kids", "Categoria para crianças", true);
        final var category07 = Category.newCategory("Series", null, true);

        final var categories =
                Stream.of(category01, category02, category03, category04, category05, category06, category07)
                        .map(CategoryJpaEntity::from)
                        .toList();

        categoryRepository.saveAllAndFlush(categories);
    }

    @Test
    void givenAValidTerm_whenTermDoesntMatchsPrePersisted_shouldReturnEmptyPage() {
        //Given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "não existe";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var expectedTotal = 0;
        final var query = new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        //When
        final var actualCategories = useCase.execute(query);

        //Then
        Assertions.assertDoesNotThrow(() -> useCase.execute(query));
        Assertions.assertEquals(expectedItemsCount, actualCategories.items().size());
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());
        Assertions.assertEquals(expectedTotal, actualCategories.total());
    }

    @ParameterizedTest
    @CsvSource({
            "fil,0,10,1,1,Filmes",
            "net,0,10,1,1,Netflix Originals",
            "ZON,0,10,1,1,Amazon Originals",
            "Ki,0,10,1,1,Kids",
            "crianças,0,10,1,1,Kids",
            "da Amazon,0,10,1,1,Amazon Originals",
    })
    void givenAValidTerm_whenCallsListCategories_shouldReturnCategoriesFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final int expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var query = new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        //When
        final var actualCategories = useCase.execute(query);

        //Then
        Assertions.assertDoesNotThrow(() -> useCase.execute(query));
        Assertions.assertEquals(expectedItemsCount, actualCategories.items().size());
        Assertions.assertEquals(expectedPage, actualCategories.currentPage());
        Assertions.assertEquals(expectedPerPage, actualCategories.perPage());
        Assertions.assertEquals(expectedTotal, actualCategories.total());
        Assertions.assertEquals(expectedCategoryName, actualCategories.items().get(0).name());
    }


    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,7,7,Amazon Originals",
            "name,desc,0,10,7,7,Sports",
            "createdAt,asc,0,10,7,7,Filmes",
            "createdAt,desc,0,10,7,7,Series",
    })
    void givenAValidSortAndDirection_whenCallsListCategories_thenShouldReturnCategoriesOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedTerms = "";

        final var aQuery =
                new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,7,Amazon Originals;Documentários",
            "1,2,2,7,Filmes;Kids",
            "2,2,2,7,Netflix Originals;Series",
            "3,2,1,7,Sports",
    })
    void givenAValidPage_whenCallsListCategories_shouldReturnCategoriesPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoriesName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTerms = "";

        final var aQuery =
                new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());

        int index = 0;
        for (final String expectedName : expectedCategoriesName.split(";")) {
            final String actualName = actualResult.items().get(index).name();
            Assertions.assertEquals(expectedName, actualName);
            index++;
        }
    }

}
