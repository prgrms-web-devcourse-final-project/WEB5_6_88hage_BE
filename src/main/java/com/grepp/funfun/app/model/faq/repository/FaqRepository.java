package com.grepp.funfun.app.model.faq.repository;

import com.grepp.funfun.app.model.faq.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    List<Faq> findAllByActivatedTrue();

}
