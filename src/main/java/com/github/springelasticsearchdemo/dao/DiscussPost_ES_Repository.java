package com.github.springelasticsearchdemo.dao;

import com.github.springelasticsearchdemo.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPost_ES_Repository extends ElasticsearchRepository<DiscussPost, Integer> {
}
