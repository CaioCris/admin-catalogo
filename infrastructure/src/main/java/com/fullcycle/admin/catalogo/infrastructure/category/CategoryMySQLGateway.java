package com.fullcycle.admin.catalogo.infrastructure.category;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils.like;

@Service
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository repository;

    public CategoryMySQLGateway(final CategoryRepository repository) {
        this.repository = repository;
    }

    private Category saveCategory(Category category) {
        return this.repository.save(CategoryJpaEntity.from(category)).toAggregate();
    }

    @Override
    public Category create(final Category category) {
        return saveCategory(category);
    }

    @Override
    public Category update(final Category category) {
        return saveCategory(category);
    }

    @Override
    public void deleteById(final CategoryID id) {
        final var idValue = id.getValue();
        if (this.repository.existsById(idValue)) this.repository.deleteById(idValue);
    }

    @Override
    public Optional<Category> findById(final CategoryID id) {
        return this.repository.findById(id.getValue()).map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery query) {
        // Pagination
        final var pageRequest = PageRequest.of(query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()),
                        query.sort())
        );

        // Dynamic Search
        final var specifications = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(str -> {
                    final Specification<CategoryJpaEntity> nameLike = like("name", str);
                    final Specification<CategoryJpaEntity> descriptionLike = like("description", str);
                    return nameLike.or(descriptionLike);
                })
                .orElse(null);

        final var pageResult = this.repository.findAll(Specification.where(specifications), pageRequest);
        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }


}
