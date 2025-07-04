package com.grepp.funfun.app.controller.api.group.payload;

import com.grepp.funfun.app.model.group.code.GroupClassification;
import com.grepp.funfun.app.model.group.code.GroupStatus;
import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.user.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class GroupRequest {

    //모임 이름
    @NotBlank(message = "제목은 필수로 입력해야 합니다.")
    private String title;
    //모임 설명
    @NotBlank(message = "설명은 필수로 입력해야 합니다.")
    private String explain;
    //모임 장소
    @NotBlank(message="모임 장소는 필수입니다.")
    private String placeName;

    @NotNull(message="모임 시간은 필수입니다.")
    private LocalDateTime groupDate;

    //모임 주소
    @NotBlank(message="모임 주소는 필수입니다.")
    private String address;

    //모임 카테고리
    @NotNull(message="모임 카테고리는 필수입니다.")
    private GroupClassification category;
    //최대 인원
    @Min(value = 2, message = "최소 2명 이상이어야 합니다.")
    @Max(value = 10, message = "최대 10명까지 가능합니다.")
    private Integer maxPeople;
    //위도
    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;
    //경도
    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    @NotEmpty(message="해시태그는 1개 이상 선택해야합니다.")
    private List<String> hashTags;

    @Min(value = 1, message = "최소 1시간 이상이어야 합니다.")  // message 추가
    private Integer during;

    public Group toEntity(User leader){
        return Group.builder()
            .leader(leader)
            .title(this.title)
            .explain(this.explain)
            .placeName(this.placeName)
            .groupDate(this.groupDate)
            .address(this.address)
            .category(this.category)
            .maxPeople(this.maxPeople)
            .nowPeople(1)
            .status(GroupStatus.RECRUITING)
            .latitude(this.latitude)
            .longitude(this.longitude)
            .during(this.during)
            .build();
    }

}
