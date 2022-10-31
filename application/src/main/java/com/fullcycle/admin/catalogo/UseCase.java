package com.fullcycle.admin.catalogo;

import com.fullcycle.admin.catalogo.domain.category.Category;

public class UseCase {
    public Category execute() {
        return Category.newCategory("Filmes", "A categoria mais assistida", true);
    }
}
