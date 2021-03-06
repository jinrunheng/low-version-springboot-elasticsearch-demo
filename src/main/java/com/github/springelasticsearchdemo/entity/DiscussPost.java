package com.github.springelasticsearchdemo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.Date;


@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "discusspost", type = "_doc",shards = 6, replicas = 3)
public class DiscussPost {

    @Id
    private Integer id;

    @Field(type = FieldType.Integer)
    private Integer userId;

    //匹配的时候，尽可能多地去匹配，搜索的时候，尽可能有效搜索
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Integer)
    private Integer type;// 0-普通 1-置顶

    @Field(type = FieldType.Integer)
    private Integer status;// 0-正常,1-精华,2-拉黑

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Integer)
    private Integer commentCount;

    @Field(type = FieldType.Double)
    private Double score;
}