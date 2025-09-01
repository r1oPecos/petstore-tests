package com.example.petstore.tests.store;

import com.example.petstore.dto.IncorrectOrderNumberDeleteModel;
import com.example.petstore.dto.OrderModel;
import com.example.petstore.tests.BaseTest;
import com.example.petstore.tests.dataproviders.TestDataProvider;
import com.example.petstore.tests.utils.RetryAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.http.HttpResponse;

/*
 * Copyright (c) 2025 Krzysztof Grabowski.
 */

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class StoreTests extends BaseTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private OrderModel created;


    @Test(groups = "Store", retryAnalyzer = RetryAnalyzer.class)
    public void testGetInventory() throws Exception {
        log.info("Send request to GET /store/inventory and verify response code is 200");
        HttpResponse<String> resp = apiClient.getInventory("/store/inventory");
        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(resp.body()).isNotNull();
    }

    @Test(groups = "Store", retryAnalyzer = RetryAnalyzer.class, dataProvider = "petIds", dataProviderClass = TestDataProvider.class)
    public void testGetOrder(long orderId) throws Exception {
        log.info("Send request to GET /store/order/{} and verify response code is 200 or 404", orderId);
        HttpResponse<String> resp = apiClient.getOrder("/store/order/", orderId);
        assertThat(resp.statusCode()).isIn(200, 404);
        assertThat(resp.body()).isNotNull();
    }

    @Test(groups = "Store", retryAnalyzer = RetryAnalyzer.class)
    public void testCreateAndDeleteOrder() throws Exception {
        log.info("Create order model");
        OrderModel orderModel = new OrderModel();
        orderModel.setId((long) (Math.random() * 10) + 1);
        orderModel.setPetId(123);
        orderModel.setQuantity(2);
        orderModel.setStatus("placed");
        orderModel.setComplete(false);

        log.info("Send request to POST /store/order/{} and verify response code is 200", orderModel.getId());
        HttpResponse<String> responseCreate = apiClient.post("/store/order", orderModel);
        assertThat(responseCreate.statusCode()).isEqualTo(200);
        OrderModel responseOrder = mapper.readValue(responseCreate.body(), OrderModel.class);
        assertThat(responseOrder.getId()).isEqualTo(orderModel.getId());
        created = responseOrder;

        log.info("Send request to DELETE /store/order/{} and verify response code is 200", orderModel.getId());
        HttpResponse<String> deleteResponse = apiClient.delete("/store/order/" + created.getId());
        assertThat(deleteResponse.statusCode()).isEqualTo(200);

        log.info("Verify that the order is not visible in the db");
        retry.run(() -> {
            HttpResponse<String> response;
            try {
                response = apiClient.getOrder("/store/order/", created.getId());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (response.statusCode() == 200) {
                throw new RuntimeException("Order still exists, retrying...");
            }
            return response;
        });

        log.info("Send request to DELETE /store/order/{} and verify response code is 404 (This is not working as the order is possible to be deleted even after it is not visible in the db)", orderModel.getId());
        HttpResponse<String> deleteTwiceResponse = apiClient.delete("/store/order/" + created.getId());
        assertThat(deleteTwiceResponse.statusCode()).isEqualTo(404);
    }

    @Test(groups = "Store", retryAnalyzer = RetryAnalyzer.class)
    public void testDeleteNonExistentOrder() throws IOException, InterruptedException {
        log.info("Send request to DELETE /store/order/incorrectOrderId and verify response code is 404 and message is 'Order Not Found'");
        HttpResponse<String> response = apiClient.delete("/store/order/" + ThreadLocalRandom.current().nextInt(1000, 9999));
        IncorrectOrderNumberDeleteModel responseModel = mapper.readValue(response.body(), IncorrectOrderNumberDeleteModel.class);
        softAssert.assertEquals(responseModel.getMessage(), "Order Not Found");
        softAssert.assertEquals(responseModel.getCode(), "404");
        softAssert.assertAll();
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        if (created != null) {
            apiClient.delete("/store/order/" + created.getId());
            created = null;
        }
    }
}
