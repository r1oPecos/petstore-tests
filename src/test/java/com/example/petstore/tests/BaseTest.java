
/*
 * Copyright (c) 2025 Krzysztof Grabowski.
 */

package com.example.petstore.tests;

import com.aventstack.extentreports.ExtentReports;
import com.example.petstore.client.APIClient;
import com.example.petstore.config.EnvConfig;
import com.example.petstore.reporting.ExtentManager;
import com.example.petstore.tests.utils.RetryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.asserts.SoftAssert;

import java.net.http.HttpResponse;

public abstract class BaseTest {
    public static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    public SoftAssert softAssert;
    public RetryBuilder<HttpResponse<String>> retry;
    protected APIClient apiClient;
    protected ExtentReports extent;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        String baseUrl = EnvConfig.getBaseUrl();
        apiClient = new APIClient(baseUrl);
        extent = ExtentManager.getInstance();
        softAssert = new SoftAssert();
        retry = new RetryBuilder<HttpResponse<String>>().withMaxAttempts(5).withInterval(java.time.Duration.ofSeconds(2));
    }
}
