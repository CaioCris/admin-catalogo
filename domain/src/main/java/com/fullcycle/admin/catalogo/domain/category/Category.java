package com.fullcycle.admin.catalogo.domain.category;

import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.Objects;

public class Category extends AggregateRoot<CategoryID> implements Cloneable {

    private String name;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Category(final CategoryID categoryID,
                    final String name,
                    final String description,
                    final boolean active,
                    final Instant createdAt,
                    final Instant updatedAt,
                    final Instant deletedAt) {
        super(categoryID);
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "'updatedAt' should not be null");
        this.deletedAt = deletedAt;
    }

    public static Category newCategory(final String name, final String description, final boolean active) {
        final var id = CategoryID.unique();
        final var now = Instant.now();
        final var deletedAt = active ? null : Instant.now();
        return new Category(id, name, description, active, now, now, deletedAt);
    }

    public static Category with(final CategoryID id,
                                final String name,
                                final String description,
                                final boolean active,
                                final Instant createdAt,
                                final Instant updatedAt,
                                final Instant deletedAt) {
        return new Category(
                id,
                name,
                description,
                active,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public static Category with(final Category category) {
        return with(
                category.getId(),
                category.name,
                category.description,
                category.isActive(),
                category.createdAt,
                category.updatedAt,
                category.deletedAt
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new CategoryValidator(this, handler).validate();
    }

    public Category activate() {
        this.deletedAt = null;
        this.active = true;
        this.updatedAt = Instant.now();
        return this;
    }

    public Category deactivate() {
        if (getDeletedAt() == null) {
            this.deletedAt = Instant.now();
        }

        this.active = false;
        this.updatedAt = Instant.now();
        return this;
    }

    public CategoryID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public Category update(final String name, final String description, final boolean isActive) {
        this.name = name;
        this.description = description;
        if (isActive) activate();
        else deactivate();
        this.updatedAt = Instant.now();
        return this;
    }

    @Override
    public Category clone() {
        try {
            Category clone = (Category) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
