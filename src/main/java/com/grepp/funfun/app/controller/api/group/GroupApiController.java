package com.grepp.funfun.app.controller.api.group;

import com.grepp.funfun.app.controller.api.group.payload.GroupRequest;
import com.grepp.funfun.app.model.group.dto.GroupDTO;
import com.grepp.funfun.app.model.group.service.GroupService;
import com.grepp.funfun.infra.response.ApiResponse;
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.util.ReferencedException;
import com.grepp.funfun.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/groups", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GroupApiController {

    private final GroupService groupService;

    public GroupApiController(final GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        return ResponseEntity.ok(groupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getGroup(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(groupService.get(id));
    }

    // 모임 신청
    @PostMapping("/apply/{groupId}")
    @Operation(summary = "모임 신청", description = "그룹 ID 와 사용자 Email 을 통해 모임 신청.")
    public ResponseEntity<ApiResponse<String>> applyGroup(
        @PathVariable Long groupId,
        Authentication authentication) {

        log.info("모임 신청 요청 - groupId: {}, user: {}", groupId, authentication.getName());

        try{
            String userEmail = authentication.getName();
            groupService.apply(groupId, userEmail);
            return ResponseEntity.ok(ApiResponse.success("모임 신청 완료되었습니다."));
        }catch(Exception e){
            log.error("모임 신청 실패: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }
    }

    // 모임 생성
    @PostMapping("/create")
    @Operation(summary = "모임 생성", description = "모임을 생성합니다.")
    public ResponseEntity<ApiResponse<String>> createGroup(@RequestBody @Valid GroupRequest request,
        Authentication authentication){
        try{
            String leaderEmail = authentication.getName();
            groupService.create(leaderEmail,request);

            return ResponseEntity.ok(ApiResponse.success("모임이 성공적으로 생성되었습니다."));
        }catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGroup(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GroupDTO groupDTO) {
        groupService.update(id, groupDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = groupService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
