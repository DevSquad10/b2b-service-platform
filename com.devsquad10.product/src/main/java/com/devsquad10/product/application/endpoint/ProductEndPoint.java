package com.devsquad10.product.application.endpoint;

import com.devsquad10.product.application.dto.message.StockDecrementMessage;
import com.devsquad10.product.application.dto.message.StockReversalMessage;
import com.devsquad10.product.application.service.ProductEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductEndPoint {

    private final ProductEventService productEventService;

    // 1번 재고차감 파티션 큐 전담 리스너
    @RabbitListener(queues = "${stockMessage.queue.stock.request}.1", concurrency = "1")
    public void handleStockDecrementRequestPartition1(StockDecrementMessage stockDecrementMessage) {
        log.info("재고 차감 요청 수신 - 1번 리스너 상품 ID: {}, 차감 수량: {}", stockDecrementMessage.getProductId(),
                stockDecrementMessage.getQuantity());
        productEventService.decreaseStock(stockDecrementMessage);
    }

    // 2번 재고차감 파티션 큐 전담 리스너
    @RabbitListener(queues = "${stockMessage.queue.stock.request}.2", concurrency = "1")
    public void handleStockDecrementRequestPartition2(StockDecrementMessage stockDecrementMessage) {
        log.info("재고 차감 요청 수신 - 2번 리스너 상품 ID: {}, 차감 수량: {}", stockDecrementMessage.getProductId(),
                stockDecrementMessage.getQuantity());
        productEventService.decreaseStock(stockDecrementMessage);
    }

    // 3번 재고차감 파티션 큐 전담 리스너
    @RabbitListener(queues = "${stockMessage.queue.stock.request}.3", concurrency = "1")
    public void handleStockDecrementRequestPartition3(StockDecrementMessage stockDecrementMessage) {
        log.info("재고 차감 요청 수신 - 3번 리스너 상품 ID: {}, 차감 수량: {}", stockDecrementMessage.getProductId(),
                stockDecrementMessage.getQuantity());
        productEventService.decreaseStock(stockDecrementMessage);
    }

    @RabbitListener(queues = "${stockMessage.queue.stockRecovery.request}", concurrency = "1")
    public void handlerStockRecoveryRequest(StockReversalMessage stockReversalMessage) {
        log.info("재고 복원 요청 수신 - 상품 ID: {}, 복원 수량: {}", stockReversalMessage.getProductId(),
                stockReversalMessage.getQuantity());
        productEventService.recoveryStock(stockReversalMessage);
    }
}
