package com.modelling.guiservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.modelling.guiservice.config.properties.ElasticsearchProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.io.IOException;
import java.io.StringReader;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchConfig {
    private final ElasticsearchProperties properties;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient.builder(
                        new HttpHost(properties.getHost(), properties.getPort(), "http"))
                .build();


        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        ElasticsearchClient client = new ElasticsearchClient(transport);

        createTemplateIfNotExists(client, "model-profiles-template");

        return client;
    }

    @Bean(name = {"elasticsearchOperation","elasticsearchTemplate"})
    public ElasticsearchOperations elasticsearchOperations(
            ElasticsearchClient client
    ) {
        return new ElasticsearchTemplate(client);
    }


    public void createTemplateIfNotExists(ElasticsearchClient client, String templateName) {
        String jsonTemplate = """
                {
                  "index_patterns": ["model_*"],
                  "mappings": {
                    "dynamic_templates": [
                      {
                        "strings_as_keywords": {
                          "match_mapping_type": "string",
                          "mapping": {
                            "type": "keyword"
                          }
                        }
                      }
                    ]
                  }
                }
                """;

        try {

            boolean exists = client.indices().existsTemplate(e -> e.name(templateName)).value();
            if (exists) {
                log.info("Template '{}' already exists. Skipping creation.", templateName);
                return;
            }
            client.indices().putTemplate(b -> b
                    .name(templateName)
                    .withJson(new StringReader(jsonTemplate))
            );
            log.info("Template '{}' created successfully.", templateName);
        } catch (ElasticsearchException | IOException e) {
            log.error("Error while checking or creating template '{}': {}", templateName, e.getMessage(), e);
            throw new RuntimeException("Failed to create or verify template", e);
        }
    }

}
