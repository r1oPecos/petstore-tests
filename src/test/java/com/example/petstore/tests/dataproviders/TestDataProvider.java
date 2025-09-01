
/*
 * Copyright (c) 2025 Krzysztof Grabowski.
 */

package com.example.petstore.tests.dataproviders;

import org.testng.annotations.DataProvider;

public class TestDataProvider {

    @DataProvider(name = "petIds")
    public Object[][] providePetIds() {
        return new Object[][]{
                {0}, {1}, {9}, {10}, {11}, {30}
        };
    }
}