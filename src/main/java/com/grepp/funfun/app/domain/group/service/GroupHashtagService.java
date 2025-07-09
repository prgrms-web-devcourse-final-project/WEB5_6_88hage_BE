package com.grepp.funfun.app.domain.group.service;

import com.grepp.funfun.app.domain.group.dto.GroupHashtagDTO;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.entity.GroupHashtag;
import com.grepp.funfun.app.domain.group.repository.GroupHashtagRepository;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GroupHashtagService {

    private final GroupHashtagRepository groupHashtagRepository;
    private final GroupRepository groupRepository;

    public GroupHashtagService(final GroupHashtagRepository groupHashtagRepository,
            final GroupRepository groupRepository) {
        this.groupHashtagRepository = groupHashtagRepository;
        this.groupRepository = groupRepository;
    }

    public List<GroupHashtagDTO> findAll() {
        final List<GroupHashtag> groupHashtags = groupHashtagRepository.findAll(Sort.by("id"));
        return groupHashtags.stream()
                .map(groupHashtag -> mapToDTO(groupHashtag, new GroupHashtagDTO()))
                .toList();
    }

    public GroupHashtagDTO get(final Long id) {
        return groupHashtagRepository.findById(id)
                .map(groupHashtag -> mapToDTO(groupHashtag, new GroupHashtagDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final GroupHashtagDTO groupHashtagDTO) {
        final GroupHashtag groupHashtag = new GroupHashtag();
        mapToEntity(groupHashtagDTO, groupHashtag);
        return groupHashtagRepository.save(groupHashtag).getId();
    }

    public void update(final Long id, final GroupHashtagDTO groupHashtagDTO) {
        final GroupHashtag groupHashtag = groupHashtagRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(groupHashtagDTO, groupHashtag);
        groupHashtagRepository.save(groupHashtag);
    }

    public void delete(final Long id) {
        groupHashtagRepository.deleteById(id);
    }

    private GroupHashtagDTO mapToDTO(final GroupHashtag groupHashtag,
            final GroupHashtagDTO groupHashtagDTO) {
        groupHashtagDTO.setId(groupHashtag.getId());
        groupHashtagDTO.setTag(groupHashtag.getTag());
        groupHashtagDTO.setGroup(groupHashtag.getGroup() == null ? null : groupHashtag.getGroup().getId());
        return groupHashtagDTO;
    }

    private GroupHashtag mapToEntity(final GroupHashtagDTO groupHashtagDTO,
            final GroupHashtag groupHashtag) {
        groupHashtag.setTag(groupHashtagDTO.getTag());
        final Group group = groupHashtagDTO.getGroup() == null ? null : groupRepository.findById(groupHashtagDTO.getGroup())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        groupHashtag.setGroup(group);
        return groupHashtag;
    }

}
