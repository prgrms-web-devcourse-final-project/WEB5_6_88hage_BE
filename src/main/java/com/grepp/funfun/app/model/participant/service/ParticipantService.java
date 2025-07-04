package com.grepp.funfun.app.model.participant.service;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.app.model.participant.code.ParticipantStatus;
import com.grepp.funfun.app.model.participant.dto.ParticipantDTO;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    //모임 승인
    @Transactional
    public void approveParticipant(Long groupId, List<String> userEmails, String leaderEmail){
        Group group = groupRepository.findById(groupId)
            .orElseThrow(()-> new CommonException(ResponseCode.NOT_FOUND));

        if(!group.getLeader().getEmail().equals(leaderEmail)){
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }

        // 최대 인원 체크
        int availableSpots = group.getMaxPeople() - group.getNowPeople();
        if(userEmails.size() > availableSpots) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        // 승인
        for(String userEmail : userEmails) {
            Participant participant = participantRepository.findByGroupIdAndUserEmail(groupId,userEmail);
            log.info(participant.toString());
            participant.setStatus(ParticipantStatus.APPROVED);
        }
        // 인원 수 변경
        group.setNowPeople(group.getNowPeople() + userEmails.size());
    }

    // 모임 신청한 사용자 조회
    @Transactional(readOnly = true)
    public List<ParticipantDTO> getPendingParticipants(Long groupId) {
        if(!groupRepository.existsById(groupId)) {
            throw new CommonException(ResponseCode.NOT_FOUND);
        }
        List<Participant> participants = participantRepository.findPendingMembers(groupId);

        return participants.stream()
            .map(participant -> mapToDTO(participant, new ParticipantDTO()))
            .collect(Collectors.toList());
    }

    public List<ParticipantDTO> findAll() {
        final List<Participant> participants = participantRepository.findAll(Sort.by("id"));
        return participants.stream()
                .map(participant -> mapToDTO(participant, new ParticipantDTO()))
                .toList();
    }

    public ParticipantDTO get(final Long id) {
        return participantRepository.findById(id)
                .map(participant -> mapToDTO(participant, new ParticipantDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ParticipantDTO participantDTO) {
        final Participant participant = new Participant();
        mapToEntity(participantDTO, participant);
        return participantRepository.save(participant).getId();
    }

    public void update(final Long id, final ParticipantDTO participantDTO) {
        final Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(participantDTO, participant);
        participantRepository.save(participant);
    }

    public void delete(final Long id) {
        participantRepository.deleteById(id);
    }

    private ParticipantDTO mapToDTO(final Participant participant,
            final ParticipantDTO participantDTO) {
        participantDTO.setId(participant.getId());
        participantDTO.setRole(participant.getRole());
        participantDTO.setStatus(participant.getStatus());
        participantDTO.setUser(participant.getUser() == null ? null : participant.getUser().getEmail());
        participantDTO.setGroup(participant.getGroup() == null ? null : participant.getGroup().getId());
        return participantDTO;
    }

    private Participant mapToEntity(final ParticipantDTO participantDTO,
            final Participant participant) {
        participant.setRole(participantDTO.getRole());
        participant.setStatus(participantDTO.getStatus());
        final User user = participantDTO.getUser() == null ? null : userRepository.findById(participantDTO.getUser())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        participant.setUser(user);
        final Group group = participantDTO.getGroup() == null ? null : groupRepository.findById(participantDTO.getGroup())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        participant.setGroup(group);
        return participant;
    }

}
