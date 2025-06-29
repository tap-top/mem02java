package com.mem0.core.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch配置类
 * 
 * @author changyu496
 */
@Configuration
public class ElasticsearchConfig {
    @Value("${elasticsearch.hosts:localhost:9200}")
    private String hosts;
    @Value("${elasticsearch.username:}")
    private String username;
    @Value("${elasticsearch.password:}")
    private String password;

    @Bean("restHighLevelClient")
    public RestHighLevelClient elasticsearchClient() {
        // 解析主机配置
        String[] hostPorts = hosts.split(",");
        HttpHost[] httpHosts = new HttpHost[hostPorts.length];
        for (int i = 0; i < hostPorts.length; i++) {
            String[] parts = hostPorts[i].trim().split(":");
            String host = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9200;
            httpHosts[i] = new HttpHost(host, port, "http");
        }
        final String u = username;
        final String p = password;
        return new RestHighLevelClient(
            RestClient.builder(httpHosts)
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    if (u != null && !u.isEmpty() && p != null && !p.isEmpty()) {
                        CredentialsProvider cp = new BasicCredentialsProvider();
                        cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(u, p));
                        return httpClientBuilder.setDefaultCredentialsProvider(cp);
                    }
                    return httpClientBuilder;
                })
        );
    }
} 