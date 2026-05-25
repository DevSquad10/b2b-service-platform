package com.devsquad10.product.application.facade;

import com.devsquad10.product.application.dto.message.StockDecrementMessage;
import com.devsquad10.product.application.exception.ProductNotFoundException;
import com.devsquad10.product.application.service.ProductEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductStockLockFacade {

    private final RedissonClient redissonClient;
    private final ProductEventService productEventService;

    public void decreaseStockWithLock(StockDecrementMessage message) {
        String lockKey = "lock:product:" + message.getProductId();
        RLock lock = redissonClient.getLock(lockKey); // 락 획득

        try {
            // 락 획득 대기 시간 5초, 락 점유 시간 3초 (비즈니스 로직 소요 시간에 맞춰 조정)
            boolean available = lock.tryLock(5, 3, TimeUnit.SECONDS);

            if (!available) {
                log.error("Redisson Lock 획득 타임아웃 - 상품 ID: {}", message.getProductId());
                // 타임아웃 시 재시도 로직 혹은 Dead Letter Queue(DLQ) 전송 예외 처리 필요
                throw new ProductNotFoundException("락 획득 실패");
            }

            // 락 획득 성공 시 실제 비즈니스 로직(트랜잭션) 호출
            productEventService.decreaseStock(message);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 대기 중 스레드 인터럽트 발생", e);
        } finally {
            // 현재 스레드가 락을 점유하고 있을 때만 해제
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
