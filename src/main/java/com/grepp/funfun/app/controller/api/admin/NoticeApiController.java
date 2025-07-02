package com.grepp.funfun.app.controller.api.admin;

import com.grepp.funfun.app.model.admin.dto.NoticeDTO;
import com.grepp.funfun.app.model.admin.service.NoticeService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/notices", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeApiController {

    private final NoticeService noticeService;

    public NoticeApiController(final NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public ResponseEntity<List<NoticeDTO>> getAllNotices() {
        return ResponseEntity.ok(noticeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO> getNotice(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(noticeService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createNotice(@RequestBody @Valid final NoticeDTO noticeDTO) {
        final Long createdId = noticeService.create(noticeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateNotice(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final NoticeDTO noticeDTO) {
        noticeService.update(id, noticeDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteNotice(@PathVariable(name = "id") final Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
