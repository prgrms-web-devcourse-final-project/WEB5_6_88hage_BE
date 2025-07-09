package com.grepp.funfun.app.domain.user.controller;

import com.grepp.funfun.app.domain.user.dto.UserInfoDTO;
import com.grepp.funfun.app.domain.user.service.UserInfoService;
import com.grepp.funfun.app.delete.util.ReferencedException;
import com.grepp.funfun.app.delete.util.ReferencedWarning;
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
@RequestMapping(value = "/api/userInfos", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserInfoApiController {

    private final UserInfoService userInfoService;

    public UserInfoApiController(final UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping
    public ResponseEntity<List<UserInfoDTO>> getAllUserInfos() {
        return ResponseEntity.ok(userInfoService.findAll());
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserInfoDTO> getUserInfo(
            @PathVariable(name = "email") final String email) {
        return ResponseEntity.ok(userInfoService.get(email));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createUserInfo(
            @RequestBody @Valid final UserInfoDTO userInfoDTO) {
        final String createdEmail = userInfoService.create(userInfoDTO);
        return new ResponseEntity<>('"' + createdEmail + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{email}")
    public ResponseEntity<String> updateUserInfo(@PathVariable(name = "email") final String email,
            @RequestBody @Valid final UserInfoDTO userInfoDTO) {
        userInfoService.update(email, userInfoDTO);
        return ResponseEntity.ok('"' + email + '"');
    }

    @DeleteMapping("/{email}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUserInfo(@PathVariable(name = "email") final String email) {
        final ReferencedWarning referencedWarning = userInfoService.getReferencedWarning(email);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        userInfoService.delete(email);
        return ResponseEntity.noContent().build();
    }

}
