package com.grepp.funfun.app.model.bookmark.service;

import com.grepp.funfun.app.model.bookmark.dto.GroupBookmarkDTO;
import com.grepp.funfun.app.model.bookmark.entity.GroupBookmark;
import com.grepp.funfun.app.model.bookmark.repository.GroupBookmarkRepository;
import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GroupBookmarkService {

    private final GroupBookmarkRepository groupBookmarkRepository;
    private final GroupRepository groupRepository;

    public GroupBookmarkService(final GroupBookmarkRepository groupBookmarkRepository,
            final GroupRepository groupRepository) {
        this.groupBookmarkRepository = groupBookmarkRepository;
        this.groupRepository = groupRepository;
    }

    public List<GroupBookmarkDTO> findAll() {
        final List<GroupBookmark> groupBookmarks = groupBookmarkRepository.findAll(Sort.by("id"));
        return groupBookmarks.stream()
                .map(groupBookmark -> mapToDTO(groupBookmark, new GroupBookmarkDTO()))
                .toList();
    }

    public GroupBookmarkDTO get(final Long id) {
        return groupBookmarkRepository.findById(id)
                .map(groupBookmark -> mapToDTO(groupBookmark, new GroupBookmarkDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final GroupBookmarkDTO groupBookmarkDTO) {
        final GroupBookmark groupBookmark = new GroupBookmark();
        mapToEntity(groupBookmarkDTO, groupBookmark);
        return groupBookmarkRepository.save(groupBookmark).getId();
    }

    public void update(final Long id, final GroupBookmarkDTO groupBookmarkDTO) {
        final GroupBookmark groupBookmark = groupBookmarkRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(groupBookmarkDTO, groupBookmark);
        groupBookmarkRepository.save(groupBookmark);
    }

    public void delete(final Long id) {
        groupBookmarkRepository.deleteById(id);
    }

    private GroupBookmarkDTO mapToDTO(final GroupBookmark groupBookmark,
            final GroupBookmarkDTO groupBookmarkDTO) {
        groupBookmarkDTO.setId(groupBookmark.getId());
        groupBookmarkDTO.setEmail(groupBookmark.getEmail());
        groupBookmarkDTO.setGroup(groupBookmark.getGroup() == null ? null : groupBookmark.getGroup().getId());
        return groupBookmarkDTO;
    }

    private GroupBookmark mapToEntity(final GroupBookmarkDTO groupBookmarkDTO,
            final GroupBookmark groupBookmark) {
        groupBookmark.setEmail(groupBookmarkDTO.getEmail());
        final Group group = groupBookmarkDTO.getGroup() == null ? null : groupRepository.findById(groupBookmarkDTO.getGroup())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        groupBookmark.setGroup(group);
        return groupBookmark;
    }

}
