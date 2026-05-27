package com.devsquad10.order.unit;

import com.devsquad10.order.application.dto.message.StockDecrementMessage;
import com.devsquad10.order.application.messaging.OrderMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class RoutingDistributionTest {

    @Autowired
    private OrderMessageService orderMessageService;

    @Test
    void testConsistentHashRouting() {
        // 서로 다른 상품 10개에 대해 각각 주문이 발생했다고 가정
        for (int i = 0; i < 10; i++) {
            UUID randomProductId = UUID.randomUUID();
            // 1. @AllArgsConstructor가 존재하는 경우의 생성 방식
            StockDecrementMessage message = new StockDecrementMessage(
                    UUID.randomUUID(),       // orderId
                    randomProductId,         // productId (해싱의 기준)
                    UUID.randomUUID(),       // supplierId
                    "테스트 상품명 " + i,      // productName
                    5,                       // quantity
                    "REQUESTED",             // status
                    15000                    // price
            );

            orderMessageService.sendStockDecrementMessage(message);
        }

        // 테스트 통과 후 RabbitMQ UI에서 3개 큐의 상태를 육안으로 확인합니다.
    }
}