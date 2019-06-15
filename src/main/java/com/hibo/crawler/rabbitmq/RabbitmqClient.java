package com.hibo.crawler.rabbitmq;

import com.hibo.crawler.Constants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RabbitmqClient {

  @Autowired
  private RabbitTemplate rabbitTemplate;


  @Autowired
  @Qualifier("urlExchange")
  private TopicExchange urlExchange;

  @Autowired
  @Qualifier("pageExchange")
  private TopicExchange pageExchange;


  @Autowired
  @Qualifier("jsonExchange")
  private TopicExchange jsonExchange;

  public void sendFetchTask(String s) {
    rabbitTemplate.convertAndSend(urlExchange.getName(), Constants.MQ_ROUTING_KEY, s);
  }

  public void sendParseTask(String s) {
    rabbitTemplate.convertAndSend(pageExchange.getName(), Constants.MQ_PAGE_ROUTING_KEY, s);
  }

  public void sendProcessTask(String s) {
    rabbitTemplate.convertAndSend(jsonExchange.getName(), Constants.MQ_JSON_ROUTING_KEY, s);
  }
}
