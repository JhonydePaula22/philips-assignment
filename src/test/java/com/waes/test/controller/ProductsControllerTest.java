package com.waes.test.controller;

import com.waes.test.TestConstants;
import com.waes.test.model.ErrorDTO;
import com.waes.test.model.NewProductDTO;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.ProductsDTO;
import com.waes.test.model.UpdateProductDTO;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.with;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductsControllerTest {

    private static String id = "1";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = TestConstants.BASE_URL;
        RestAssured.port = TestConstants.PORT;
    }

    @Test
    @Order(1)
    void should_get_200_and_no_data_when_get_products_and_no_products_exists() {
        final String path = TestConstants.PRODUCTS_PATH;
        ProductsDTO actual = with().contentType("application/json").request("GET", path)
                .then().statusCode(200).extract().as(ProductsDTO.class);
        Assertions.assertEquals(new ProductsDTO(), actual);
    }

    @Test
    @Order(2)
    void should_get_400_when_get_product_that_does_not_exists() {
        final String path = String.format(TestConstants.PRODUCTS_ID_PATH, id);
        ErrorDTO actual = with().contentType("application/json").request("GET", path)
                .then().statusCode(400).extract().as(ErrorDTO.class);
        Assertions.assertEquals(new ErrorDTO().message("Product not found with the provided Id"), actual);
    }

    @Test
    @Order(3)
    void should_get_201_and_create_new_product_and_get_status_201() {
        final String path = TestConstants.PRODUCTS_PATH;
        final NewProductDTO newProductDTO = new NewProductDTO()
                .name("product").price(new BigDecimal("12.01")).quantity(1);
        ProductDTO actual = with().body(newProductDTO).contentType("application/json").request("POST", path)
                .then().statusCode(201).extract().as(ProductDTO.class);
        Assertions.assertEquals("product", actual.getName());
        Assertions.assertEquals(new BigDecimal("12.01"), actual.getPrice());
        Assertions.assertEquals(1, actual.getQuantity());
        Assertions.assertNotNull(actual.getId());
        id = actual.getId();
    }

    @Test
    @Order(4)
    void should_get_200_and_be_able_to_get_products() {
        final String path = TestConstants.PRODUCTS_PATH;
        ProductsDTO actual = with().contentType("application/json").request("GET", path)
                .then().statusCode(200).extract().as(ProductsDTO.class);
        Assertions.assertEquals(new ProductsDTO()
                        .bundle(new ArrayList<>(Arrays.asList(new ProductDTO().name("product")
                                .price(new BigDecimal("12.01"))
                                .quantity(1).id(id))))
                , actual);
    }

    @Test
    @Order(5)
    void should_get_200_and_be_able_to_get_product() {
        final String path = String.format(TestConstants.PRODUCTS_ID_PATH, id);
        ProductDTO actual = with().contentType("application/json").request("GET", path)
                .then().statusCode(200).extract().as(ProductDTO.class);
        Assertions.assertEquals(new ProductDTO().name("product")
                        .price(new BigDecimal("12.01"))
                        .quantity(1).id(id)
                , actual);
    }

    @Test
    @Order(6)
    void should_get_200_and_be_able_to_update_product() {
        final String path = String.format(TestConstants.PRODUCTS_ID_PATH, id);
        final UpdateProductDTO newProductDTO = new UpdateProductDTO()
                .name("product updated").price(new BigDecimal("12.02")).quantity(2);
        ProductDTO actual = with().body(newProductDTO).contentType("application/json").request("PATCH", path)
                .then().statusCode(200).extract().as(ProductDTO.class);
        Assertions.assertEquals("product updated", actual.getName());
        Assertions.assertEquals(new BigDecimal("12.02"), actual.getPrice());
        Assertions.assertEquals(2, actual.getQuantity());
        Assertions.assertNotNull(actual.getId());
    }

    @Test
    @Order(7)
    void should_get_200_and_delete_product() {
        final String path = String.format(TestConstants.PRODUCTS_ID_PATH, id);
        with().contentType("application/json").request("DELETE", path)
                .then().statusCode(204);
    }
}