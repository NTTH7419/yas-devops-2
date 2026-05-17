package com.yas.search.config;

import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.yas.search.repository")
@ComponentScan(basePackages = "com.yas.search.service")
@RequiredArgsConstructor
public class ImperativeClientConfig extends ElasticsearchConfiguration {

    private final ElasticsearchDataConfig elasticsearchConfig;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchConfig.getUrl())
                .withBasicAuth(elasticsearchConfig.getUsername(), elasticsearchConfig.getPassword())
                .withClientConfigurer(
                    ElasticsearchClients.ElasticsearchHttpClientConfigurationCallback.from(
                        httpClientBuilder -> {
                            httpClientBuilder.addInterceptorLast(
                                (HttpRequestInterceptor) (request, entity, context) -> {
                                    request.setHeader("Content-Type",
                                        "application/vnd.elasticsearch+json;compatible-with=8");
                                    request.setHeader("Accept",
                                        "application/vnd.elasticsearch+json;compatible-with=8");
                                }
                            );
                            return httpClientBuilder;
                        }
                    )
                )
                .build();
    }
}
