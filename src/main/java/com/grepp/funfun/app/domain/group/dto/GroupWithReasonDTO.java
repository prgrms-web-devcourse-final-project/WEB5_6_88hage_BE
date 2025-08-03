package com.grepp.funfun.app.domain.group.dto;

import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupWithReasonDTO {

    private Long id;
    private String title;
    private String simpleExplain;
    private String placeName;
    private LocalDateTime groupDate;
    private String imageUrl;
    private String leader;
    private String reason;
}
