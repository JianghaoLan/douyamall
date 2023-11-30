package org.lanjianghao.douyamall.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.elasticsearch.client.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
public class DouyamallElasticSearchConfig extends AbstractElasticsearchConfiguration {

    private final RestClients.ElasticsearchRestClient esRestClient;

    DouyamallElasticSearchConfig() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("172.16.2.132:9200")
                .build();
        this.esRestClient = RestClients.create(clientConfiguration);
    }

    @NotNull
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        return esRestClient.rest();
    }

    @NotNull
    @Bean
    public ElasticsearchClient restClientTransport() {
        ElasticsearchTransport transport =
                new RestClientTransport(esRestClient.lowLevelRest(), new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
