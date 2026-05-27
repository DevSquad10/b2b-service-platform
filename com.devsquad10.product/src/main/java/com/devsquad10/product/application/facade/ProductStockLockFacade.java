package com.devsquad10.product.application.facade;

import com.devsquad10.product.application.dto.message.StockDecrementMessage;
import com.devsquad10.product.application.dto.message.StockReversalMessage;
import com.devsquad10.product.application.service.ProductEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductStockLockFacade {

    private final RedissonClient redissonClient;
    private final ProductEventService productEventService;

    public void decreaseStockWithLock(StockDecrementMessage message) {
        executeWithLock(message.getProductId(), () -> productEventService.decreaseStock(message));
    }

    public void recoveryStockWithLock(StockReversalMessage message) {
        executeWithLock(message.getProductId(), () -> productEventService.recoveryStock(message));
    }

    /**
     * 분산 락 제어 및 예외 처리 공통 메서드
     */
    private void executeWithLock(UUID productId, Runnable operation) {
        String lockKey = "lock:product:" + productId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean available = lock.tryLock(5, 3, TimeUnit.SECONDS);

            if (!available) {
                log.error("Redisson Lock 획득 타임아웃 - 상품 ID: {}", productId);
                // 정정된 예외 처리: 상품 미존재 예외가 아닌 락 획득 전용 예외 처리
                throw new RuntimeException("락 획득 실패 타임아웃");
            }

            // 전달받은 비즈니스 로직 실행
            operation.run();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 대기 중 스레드 인터럽트 발생", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
