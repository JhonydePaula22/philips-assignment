package com.waes.test.service.impl;

import com.waes.test.exception.BadRequestException;
import com.waes.test.integration.SupplyChainIntegration;
import com.waes.test.model.NewProductDTO;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.ProductsDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.entity.ProductEntity;
import com.waes.test.model.event.ActionEnum;
import com.waes.test.model.event.EventTypeEnum;
import com.waes.test.observer.Observer;
import com.waes.test.repository.ProductRepository;
import com.waes.test.util.ProductsMapperUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Captor
    ArgumentCaptor<ProductEntity> productEntityArgumentCaptor;
    @Mock
    private ProductRepository repository;
    @Mock
    private SupplyChainIntegration supplyChainIntegration;
    @Mock
    private Observer<ProductDTO> observer;
    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void should_get_products_from_upstream() {
        ProductsDTO expected = new ProductsDTO();
        Mockito.when(supplyChainIntegration.getProducts()).thenReturn(expected);

        ProductsDTO actual = service.getProducts(true);

        assertEquals(expected, actual);
        Mockito.verify(supplyChainIntegration, Mockito.times(1)).getProducts();
    }

    @Test
    void should_get_products_locally() {
        ProductsDTO expected = new ProductsDTO();
        Iterable<ProductEntity> iterable = Collections.emptyList();
        Mockito.when(repository.findAll()).thenReturn(iterable);

        ProductsDTO actual = service.getProducts(false);

        assertEquals(expected, actual);
        Mockito.verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    void should_get_product_from_upstream() {
        ProductDTO expected = new ProductDTO();
        Mockito.when(supplyChainIntegration.getProduct("1")).thenReturn(expected);

        ProductDTO actual = service.getProduct("1", true);

        assertEquals(expected, actual);
        Mockito.verify(supplyChainIntegration, Mockito.times(1)).getProduct("1");
    }

    @Test
    void should_get_product_locally() {
        ProductDTO expected = new ProductDTO();
        Optional<ProductEntity> optional = Optional.of(new ProductEntity());
        Mockito.when(repository.findById("1")).thenReturn(optional);

        ProductDTO actual = service.getProduct("1", false);

        assertEquals(expected, actual);
        Mockito.verify(repository, Mockito.times(1)).findById("1");
    }

    @Test
    void should_get_product_locally_and_throw_exception_when_not_found() {
        Optional<ProductEntity> optional = Optional.empty();
        Mockito.when(repository.findById("1")).thenReturn(optional);

        assertThrows(BadRequestException.class, () -> service.getProduct("1", false));
        Mockito.verify(repository, Mockito.times(1)).findById("1");
    }

    @Test
    void should_save_product() {
        NewProductDTO newProductDTO = new NewProductDTO().name("name").quantity(1);
        ProductEntity productEntity = ProductEntity.builder().name("name").quantity(1).build();
        ProductDTO productDTO = ProductsMapperUtils.productDtofrom(productEntity);

        Mockito.when(repository.save(productEntityArgumentCaptor.capture())).thenReturn(productEntity);
        Mockito.doNothing().when(observer).notifyObserver(productDTO, ActionEnum.CREATE, EventTypeEnum.PROPAGATE);

        ProductDTO actual = service.saveProduct(newProductDTO);

        assertEquals("name", actual.getName());
        assertEquals(1, actual.getQuantity());
        assertNotNull(productEntityArgumentCaptor.getValue().getId());
        Mockito.verify(repository, Mockito.times(1)).save(productEntityArgumentCaptor.getValue());
        Mockito.verify(observer, Mockito.times(1)).notifyObserver(productDTO, ActionEnum.CREATE, EventTypeEnum.PROPAGATE);
    }

    @Test
    void should_update_product() {
        UpdateProductDTO updateProductDTO = new UpdateProductDTO().name("name").quantity(1);
        ProductEntity productEntity = ProductEntity.builder().name("name").quantity(1).build();
        ProductDTO productDTO = ProductsMapperUtils.productDtofrom(productEntity);

        Mockito.when(repository.save(productEntityArgumentCaptor.capture())).thenReturn(productEntity);
        Mockito.when(repository.countById("1")).thenReturn(1);
        Mockito.doNothing().when(observer).notifyObserver(productDTO, ActionEnum.UPDATE, EventTypeEnum.PROPAGATE);

        ProductDTO actual = service.updateProduct(updateProductDTO, "1");

        assertEquals("name", actual.getName());
        assertEquals(1, actual.getQuantity());
        assertNotNull(productEntityArgumentCaptor.getValue().getId());
        Mockito.verify(repository, Mockito.times(1)).save(productEntityArgumentCaptor.getValue());
        Mockito.verify(repository, Mockito.times(1)).countById("1");
        Mockito.verify(observer, Mockito.times(1)).notifyObserver(productDTO, ActionEnum.UPDATE, EventTypeEnum.PROPAGATE);
    }

    @Test
    void should_fail_to_update_product_not_found() {
        UpdateProductDTO updateProductDTO = new UpdateProductDTO().name("name").quantity(1);
        Mockito.when(repository.countById("1")).thenReturn(0);

        assertThrows(BadRequestException.class, () -> service.updateProduct(updateProductDTO, "1"));
        Mockito.verify(repository, Mockito.times(1)).countById("1");
        Mockito.verify(repository, Mockito.times(0)).save(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.times(0)).notifyObserver(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void should_delete_product() {
        Mockito.when(repository.countById("1")).thenReturn(1);
        Mockito.doNothing().when(repository).deleteById("1");
        Mockito.doNothing().when(observer).notifyObserver(new ProductDTO().id("1"), ActionEnum.DELETE, EventTypeEnum.PROPAGATE);

        service.deleteProduct("1");

        Mockito.verify(repository, Mockito.times(1)).deleteById("1");
        Mockito.verify(repository, Mockito.times(1)).countById("1");
        Mockito.verify(observer, Mockito.times(1)).notifyObserver(new ProductDTO().id("1"), ActionEnum.DELETE, EventTypeEnum.PROPAGATE);
    }

    @Test
    void should_fail_to_delete_product_not_found() {
        Mockito.when(repository.countById("1")).thenReturn(0);

        assertThrows(BadRequestException.class, () -> service.deleteProduct("1"));

        Mockito.verify(repository, Mockito.times(1)).countById("1");
        Mockito.verify(repository, Mockito.times(0)).save(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.times(0)).notifyObserver(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

}