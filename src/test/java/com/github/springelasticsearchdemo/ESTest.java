package com.github.springelasticsearchdemo;

import com.github.springelasticsearchdemo.dao.DiscussPostDao;
import com.github.springelasticsearchdemo.dao.DiscussPost_ES_Repository;
import com.github.springelasticsearchdemo.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringElasticsearchDemoApplication.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private DiscussPost_ES_Repository repository;

    @Autowired
    private DiscussPostDao discussPostDao;

    @Test
    public void testInsert() {
        repository.save(discussPostDao.findDiscussPostById(241));
    }

    @Test
    public void testInsertList() {
        repository.saveAll(discussPostDao.findAll());
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussPostDao.findDiscussPostById(231);
        System.out.println(post.getTitle());
        System.out.println(post.getContent());
        post.setContent("update!!!");
        repository.save(post);
    }

    @Test
    public void testDelete() {
        repository.deleteById(231);
    }

    @Test
    public void testDeleteAll() {
        repository.deleteAll();
    }

    @Test
    public void testSearchByRepository() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        Page<DiscussPost> resultPage = repository.search(searchQuery);
        System.out.println(resultPage.getTotalElements());
        System.out.println(resultPage.getTotalPages());
        System.out.println(resultPage.getNumber());
        System.out.println(resultPage.getSize());
        for (DiscussPost post : resultPage) {
            System.out.println(post);
        }
    }

    @Test
    public void testSearchByTemplate() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        AggregatedPage<DiscussPost> resultPage = elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                SearchHits hits = response.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }
                List<DiscussPost> list = new ArrayList<>();
                for (SearchHit hit : hits) {
                    DiscussPost post = DiscussPost.builder()
                            .id(Integer.valueOf(hit.getSourceAsMap().get("id").toString()))
                            .userId(Integer.valueOf(hit.getSourceAsMap().get("userId").toString()))
                            .title(hit.getSourceAsMap().get("title").toString())
                            .content(hit.getSourceAsMap().get("content").toString())
                            .status(Integer.valueOf(hit.getSourceAsMap().get("status").toString()))
                            .createTime(new Date(Long.valueOf(hit.getSourceAsMap().get("createTime").toString())))
                            .commentCount(Integer.valueOf(hit.getSourceAsMap().get("commentCount").toString()))
                            .build();

                    // 处理高亮显示的结果
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        titleField.getFragments()[0].toString();
                        post.setTitle(titleField.getFragments()[0].toString());
                    }
                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        post.setContent(contentField.getFragments()[0].toString());
                    }
                    list.add(post);
                }

                return new AggregatedPageImpl(list,
                        pageable,
                        hits.getTotalHits(),
                        response.getAggregations(),
                        response.getScrollId(),
                        hits.getMaxScore());
            }
        });

        System.out.println(resultPage.getTotalElements());
        System.out.println(resultPage.getTotalPages());
        System.out.println(resultPage.getNumber());
        System.out.println(resultPage.getSize());
        for (DiscussPost post : resultPage) {
            System.out.println(post);
        }
    }
}
