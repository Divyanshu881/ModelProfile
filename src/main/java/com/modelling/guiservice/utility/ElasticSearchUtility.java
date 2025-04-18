package com.modelling.guiservice.utility;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modelling.guiservice.dto.helper.ElasticSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ElasticSearchUtility {

    private final ElasticsearchClient client;

    private final ObjectMapper objectMapper;


    public ElasticSearchUtility(ElasticsearchClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }


    public <T> ElasticSearchResult<T> search(String indexName, Pageable pageable, Class<T> entityClass, Map<String, Object> fieldSearchMap, String globalSearch) throws IOException {
        createIndexIfNotExists(indexName);
        Query boolQuery = buildBoolQuery(fieldSearchMap, globalSearch);
        CountRequest countRequest = CountRequest.of(i -> i.index(indexName));
        CountResponse countResponse = client.count(countRequest);
        long totalDocs = countResponse.count();
        log.info("Total {} docs are present in {} index", totalDocs, indexName);
        if (totalDocs == 0) {
            return new ElasticSearchResult<>(Collections.emptyList(), 0);
        }
        // Remove duplicate sort fields
        Sort validateSort = removeDuplicateSortFields(pageable.getSort());
        // Sorting
        List<SortOptions> sortOptions = buildSortOptions(validateSort);
        return totalDocs < 10000 ? executeFromSizePagination(indexName, boolQuery, sortOptions, pageable, entityClass) : executeSearchAfterPagination(indexName, boolQuery, sortOptions, pageable, entityClass);
    }

    private void createIndexIfNotExists(String indexName) throws IOException {
        // Check if index exists
        boolean exists = client.indices()
                .exists(ExistsRequest.of(e -> e.index(indexName)))
                .value();

        if (!exists) {
            // Create index
            CreateIndexResponse response = client.indices()
                    .create(CreateIndexRequest.of(c -> c.index(indexName)));

            if (!response.acknowledged()) {
                throw new RuntimeException("Failed to create index: " + indexName);
            }
        }else{
            log.info("Index already present");
        }

    }

    private <T> ElasticSearchResult<T> executeSearchAfterPagination(String indexName, Query boolQuery, List<SortOptions> sortOptions, Pageable pageable, Class<T> entityClass) throws IOException {

        log.info("Using search _after pagination");
        Object[] searchAfterValues = null;
        SearchResponse<JsonData> searchResponse;
        for (int currentPage = 0; currentPage <= pageable.getPageNumber(); currentPage++) {
            SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
                    .index(indexName).size(pageable.getPageSize())
                    .sort(sortOptions)
                    .query(boolQuery)
                    .trackTotalHits(t -> t.enabled(true));

            if (searchAfterValues != null) {
                searchRequestBuilder.searchAfter(Arrays.stream(searchAfterValues)
                        .map(FieldValue::of)
                        .collect(Collectors.toList()));
            }
            SearchRequest searchRequest = searchRequestBuilder.build();

            log.info("Executing Elasticsearch Query: {}", searchRequest);
            searchResponse = client.search(searchRequest, JsonData.class);
            List<T> pageResults = extractResults(searchResponse, entityClass);
            if (currentPage == pageable.getPageNumber()) {
                return new ElasticSearchResult<>(pageResults, searchResponse.hits().total().value());
            }
            if (!pageResults.isEmpty()) {
                if (!searchResponse.hits().hits().isEmpty()) {
                    searchAfterValues = searchResponse.hits().hits().get(searchResponse.hits().hits().size() - 1).sort().toArray();
                }
            } else {
                break;
            }
        }
        return new ElasticSearchResult<>(Collections.emptyList(), 0);
    }


    private <T> ElasticSearchResult<T> executeFromSizePagination(String indexName, Query boolQuery, List<SortOptions> sortOptions, Pageable pageable, Class<T> entityClass) throws IOException {
        log.info("Using from+size pagination");
        SearchRequest searchRequest = SearchRequest.of(r -> r
                .index(indexName)
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize())
                .sort(sortOptions)
                .query(boolQuery)
                .trackTotalHits(t -> t.enabled(true))
        );
        log.info("Executing Elasticsearch Query: {}", searchRequest);
        SearchResponse<JsonData> searchResponse = client.search(searchRequest, JsonData.class);
        List<T> result = extractResults(searchResponse, entityClass);
        return new ElasticSearchResult<>(result, searchResponse.hits().total().value());
    }

    private List<SortOptions> buildSortOptions(Sort sort) {
        List<SortOptions> sortOptionsList = new ArrayList<>();
        for (Sort.Order order : sort) {
            sortOptionsList.add(SortOptions.of(so -> so.field(f -> f.field(order.getProperty())
                    .order(order.isAscending() ? SortOrder.Asc : SortOrder.Desc))));
        }

        sortOptionsList.add(SortOptions.of(s -> s.field(f -> f.field("_id").order(SortOrder.Desc))));

        return sortOptionsList;
    }

    private Sort removeDuplicateSortFields(Sort sort) {
        if (!sort.isSorted()) {
            return sort;
        }
        Set<String> seenFields = new LinkedHashSet<>();
        List<Sort.Order> uniqueOrder = new ArrayList<>();

        for (Sort.Order order : sort) {
            if (!seenFields.contains(order.getProperty())) {
                uniqueOrder.add(order);
                seenFields.add(order.getProperty());
            }
        }
        return Sort.by(uniqueOrder);
    }

    private Query buildBoolQuery(Map<String, Object> fieldSearchMap, String globalSearch) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if ((fieldSearchMap == null || fieldSearchMap.isEmpty()) && StringUtils.isBlank(globalSearch)) {
            return Query.of(q -> q.matchAll(m -> m));
        }

        //build query based on the condition
        if (fieldSearchMap != null && !fieldSearchMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : fieldSearchMap.entrySet()) {
                String field = entry.getKey();
                Object searchValue = entry.getValue();
                //exclusion filter
                if (searchValue instanceof List<?>) {
                    List<String> excludedValues = (List<String>) searchValue;
                    if (!excludedValues.isEmpty()) {
                        boolQueryBuilder.mustNot(mn -> mn.terms(t -> t.field(field)
                                .terms(terms -> terms.value(excludedValues.stream()
                                        .map(FieldValue::of)
                                        .collect(Collectors.toList())))));
                        log.info("Excluded values for field {} : {}", field, excludedValues);
                    }
                } else {
                    Query fieldQuery = queryBuilderForFieldSearch(field, searchValue);
                    if (fieldQuery != null) {
                        boolQueryBuilder.must(fieldQuery);
                        log.info("Added search value for field {} : {}", field, searchValue);
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(globalSearch)) {
            //queryStringQuery alone itself does not support search with space. Searching for all keywords split by space
            if (StringUtils.contains(globalSearch, " ")) {
                String[] arr = globalSearch.split(" ");
                for (String key : arr) {
                    boolQueryBuilder.must(m -> m.queryString(qs -> qs.query("*" + key + "*")));
                }
            } else {
                boolQueryBuilder.must(m -> m.queryString(qs -> qs.query(globalSearch + "*")));
            }
        }
        return boolQueryBuilder.build()._toQuery();
    }


    private Query queryBuilderForFieldSearch(String fieldName, Object search) {
        if (search instanceof String searchValue) {
            if (StringUtils.isNotBlank(searchValue)) {
                return Query.of(q -> q.regexp(r -> r.field(fieldName).value(searchValue + ".*")));
            }
        } else if (search instanceof Number) {
            return Query.of(q -> q.term(t -> t.field(fieldName).value((Long) search)));
        } else if (search instanceof Enum<?> enumValue) {
        return Query.of(q -> q.term(t -> t.field(fieldName).value(enumValue.name())));
        }
        // for date "from" - "to"
        else if (search instanceof Map<?, ?> rangeMap) {
            if (rangeMap.containsKey("from") || rangeMap.containsKey("to")) {
                RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder().field(fieldName);
                if (rangeMap.get("from") != null && StringUtils.isNotBlank(rangeMap.get("from").toString())) {
                    rangeQueryBuilder.from((rangeMap.get("from").toString()));
                }
                if (rangeMap.get("to") != null && StringUtils.isNotBlank(rangeMap.get("to").toString())) {
                    rangeQueryBuilder.to((rangeMap.get("to").toString()));
                }
                rangeQueryBuilder.format("yyyy-MM-dd");
                log.info("Added date range filter for field {}", fieldName);
                return Query.of(q -> q.range(rangeQueryBuilder.build()));
            }
        }
        return null;
    }


    private <T> List<T> extractResults(SearchResponse<JsonData> searchResponse, Class<T> entityClass) {

        List<?> content = searchResponse.hits().hits().stream()
                .map(hit -> {
                    Map<String, Object> map = hit.source().to(Map.class);
                    map.remove("_class");
                    return map;
                })
                .collect(Collectors.toList());

        return content.stream()
                .map(obj -> objectMapper.convertValue(obj, entityClass))
                .collect(Collectors.toList());
    }

}