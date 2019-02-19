package com.kealliang.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.thymeleaf.postprocessor.PostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lsr
 * @ClassName SearchConfig
 * @Date 2019-02-07
 * @Desc ElasticSearch相关配置
 * @Vertion 1.0
 */
@Configuration
public class SearchConfig {

    @Value("${elasticsearch.master.addr}")
    private String masterAddress;
    @Value("${elasticsearch.master.port:9300}")
    private int port;

    @Bean
    public TransportClient transportClient() throws UnknownHostException {
        Settings keal = Settings.builder()
                .put("cluster.name", "keal")
                .put("client.transport.sniff", true)
                .build();

        // ES的tcp端口默认是9300
        TransportAddress address = new TransportAddress(InetAddress.getByName(masterAddress), port);

        TransportClient client = new PreBuiltTransportClient(keal)
                .addTransportAddress(address);
        return client;
    }

}
