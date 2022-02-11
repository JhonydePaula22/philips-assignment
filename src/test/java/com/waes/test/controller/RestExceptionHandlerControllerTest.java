package com.waes.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waes.test.TestConstants;
import com.waes.test.exception.BadRequestException;
import com.waes.test.exception.InternalServerErrorException;
import com.waes.test.model.ErrorDTO;
import com.waes.test.model.event.EventEnum;
import com.waes.test.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerControllerTest {

    private static final String MESSAGE = "exception";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "It seems that there is an error happening internally. We are are working to fix it. Please try to access the resource later. ";
    private static final String INTERNAL_SERVER_ERROR_CREATE_COMPLEMENT_MESSAGE = "If you are trying to CREATE a resource it will be reprocessed later with the ID %s.";
    private static final String INTERNAL_SERVER_ERROR_DELETE_UPDATE_COMPLEMENT_MESSAGE = "If you are trying to UPDATE or DELETE a resource it will be reprocessed later.";
    private ObjectMapper mapper;
    private ProductService productService;
    private ProductsController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        productService = mock(ProductService.class);
        controller = new ProductsController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandlerController())
                .build();
    }

    @Test
    void should_return_ErrorDTO_and_bad_request_when_BadRequestException_happens() throws Exception {
        final ErrorDTO expected = new ErrorDTO().message(MESSAGE);

        Mockito.doThrow(new BadRequestException(MESSAGE)).when(productService).getProduct("1", false);

        mockMvc.perform(get(String.format(TestConstants.PRODUCTS_ID_PATH, "1"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(expected, mapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class)));
    }

    @Test
    void should_return_ErrorDTO_and_internal_error_when_InternalServerErrorException_happens() throws Exception {
        final ErrorDTO expected = new ErrorDTO().message(INTERNAL_SERVER_ERROR_MESSAGE);

        Mockito.doThrow(new InternalServerErrorException(MESSAGE)).when(productService).getProduct("1", false);

        mockMvc.perform(get(String.format(TestConstants.PRODUCTS_ID_PATH, "1"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(mvcResult -> assertEquals(expected, mapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class)));
    }

    @Test
    void should_return_ErrorDTO_and_internal_error_when_InternalServerErrorException_happens_with_event_CREATE() throws Exception {
        final ErrorDTO expected = new ErrorDTO().message(INTERNAL_SERVER_ERROR_MESSAGE.concat(String.format(INTERNAL_SERVER_ERROR_CREATE_COMPLEMENT_MESSAGE, "1")));

        Mockito.doThrow(new InternalServerErrorException(MESSAGE, "1", EventEnum.CREATE)).when(productService).getProduct("1", false);

        mockMvc.perform(get(String.format(TestConstants.PRODUCTS_ID_PATH, "1"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(mvcResult -> assertEquals(expected, mapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class)));
    }

    @Test
    void should_return_ErrorDTO_and_internal_error_when_InternalServerErrorException_happens_with_event_UPDATE() throws Exception {
        final ErrorDTO expected = new ErrorDTO().message(INTERNAL_SERVER_ERROR_MESSAGE.concat(INTERNAL_SERVER_ERROR_DELETE_UPDATE_COMPLEMENT_MESSAGE));

        Mockito.doThrow(new InternalServerErrorException(MESSAGE, "1", EventEnum.UPDATE)).when(productService).getProduct("1", false);

        mockMvc.perform(get(String.format(TestConstants.PRODUCTS_ID_PATH, "1"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(mvcResult -> assertEquals(expected, mapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class)));
    }

    @Test
    void should_return_ErrorDTO_and_internal_error_when_InternalServerErrorException_happens_with_event_DELETE() throws Exception {
        final ErrorDTO expected = new ErrorDTO().message(INTERNAL_SERVER_ERROR_MESSAGE.concat(INTERNAL_SERVER_ERROR_DELETE_UPDATE_COMPLEMENT_MESSAGE));

        Mockito.doThrow(new InternalServerErrorException(MESSAGE, "1", EventEnum.UPDATE)).when(productService).getProduct("1", false);

        mockMvc.perform(get(String.format(TestConstants.PRODUCTS_ID_PATH, "1"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(mvcResult -> assertEquals(expected, mapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class)));
    }

    @Test
    void should_return_ErrorDTO_and_internal_server_error_when_Exception_happens() throws Exception {
        final ErrorDTO expected = new ErrorDTO().message("Something went wrong. We are are working to fix it.");

        Mockito.doThrow(new RuntimeException(MESSAGE)).when(productService).getProduct("1", false);

        mockMvc.perform(get(String.format(TestConstants.PRODUCTS_ID_PATH, "1"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(mvcResult -> assertEquals(expected, mapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class)));
    }
}