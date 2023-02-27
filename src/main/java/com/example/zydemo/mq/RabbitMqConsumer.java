//package com.example.zydemo.mq;
//
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.*;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class RabbitMqConsumer {
//
//    private static Logger logger = LoggerFactory.getLogger(RabbitMqConsumer.class);
//
//    /**
//     * 处理对象的MQ队列
//     * com.hikvision.mq.queune.name(224测试用)
//     * xalarm_aps_exchange_forward_to_component
//     */
//    final public static String QUEUE_NAME = "hik.bmunicipal.cover.queue";
//    final public static String DIRECT_ROUTINGKEY = "hik.municipal.device.data.routingkey";
//    final public static String DIRECT_EXCHANGE = "hik.municipal.device.exchange";
//
//    /**
//     * @param
//     * @RabbitListener(queues = {HANDLER_OBJECT_QUEUE_NAME})
//     */
//    @RabbitListener(
//            bindings = @QueueBinding(
//                    value = @Queue(value = QUEUE_NAME, autoDelete = "false"),
//                    exchange = @Exchange(value = DIRECT_EXCHANGE, type = "direct",durable = "true"),
//                    key = DIRECT_ROUTINGKEY
//            )
//    )
//    @RabbitHandler
//    public void getMqMessage(Message message) {
//        log.info("=====================rabbitMq接收参数" + message);
//        String bodyStr = new String(message.getBody());
//
//    }
//}
