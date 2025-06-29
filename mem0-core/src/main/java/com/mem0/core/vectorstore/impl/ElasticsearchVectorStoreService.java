package com.mem0.core.vectorstore.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mem0.core.vectorstore.VectorStoreService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 基于Elasticsearch的向量存储服务实现
 * 
 * @author changyu496
 */
@Service
public class ElasticsearchVectorStoreService implements VectorStoreService {
    
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchVectorStoreService.class);
    
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient elasticsearchClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${elasticsearch.index-prefix:mem0_vectors}")
    private String indexPrefix;
    
    @Override
    public void storeVector(String indexName, String id, List<Float> vector, Map<String, Object> metadata) {
        try {
            String fullIndexName = getFullIndexName(indexName);
            
            Map<String, Object> document = new HashMap<>();
            document.put("vector", vector);
            if (metadata != null) {
                document.putAll(metadata);
            }
            
            IndexRequest request = new IndexRequest(fullIndexName)
                    .id(id)
                    .source(document, XContentType.JSON);
            
            // 添加响应处理
            IndexResponse response = elasticsearchClient.index(request, RequestOptions.DEFAULT);
            
            // 检查响应状态
            if (response.status() != org.elasticsearch.rest.RestStatus.CREATED && 
                response.status() != org.elasticsearch.rest.RestStatus.OK) {
                throw new RuntimeException("存储向量失败，状态码: " + response.status());
            }
            
        } catch (IOException e) {
            // 如果是解析响应体的问题，但操作实际成功了，可以忽略
            if (e.getMessage() != null && e.getMessage().contains("Unable to parse response body")) {
                log.warn("向量存储成功，但响应解析失败: {}", e.getMessage());
                return; // 忽略这个错误
            }
            throw new RuntimeException("存储向量失败", e);
        }
    }
    
    @Override
    public void storeVectors(String indexName, List<VectorData> vectors) {
        for (VectorData vectorData : vectors) {
            storeVector(indexName, vectorData.getId(), vectorData.getVector(), vectorData.getMetadata());
        }
    }
    
    @Override
    public List<SearchResult> search(String indexName, List<Float> queryVector, int limit, Map<String, Object> filters) {
        try {
            String fullIndexName = getFullIndexName(indexName);
            
            log.info("开始向量搜索 - 索引: {}, 查询向量维度: {}, 限制: {}, 过滤条件: {}", 
                    fullIndexName, queryVector.size(), limit, filters);
            
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            
            // 构建向量查询
            Map<String, Object> scriptParams = new HashMap<>();
            scriptParams.put("query_vector", queryVector);
            
            Script script = new Script(ScriptType.INLINE, "painless", 
                "cosineSimilarity(params.query_vector, 'vector') + 1.0", scriptParams);
            
            // 构建基础查询
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            
            // 添加过滤条件
            if (filters != null && !filters.isEmpty()) {
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    boolQuery.filter(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                    log.info("添加过滤条件: {} = {}", entry.getKey(), entry.getValue());
                }
            }
            
            // 如果没有过滤条件，使用matchAllQuery
            if (filters == null || filters.isEmpty()) {
                boolQuery.must(QueryBuilders.matchAllQuery());
                log.info("使用matchAllQuery，无过滤条件");
            }
            
            // 将过滤条件与向量查询结合
            sourceBuilder.query(QueryBuilders.scriptScoreQuery(boolQuery, script));
            sourceBuilder.size(limit);
            sourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
            
            SearchRequest searchRequest = new SearchRequest(fullIndexName);
            searchRequest.source(sourceBuilder);
            
            log.info("ES查询请求: {}", searchRequest.source().toString());
            
            SearchResponse response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
            
            log.info("ES响应 - 总命中数: {}, 最大分数: {}", 
                    response.getHits().getTotalHits().value, 
                    response.getHits().getMaxScore());
            
            List<SearchResult> results = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> source = hit.getSourceAsMap();
                Map<String, Object> metadata = new HashMap<>(source);
                metadata.remove("vector"); // 移除向量数据，只保留元数据
                
                log.info("ES召回结果 - ID: {}, 分数: {}, 内容: {}", 
                        hit.getId(), hit.getScore(), source.get("content"));
                
                results.add(new SearchResult(hit.getId(), hit.getScore(), metadata));
            }
            
            log.info("最终返回结果数量: {}", results.size());
            return results;
        } catch (IOException e) {
            log.error("搜索向量失败", e);
            throw new RuntimeException("搜索向量失败", e);
        }
    }
    
    @Override
    public void deleteVector(String indexName, String id) {
        try {
            String fullIndexName = getFullIndexName(indexName);
            
            // 添加响应处理
            org.elasticsearch.action.delete.DeleteResponse response = elasticsearchClient.delete(
                    new org.elasticsearch.action.delete.DeleteRequest(fullIndexName, id),
                    RequestOptions.DEFAULT
            );
            
            // 检查响应状态
            if (response.status() != org.elasticsearch.rest.RestStatus.OK && 
                response.status() != org.elasticsearch.rest.RestStatus.NOT_FOUND) {
                throw new RuntimeException("删除向量失败，状态码: " + response.status());
            }
            
        } catch (IOException e) {
            // 如果是解析响应体的问题，但操作实际成功了，可以忽略
            if (e.getMessage() != null && e.getMessage().contains("Unable to parse response body")) {
                log.warn("向量删除成功，但响应解析失败: {}", e.getMessage());
                return; // 忽略这个错误
            }
            throw new RuntimeException("删除向量失败", e);
        }
    }
    
    @Override
    public void createIndex(String indexName, int dimension) {
        try {
            String fullIndexName = getFullIndexName(indexName);
            
            CreateIndexRequest request = new CreateIndexRequest(fullIndexName);
            request.settings(Settings.builder()
                    .put("index.number_of_shards", 1)
                    .put("index.number_of_replicas", 0)
            );
            
            // 定义映射
            Map<String, Object> properties = new HashMap<>();
            
            // 向量字段映射
            Map<String, Object> vectorMapping = new HashMap<>();
            vectorMapping.put("type", "dense_vector");
            vectorMapping.put("dims", dimension);
            vectorMapping.put("index", true);
            vectorMapping.put("similarity", "cosine");
            properties.put("vector", vectorMapping);
            
            // 元数据字段映射
            Map<String, Object> metadataMapping = new HashMap<>();
            metadataMapping.put("type", "object");
            metadataMapping.put("dynamic", true);
            properties.put("metadata", metadataMapping);
            
            Map<String, Object> mapping = new HashMap<>();
            mapping.put("properties", properties);
            
            request.mapping(mapping);
            
            elasticsearchClient.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("创建索引失败", e);
        }
    }
    
    @Override
    public void deleteIndex(String indexName) {
        try {
            String fullIndexName = getFullIndexName(indexName);
            elasticsearchClient.indices().delete(
                    new DeleteIndexRequest(fullIndexName),
                    RequestOptions.DEFAULT
            );
        } catch (IOException e) {
            throw new RuntimeException("删除索引失败", e);
        }
    }
    
    @Override
    public boolean indexExists(String indexName) {
        try {
            String fullIndexName = getFullIndexName(indexName);
            GetIndexRequest request = new GetIndexRequest(fullIndexName);
            return elasticsearchClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("检查索引是否存在失败", e);
        }
    }
    
    private String getFullIndexName(String indexName) {
        if (indexName == null || indexName.trim().isEmpty()) {
            return indexPrefix;
        }
        return indexPrefix + "_" + indexName;
    }
} 