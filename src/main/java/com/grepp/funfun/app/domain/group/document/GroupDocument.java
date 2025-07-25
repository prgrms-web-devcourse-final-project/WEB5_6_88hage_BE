package com.grepp.funfun.app.domain.group.document;

import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "groups")
@Setting(settingPath = "/elasticsearch/group-settings.json")
@AllArgsConstructor
@Getter
public class GroupDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "groups_title_analyzer")
    private String title;

    @Field(type = FieldType.Keyword)
    private String explain;

    @Field(type = FieldType.Text, analyzer = "groups_simpleExplain_analyzer")
    private String simpleExplain;

    @Field(type = FieldType.Keyword)
    private String imageUrl;

    @Field(type = FieldType.Keyword)
    private String placeName;

    @Field(type = FieldType.Keyword)
    private String address;

    @Field(type = FieldType.Integer)
    private Integer viewCount;

    @Field(type = FieldType.Date)
    private String groupDate;

    @Field(type = FieldType.Date)
    private String createdAt;

    @Field(type = FieldType.Integer)
    private Integer maxPeople;

    @Field(type = FieldType.Integer)
    private Integer nowPeople;

    @Field(type = FieldType.Keyword)
    private GroupStatus status;

    @Field(type = FieldType.Double)
    private Double latitude;

    @Field(type = FieldType.Double)
    private Double longitude;

    @Field(type = FieldType.Integer)
    private Integer during;

    @Field(type = FieldType.Keyword)
    private GroupClassification category;

    @Field(type = FieldType.Boolean)
    private Boolean activated;
}
