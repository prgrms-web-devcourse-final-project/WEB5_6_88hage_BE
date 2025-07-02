package com.grepp.funfun.app.model.group.service;

import com.grepp.funfun.app.model.group.dto.GroupHashTagDTO;
import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.entity.GroupHashTag;
import com.grepp.funfun.app.model.group.repository.GroupHashTagRepository;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GroupHashTagService {

    private final GroupHashTagRepository groupHashTagRepository;
    private final GroupRepository groupRepository;

    public GroupHashTagService(final GroupHashTagRepository groupHashTagRepository,
            final GroupRepository groupRepository) {
        this.groupHashTagRepository = groupHashTagRepository;
        this.groupRepository = groupRepository;
    }

    public List<GroupHashTagDTO> findAll() {
        final List<GroupHashTag> groupHashTags = groupHashTagRepository.findAll(Sort.by("id"));
        return groupHashTags.stream()
                .map(groupHashTag -> mapToDTO(groupHashTag, new GroupHashTagDTO()))
                .toList();
    }

    public GroupHashTagDTO get(final Long id) {
        return groupHashTagRepository.findById(id)
                .map(groupHashTag -> mapToDTO(groupHashTag, new GroupHashTagDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final GroupHashTagDTO groupHashTagDTO) {
        final GroupHashTag groupHashTag = new GroupHashTag();
        mapToEntity(groupHashTagDTO, groupHashTag);
        return groupHashTagRepository.save(groupHashTag).getId();
    }

    public void update(final Long id, final GroupHashTagDTO groupHashTagDTO) {
        final GroupHashTag groupHashTag = groupHashTagRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(groupHashTagDTO, groupHashTag);
        groupHashTagRepository.save(groupHashTag);
    }

    public void delete(final Long id) {
        groupHashTagRepository.deleteById(id);
    }

    private GroupHashTagDTO mapToDTO(final GroupHashTag groupHashTag,
            final GroupHashTagDTO groupHashTagDTO) {
        groupHashTagDTO.setId(groupHashTag.getId());
        groupHashTagDTO.setTag(groupHashTag.getTag());
        groupHashTagDTO.setGroup(groupHashTag.getGroup() == null ? null : groupHashTag.getGroup().getId());
        return groupHashTagDTO;
    }

    private GroupHashTag mapToEntity(final GroupHashTagDTO groupHashTagDTO,
            final GroupHashTag groupHashTag) {
        groupHashTag.setTag(groupHashTagDTO.getTag());
        final Group group = groupHashTagDTO.getGroup() == null ? null : groupRepository.findById(groupHashTagDTO.getGroup())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        groupHashTag.setGroup(group);
        return groupHashTag;
    }

}
