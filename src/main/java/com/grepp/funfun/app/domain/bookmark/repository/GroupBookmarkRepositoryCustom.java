package com.grepp.funfun.app.domain.bookmark.repository;

import com.grepp.funfun.app.domain.bookmark.entity.GroupBookmark;
import java.util.List;

public interface GroupBookmarkRepositoryCustom {
    List<GroupBookmark> getMyGroupBookMarks(String userEmail);

}
