package com.example.kafka.demo.producer;


import com.alibaba.fastjson.JSON;
import com.example.kafka.demo.config.KafkaSendResultHandler;
import com.example.kafka.demo.entity.UserLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

@Component
@Slf4j
public class UserLogProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private KafkaSendResultHandler producerListener;


    public void sendLog(String userid){
        UserLog userLog = new UserLog();
        userLog.setUsername("jhp").setUserid(userid).setState("0");
        System.err.println("发送用户日志数据:"+userLog);
        ListenableFuture send = kafkaTemplate.send("user-log", JSON.toJSONString(userLog));
        send.addCallback(new SuccessCallback() {
            @Override
            public void onSuccess(@Nullable Object o) {
                log.error("消息成功");
            }
        }, new FailureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("消息失败");
            }
        });

    }

    public void sendLog1(String userid){
        UserLog userLog = new UserLog();
        userLog.setUsername("jhp").setUserid(userid).setState("0");
        System.err.println("发送用户日志数据:"+userLog);
        kafkaTemplate.setProducerListener(producerListener);
        kafkaTemplate.send("user-log", JSON.toJSONString(userLog));
    }

    @Transactional
    public void sendLog2(String userid){
        UserLog userLog = new UserLog();
        userLog.setUsername("jhp").setUserid(userid).setState("0");
        System.err.println("发送用户日志数据:"+userLog);
        kafkaTemplate.send("user-log", JSON.toJSONString(userLog));
        throw new RuntimeException("fail");
    }

    public void sendLog3(String userid){
        kafkaTemplate.executeInTransaction(new KafkaOperations.OperationsCallback() {
            @Override
            public Object doInOperations(KafkaOperations kafkaOperations) {
                UserLog userLog = new UserLog();
                userLog.setUsername("jhp").setUserid(userid).setState("0");
                System.err.println("发送用户日志数据:"+userLog);
                kafkaTemplate.send("user-log", JSON.toJSONString(userLog));
                throw new RuntimeException("fail");
            }
        });

    }



}
