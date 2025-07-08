package com.grepp.funfun.app.controller.api.participant;

import com.grepp.funfun.app.controller.api.participant.payload.ParticipantResponse;
import com.grepp.funfun.app.model.participant.dto.ParticipantDTO;
import com.grepp.funfun.app.model.participant.service.ParticipantService;
import com.grepp.funfun.infra.response.ApiResponse;
import com.grepp.funfun.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@Slf4j
@RequestMapping(value = "/api/participants", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParticipantApiController {

    private final ParticipantService participantService;

    public ParticipantApiController(final ParticipantService participantService) {
        this.participantService = participantService;
    }

    // PENDING MEMBER 사용자 데려오기
    @GetMapping("/{groupId}/pending")
    @Operation(summary = "모임 신청 사용자 조회", description = "특정 GROUP 의 TRUE/PENDING 상태의 사용자 조회")
    public ResponseEntity<ApiResponse<List<ParticipantResponse>>> pendingParticipant(
        @PathVariable Long groupId) {
        List<ParticipantResponse> participants = participantService.getPendingParticipants(groupId);
        return ResponseEntity.ok(ApiResponse.success(participants));
    }

    // APPROVE MEMBER 사용자 데려오기
    @GetMapping("/{groupId}/approve")
    @Operation(summary = "모임 승인 사용자 조회", description = "특정 GROUP 의 TRUE/APPROVE 상태의 사용자 조회")
    public ResponseEntity<ApiResponse<List<ParticipantResponse>>> approveParticipant(
        @PathVariable Long groupId) {
        List<ParticipantResponse> participants = participantService.getApproveParticipants(groupId);
        return ResponseEntity.ok(ApiResponse.success(participants));

    }

    // 참여 신청
    @PostMapping("/{groupId}/apply")
    @Operation(summary = "모임 신청", description = "그룹 ID 와 사용자 Email 을 통해 모임 신청.")
    public ResponseEntity<ApiResponse<String>> applyGroup(
        @PathVariable Long groupId,
        Authentication authentication) {

        log.info("모임 신청 요청 - groupId: {}, user: {}", groupId, authentication.getName());

        try {
            String userEmail = authentication.getName();
            participantService.apply(groupId, userEmail);
            return ResponseEntity.ok(ApiResponse.success("모임 신청 완료되었습니다."));
        } catch (Exception e) {
            log.error("모임 신청 실패: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }
    }

    // 참여 승인
    @PostMapping("/{groupId}/approve")
    @Operation(summary = "모임 참여 승인", description = "userEmails 로 여러 대상의 사용자를 받아서 승인 처리")
    public ResponseEntity<ApiResponse<String>> approveParticipant(@PathVariable Long groupId,
        @RequestBody List<String> userEmails,
        Authentication authentication) {

        try {
            String leaderEmail = authentication.getName();

            participantService.approveParticipant(groupId, userEmails, leaderEmail);

            return ResponseEntity.ok(ApiResponse.success("모임 승인 완료되었습니다."));
        } catch (Exception e) {
            log.error("모임 승인 실패: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }

    }

    // 참여 거절
    @PostMapping("/{groupId}/reject")
    @Operation(summary = "모임 참여 거절", description = "userEmails 로 여러 대상의 사용자를 받아서 승인 거절")
    public ResponseEntity<ApiResponse<String>> rejectParticipant(@PathVariable Long groupId,
        @RequestBody List<String> userEmails,
        Authentication authentication) {

        try {
            String leaderEmail = authentication.getName();

            participantService.rejectParticipant(groupId, userEmails, leaderEmail);

            return ResponseEntity.ok(ApiResponse.success("모임 거절 완료되었습니다."));
        } catch (Exception e) {
            log.error("모임 거절 실패: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }

    }

    // 참여자 강퇴
    @PostMapping("/{groupId}/{userEmail}/kickout")
    @Operation(summary = "모임 참여자 강퇴", description = "모임 참여자를 강제퇴장")
    public ResponseEntity<ApiResponse<String>> kickoutParticipant(@PathVariable Long groupId,
        @PathVariable String userEmail,
        Authentication authentication) {

        try {
            String leaderEmail = authentication.getName();

            participantService.kickOut(groupId, userEmail, leaderEmail);

            return ResponseEntity.ok(ApiResponse.success("모임 강제퇴장이 완료되었습니다."));
        } catch (Exception e) {
            log.error("모임 강제퇴장 실패: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }

    }

    // 모임 나가기
    @PostMapping("/{groupId}/leave")
    @Operation(summary = "모임 나가기", description = "모임에서 나가기")
    public ResponseEntity<ApiResponse<String>> leaveParticipant(@PathVariable Long groupId,
        Authentication authentication) {

        try {
            String userEmail = authentication.getName();
            participantService.leave(groupId, userEmail);

            return ResponseEntity.ok(ApiResponse.success("모임 나가기가 완료되었습니다."));
        } catch (Exception e) {
            log.error("모임 나가기 실패: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }

    }

    @GetMapping
    public ResponseEntity<List<ParticipantDTO>> getAllParticipants() {
        return ResponseEntity.ok(participantService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantDTO> getParticipant(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(participantService.get(id));
    }

}
