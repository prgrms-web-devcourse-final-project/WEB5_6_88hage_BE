package com.grepp.funfun.app.domain.group.repository;

import com.grepp.funfun.app.domain.group.document.GroupDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GroupDocumentRepository extends ElasticsearchRepository<GroupDocument, String> {

}
