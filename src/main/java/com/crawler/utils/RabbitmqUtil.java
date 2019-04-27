package com.crawler.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq 实用类
 */
public class RabbitmqUtil {
    private static Connection connection;

    static {
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("192.168.0.108");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("rabbitmq");
        connectionFactory.setPassword("rabbitmq");
        connectionFactory.setVirtualHost("/");

        try {
            connection = connectionFactory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnection() {
        return connection;
    }
}
