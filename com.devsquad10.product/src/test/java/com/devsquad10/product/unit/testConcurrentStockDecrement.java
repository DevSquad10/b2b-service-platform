package com.devsquad10.product.unit;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import com.devsquad10.product.application.dto.message.StockDecrementMessage;
import com.devsquad10.product.application.service.ProductEventService;
import com.devsquad10.product.domain.enums.ProductStatus;
import com.devsquad10.product.domain.model.Product;
import com.devsquad10.product.domain.repository.ProductRepository;

@SpringBootTest
public class testConcurrentStockDecrement {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private RabbitTemplate rabbitTemplate;

	@InjectMocks
	private ProductEventService productEventService;

	private UUID productId;

	private Product product;

	private StockDecrementMessage stockDecrementMessage;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Mockito 초기화
		productId = UUID.randomUUID();
		product = Product.builder()
			.id(productId)
			.name("테스트 상품")
			.description("테스트 상품입니다.")
			.quantity(10)
			.price(1000)
			.supplierId(UUID.randomUUID())
			.hubId(UUID.randomUUID())
			.status(ProductStatus.AVAILABLE)
			.build();
		stockDecrementMessage = StockDecrementMessage.builder()
			.orderId(UUID.randomUUID())
			.productId(productId)
			.quantity(5)
			.build(); // 주문 수량 5
	}

}
