package com.grepp.funfun.app.model.content.service;

import com.grepp.funfun.app.model.content.dto.ContentCategoryDTO;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.entity.ContentCategory;
import com.grepp.funfun.app.model.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ContentCategoryService {

    private final ContentCategoryRepository contentCategoryRepository;
    private final ContentRepository contentRepository;

    public ContentCategoryService(final ContentCategoryRepository contentCategoryRepository,
            final ContentRepository contentRepository) {
        this.contentCategoryRepository = contentCategoryRepository;
        this.contentRepository = contentRepository;
    }

    public List<ContentCategoryDTO> findAll() {
        final List<ContentCategory> contentCategories = contentCategoryRepository.findAll(Sort.by("id"));
        return contentCategories.stream()
                .map(contentCategory -> mapToDTO(contentCategory, new ContentCategoryDTO()))
                .toList();
    }

    public ContentCategoryDTO get(final Long id) {
        return contentCategoryRepository.findById(id)
                .map(contentCategory -> mapToDTO(contentCategory, new ContentCategoryDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ContentCategoryDTO contentCategoryDTO) {
        final ContentCategory contentCategory = new ContentCategory();
        mapToEntity(contentCategoryDTO, contentCategory);
        return contentCategoryRepository.save(contentCategory).getId();
    }

    public void update(final Long id, final ContentCategoryDTO contentCategoryDTO) {
        final ContentCategory contentCategory = contentCategoryRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(contentCategoryDTO, contentCategory);
        contentCategoryRepository.save(contentCategory);
    }

    public void delete(final Long id) {
        contentCategoryRepository.deleteById(id);
    }

    private ContentCategoryDTO mapToDTO(final ContentCategory contentCategory,
            final ContentCategoryDTO contentCategoryDTO) {
        contentCategoryDTO.setId(contentCategory.getId());
        contentCategoryDTO.setCategory(contentCategory.getCategory());
        contentCategoryDTO.setDuring(contentCategory.getDuring());
        return contentCategoryDTO;
    }

    private ContentCategory mapToEntity(final ContentCategoryDTO contentCategoryDTO,
            final ContentCategory contentCategory) {
        contentCategory.setCategory(contentCategoryDTO.getCategory());
        contentCategory.setDuring(contentCategoryDTO.getDuring());
        return contentCategory;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final ContentCategory contentCategory = contentCategoryRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        final Content categoryContent = contentRepository.findFirstByCategory(contentCategory);
        if (categoryContent != null) {
            referencedWarning.setKey("contentCategory.content.category.referenced");
            referencedWarning.addParam(categoryContent.getId());
            return referencedWarning;
        }
        return null;
    }

}
