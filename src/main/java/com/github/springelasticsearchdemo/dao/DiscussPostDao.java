package com.github.springelasticsearchdemo.dao;

import com.github.springelasticsearchdemo.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscussPostDao {

    DiscussPost findDiscussPostById(Integer id);

    List<DiscussPost> findAll();
}
