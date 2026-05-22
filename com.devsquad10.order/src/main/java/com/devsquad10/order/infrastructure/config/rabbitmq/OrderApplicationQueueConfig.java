package com.devsquad10.order.infrastructure.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OrderApplicationQueueConfig {

    // exchange
    @Value("${stockMessage.exchange.stock.request}")
    private String stockRequestExchange;

    @Value("${stockMessage.exchange.stock.response}")
    private String stockResponseExchange;

    @Value("${stockMessage.exchange.stockRecovery.request}")
    private String stockRecoveryExchange;

    @Value("${shippingMessage.exchange.shipping.request}")
    private String shippingCreateRequestExchange;

    @Value("${shippingMessage.exchange.shipping.response}")
    private String shippingCreateResponseExchange;

    @Value("${shippingMessage.exchange.shipping_update.request}")
    private String shippingUpdateRequestExchange;

    @Value("${shippingMessage.exchange.shipping_update.response}")
    private String shippingUpdateResponseExchange;

    //queue
    @Value("${stockMessage.queue.stock.request}")
    private String queueRequestStock;

    @Value("${stockMessage.queue.stock.response}")
    private String queueResponseStock;

    @Value("${stockMessage.queue.stockRecovery.request}")
    private String queueStockRecovery;

    @Value("${shippingMessage.queue.shipping.request}")
    private String queueShippingCreateRequest;

    @Value("${shippingMessage.queue.shipping.response}")
    private String queueShippingCreateResponse;

    @Value("${shippingMessage.queue.shipping_update.request}")
    private String queueShippingUpdateRequest;

    @Value("${shippingMessage.queue.shipping_update.response}")
    private String queueShippingUpdateResponse;

    /**
     * exchange
     */
    @Bean
    public CustomExchange stockRequestExchange() {
        Map<String, Object> args = new HashMap<>();
        // 라우팅 키를 기준으로 해싱하도록 설정 (기본 동작이므로 생략 가능하나 명시적으로 작성)
        return new CustomExchange(stockRequestExchange, "x-consistent-hash", true, false, args);
    }

    @Bean
    public TopicExchange stockResponseExchange() {
        return new TopicExchange(stockResponseExchange);
    }

    @Bean
    public TopicExchange stockRecoveryExchange() {
        return new TopicExchange(stockRecoveryExchange);
    }

    @Bean
    public TopicExchange shippingCreateRequestExchange() {
        return new TopicExchange(shippingCreateRequestExchange);
    }

    @Bean
    public TopicExchange shippingCreateResponseExchange() {
        return new TopicExchange(shippingCreateResponseExchange);
    }

    @Bean
    public TopicExchange shippingUpdateRequestExchange() {
        return new TopicExchange(shippingUpdateRequestExchange);
    }

    @Bean
    public TopicExchange shippingUpdateResponseExchange() {
        return new TopicExchange(shippingUpdateResponseExchange);
    }

    /**
     * queue
     */
    @Bean
    public Queue queueRequestStock1() {
        return new Queue(queueRequestStock + ".1");
    }

    @Bean
    public Queue queueRequestStock2() {
        return new Queue(queueRequestStock + ".2");
    }

    @Bean
    public Queue queueRequestStock3() {
        return new Queue(queueRequestStock + ".3");
    }

    @Bean
    public Queue queueResponseStock() {
        return new Queue(queueResponseStock);
    }

    @Bean
    public Queue queueStockRecovery() {
        return new Queue(queueStockRecovery);
    }

    @Bean
    public Queue queueShippingCreateRequest() {
        return new Queue(queueShippingCreateRequest);
    }

    @Bean
    public Queue queueShippingCreateResponse() {
        return new Queue(queueShippingCreateResponse);
    }

    @Bean
    public Queue queueShippingUpdateRequest() {
        return new Queue(queueShippingUpdateRequest);
    }

    @Bean
    public Queue queueShippingUpdateResponse() {
        return new Queue(queueShippingUpdateResponse);
    }

    /**
     * binding
     */

    /**
     * binding 변경: 분할된 큐들을 Consistent Hash Exchange에 바인딩.
     * x-consistent-hash에서 with()에 들어가는 값은 라우팅 키가 아니라 큐의 가중치
     * 모든 큐에 균등하게 분배하기 위해 가중치를 "1"로 통일합니다.
     */
    @Bean
    public Binding bindingRequestStock1() {
        return BindingBuilder.bind(queueRequestStock1())
                .to(stockRequestExchange())
                .with("1")
                .noargs();
    }

    @Bean
    public Binding bindingRequestStock2() {
        return BindingBuilder.bind(queueRequestStock2())
                .to(stockRequestExchange())
                .with("1")
                .noargs();
    }

    @Bean
    public Binding bindingRequestStock3() {
        return BindingBuilder.bind(queueRequestStock3())
                .to(stockRequestExchange())
                .with("1")
                .noargs();
    }

    @Bean
    public Binding bindingResponseStock() {
        return BindingBuilder.bind(queueResponseStock()).to(stockResponseExchange()).with(queueResponseStock);
    }

    @Bean
    public Binding bindingRecoveryStock() {
        return BindingBuilder.bind(queueStockRecovery()).to(stockRecoveryExchange()).with(queueStockRecovery);
    }

    @Bean
    public Binding bindingRequestShipping() {
        return BindingBuilder.bind(queueShippingCreateRequest())
                .to(shippingCreateRequestExchange())
                .with(queueShippingCreateRequest);
    }

    @Bean
    public Binding bindingResponseShipping() {
        return BindingBuilder.bind(queueShippingCreateResponse())
                .to(shippingCreateResponseExchange())
                .with(queueShippingCreateResponse);
    }

    @Bean
    public Binding bindingRequestShippingUpdate() {
        return BindingBuilder.bind(queueShippingUpdateRequest())
                .to(shippingUpdateRequestExchange())
                .with(queueShippingUpdateRequest);
    }

    @Bean
    public Binding bindingResponseShippingUpdate() {
        return BindingBuilder.bind(queueShippingUpdateResponse())
                .to(shippingUpdateResponseExchange())
                .with(queueShippingUpdateResponse);
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
