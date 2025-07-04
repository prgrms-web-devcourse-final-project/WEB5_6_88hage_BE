package com.grepp.funfun.app.controller.api.participant;

import com.grepp.funfun.app.model.participant.dto.ParticipantDTO;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.participant.service.ParticipantService;
import com.grepp.funfun.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping(value = "/api/participants", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParticipantApiController {

    private final ParticipantService participantService;

    public ParticipantApiController(final ParticipantService participantService) {
        this.participantService = participantService;
    }

    // PENDING MEMBER 사용자 데려오기
    @GetMapping("/pending/{groupId}")
    @Operation(summary = "모임 신청 사용자 조회", description = "특정 GROUP 의 TRUE/PENDING 상태의 사용자 조회")
    public ResponseEntity<ApiResponse<List<ParticipantDTO>>> checkParticipant(@PathVariable Long groupId) {
        List<ParticipantDTO> participants = participantService.getPendingParticipants(groupId);
        return ResponseEntity.ok(ApiResponse.success(participants));
    }

    // 참여 승인
    @PostMapping("/approve/{groupId}")
    @Operation(summary = "모임 참여 승인", description = "userEmails 로 여러 대상의 사용자를 받아서 승인 처리")
    public ResponseEntity<ApiResponse<String>> approveParticipant(@PathVariable Long groupId,
        @RequestBody List<String> userEmails,
        Authentication authentication) {

        participantService.approveParticipant(groupId, userEmails, authentication.getName());

        return ResponseEntity.ok(ApiResponse.success("승인"));
    }

    @GetMapping
    public ResponseEntity<List<ParticipantDTO>> getAllParticipants() {
        return ResponseEntity.ok(participantService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantDTO> getParticipant(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(participantService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createParticipant(
            @RequestBody @Valid final ParticipantDTO participantDTO) {
        final Long createdId = participantService.create(participantDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateParticipant(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ParticipantDTO participantDTO) {
        participantService.update(id, participantDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable(name = "id") final Long id) {
        participantService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
