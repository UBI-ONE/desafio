package com.ubione.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubione.order.application.CreateOrderUseCase;
import com.ubione.order.application.GetOrderUseCase;
import com.ubione.order.domain.model.OrderStatus;
import com.ubione.order.interfaces.web.controller.OrderController;
import com.ubione.order.interfaces.web.dto.CreateOrderRequest;
import com.ubione.order.interfaces.web.dto.OrderResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private GetOrderUseCase getOrderUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setExternalId("ORDER-CTRL");
        CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest();
        item.setProductCode("P1");
        item.setDescription("Beer");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("10.00"));
        request.setItems(Collections.singletonList(item));

        OrderResponse response = new OrderResponse();
        response.setId(1L);
        response.setExternalId("ORDER-CTRL");
        response.setStatus(OrderStatus.SENT_TO_PRODUCT_B);
        response.setTotalAmount(new BigDecimal("10.00"));
        response.setReceivedAt(OffsetDateTime.now());

        Mockito.when(createOrderUseCase.execute(Mockito.any(CreateOrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.externalId", is("ORDER-CTRL")))
                .andExpect(jsonPath("$.totalAmount", is(10.00)));
    }
}
