package com.fullcycle.admin.catalogo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    public void testNewCategory() {
        Assertions.assertNotNull(new Category());
    }
}
