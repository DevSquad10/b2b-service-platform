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

	// exchange
	@Value("${stockMessage.exchange.stock.request}")
	private String stockRequestExchange;

	@Value("${stockMessage.exchange.stock.response}")
	private String stockResponseExchange;

	@Value("${stockMessage.queue.stock.request}")
	private String queueRequestStock;

	@Value("${stockMessage.queue.stock.response}")
	private String queueResponseStock;

	/**
	 * exchange
	 */
	@Bean
	public TopicExchange stockRequestExchange() {
		return new TopicExchange(stockRequestExchange);
	}

	@Bean
	public TopicExchange stockResponseExchange() {
		return new TopicExchange(stockResponseExchange);
	}

	/**
	 * queue
	 */
	@Bean
	public Queue queueRequestStock() {
		return new Queue(queueRequestStock);
	}

	@Bean
	public Queue queueResponseStock() {
		return new Queue(queueResponseStock);
	}

	/**
	 * binding
	 */
	@Bean
	public Binding bindingRequestStock() {
		return BindingBuilder.bind(queueRequestStock()).to(stockRequestExchange()).with(queueRequestStock);
	}

	@Bean
	public Binding bindingResponseStock() {
		return BindingBuilder.bind(queueResponseStock()).to(stockResponseExchange()).with(queueResponseStock);
	}

	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
