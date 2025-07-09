package com.grepp.funfun.app.domain.auth.token;

import com.grepp.funfun.app.domain.auth.token.entity.UserBlackList;
import org.springframework.data.repository.CrudRepository;

public interface UserBlackListRepository extends CrudRepository<UserBlackList, String> {
}
