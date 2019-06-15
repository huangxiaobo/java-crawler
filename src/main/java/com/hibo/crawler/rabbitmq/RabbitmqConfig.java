package com.hibo.crawler.rabbitmq;

import com.hibo.crawler.Constants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

  @Bean
  public Queue urlQueue() {
    // 待抓取用户url
    return new Queue(Constants.MQ_QUEUE_NAME);
  }

  @Bean
  public TopicExchange urlExchange() {
    return new TopicExchange(Constants.MQ_EXCHANGE_NAME);
  }

  @Bean
  public Binding urlBinding(
      @Qualifier("urlQueue") Queue queue, @Qualifier("urlExchange") TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(Constants.MQ_ROUTING_KEY);
  }

  @Bean
  public Queue pageQueue() {
    // 待解析页面content
    return new Queue(Constants.MQ_PAGE_QUEUE_NAME);
  }

  @Bean
  public TopicExchange pageExchange() {
    return new TopicExchange(Constants.MQ_PAGE_EXCHANGE_NAME);
  }

  @Bean
  public Binding pageBinding(
      @Qualifier("pageQueue") Queue queue, @Qualifier("pageExchange") TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(Constants.MQ_PAGE_ROUTING_KEY);
  }

  @Bean
  public Queue jsonQueue() {
    // 已经解析的用户信息
    return new Queue(Constants.MQ_JSON_QUEUE_NAME);
  }

  @Bean
  public TopicExchange jsonExchange() {
    return new TopicExchange(Constants.MQ_JSON_EXCHANGE_NAME);
  }

  @Bean
  public Binding jsonBinding(
      @Qualifier("jsonQueue") Queue queue, @Qualifier("jsonExchange") TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(Constants.MQ_JSON_ROUTING_KEY);
  }
}
