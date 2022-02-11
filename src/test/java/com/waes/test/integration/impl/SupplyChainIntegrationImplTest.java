package com.waes.test.integration.impl;

import com.waes.test.exception.BadRequestException;
import com.waes.test.integration.SupplyChainIntegration;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.ProductsDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.event.EventEnum;
import com.waes.test.util.HttpUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
class SupplyChainIntegrationImplTest {

    @Mock
    private HttpUtils httpUtils;

    private SupplyChainIntegration integration;

    private String url = "http://localhost/resource";

    @BeforeEach
    void setUp() {
        integration = new SupplyChainIntegrationImpl("http://localhost", "/resource", httpUtils);
    }

    @Test
    void should_get_empty_list_of_products() {
        Mockito.when(httpUtils.executeCall(ArgumentMatchers.any(Supplier.class))).thenReturn(null);
        ProductsDTO expected = new ProductsDTO();

        ProductsDTO actual = integration.getProducts();

        Assertions.assertEquals(expected, actual);
        Mockito.verify(httpUtils, Mockito.times(1)).executeCall(ArgumentMatchers.any(Supplier.class));
    }

    @Test
    void should_get_list_of_products() {
        ProductsDTO expected = new ProductsDTO().bundle(List.of(new ProductDTO()));
        Mockito.when(httpUtils.executeCall(ArgumentMatchers.any(Supplier.class)))
                .thenReturn(expected);

        ProductsDTO actual = integration.getProducts();

        Assertions.assertEquals(expected, actual);
        Mockito.verify(httpUtils, Mockito.times(1)).executeCall(ArgumentMatchers.any(Supplier.class));
    }

    @Test
    void should_get_product() {
        ProductDTO expected = new ProductDTO();
        Mockito.when(httpUtils.executeCall(ArgumentMatchers.any(Supplier.class)))
                .thenReturn(expected);

        ProductDTO actual = integration.getProduct("1");

        Assertions.assertEquals(expected, actual);
        Mockito.verify(httpUtils, Mockito.times(1)).executeCall(ArgumentMatchers.any(Supplier.class));
    }

    @Test
    void should_create_new_product() {
        ProductDTO expected = new ProductDTO();
        Mockito.when(httpUtils.executeCallWithObserver(ArgumentMatchers.any(Supplier.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(EventEnum.CREATE)))
                .thenReturn(expected);

        ProductDTO actual = integration.createNewProduct(expected);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(httpUtils, Mockito.times(1)).executeCallWithObserver(ArgumentMatchers.any(Supplier.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(EventEnum.CREATE));
    }

    @Test
    void should_update_product() {
        UpdateProductDTO updateProductDTO = new UpdateProductDTO();
        ProductDTO expected = new ProductDTO().id("1");
        Mockito.when(httpUtils.executeCall(ArgumentMatchers.any(Supplier.class)))
                .thenReturn(expected);
        Mockito.when(httpUtils.executeCallWithObserver(ArgumentMatchers.any(Supplier.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(EventEnum.UPDATE)))
                .thenReturn(expected);

        ProductDTO actual = integration.updateProduct(updateProductDTO, "1");

        Assertions.assertEquals(expected, actual);
        Mockito.verify(httpUtils, Mockito.times(1)).executeCall(ArgumentMatchers.any(Supplier.class));
        Mockito.verify(httpUtils, Mockito.times(1)).executeCallWithObserver(ArgumentMatchers.any(Supplier.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(EventEnum.UPDATE));
    }

    @Test
    void should_fail_to_update_product() {
        UpdateProductDTO updateProductDTO = new UpdateProductDTO();
        Mockito.when(httpUtils.executeCall(ArgumentMatchers.any(Supplier.class)))
                .thenThrow(new BadRequestException("We could not find a valid Product with the provided Id 1."));

        Assertions.assertThrows(BadRequestException.class, () ->  integration.updateProduct(updateProductDTO, "1"));
        Mockito.verify(httpUtils, Mockito.times(1)).executeCall(ArgumentMatchers.any(Supplier.class));
    }

    @Test
    void should_delete_product() {
        ProductDTO expected = new ProductDTO().id("1");
        Mockito.when(httpUtils.executeCall(ArgumentMatchers.any(Supplier.class)))
                .thenReturn(expected);
        Mockito.when(httpUtils.executeCallWithObserver(ArgumentMatchers.any(Supplier.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(EventEnum.DELETE)))
                .thenReturn(expected);

        integration.deleteProduct("1");

        Mockito.verify(httpUtils, Mockito.times(1)).executeCall(ArgumentMatchers.any(Supplier.class));
        Mockito.verify(httpUtils, Mockito.times(1)).executeCallWithObserver(ArgumentMatchers.any(Supplier.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(EventEnum.DELETE));
    }

    @Test
    void should_fail_to_delete_product() {
        Mockito.when(httpUtils.executeCall(ArgumentMatchers.any(Supplier.class)))
                .thenThrow(new BadRequestException("We could not find a valid Product with the provided Id 1."));

        Assertions.assertThrows(BadRequestException.class, () ->  integration.deleteProduct("1"));
        Mockito.verify(httpUtils, Mockito.times(1)).executeCall(ArgumentMatchers.any(Supplier.class));
    }
}