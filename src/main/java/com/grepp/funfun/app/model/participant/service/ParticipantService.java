package com.grepp.funfun.app.model.participant.service;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.app.model.participant.dto.ParticipantDTO;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ParticipantService(final ParticipantRepository participantRepository,
            final UserRepository userRepository, final GroupRepository groupRepository) {
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
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
