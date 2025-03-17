package com.devsquad10.order.infrastructure.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderApplicationQueueConfig {

	// 일반 상황
	@Value("${message.exchange.stock}")
	private String stockExchange;

	@Value("${message.queue.stock}")
	private String queueStock;

	// 예외 처리
	@Value("${message.exchange.error}")
	private String errorProductStockExchange;

	@Value("${message.queue.error.stock}")
	private String queueErrorProductStock;

	/**
	 * exchange
	 */
	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(stockExchange);
	}

	@Bean
	public TopicExchange errorOrderExchange() {
		return new TopicExchange(errorProductStockExchange);
	}

	/**
	 * queue
	 */
	@Bean
	public Queue queueStock() {
		return new Queue(queueStock);
	}

	@Bean
	public Queue queueErrorProductStock() {
		return new Queue(queueErrorProductStock);
	}

	/**
	 * binding
	 */
	@Bean
	public Binding bindingProduct() {
		return BindingBuilder.bind(queueStock()).to(exchange()).with(queueStock);
	}

	@Bean
	public Binding bindingErrorOrder() {
		return BindingBuilder.bind(queueErrorProductStock()).to(errorOrderExchange()).with(queueErrorProductStock);
	}

	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
