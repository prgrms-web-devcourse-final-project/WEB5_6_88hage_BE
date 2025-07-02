package com.grepp.funfun.app.controller.api.user;

import com.grepp.funfun.app.model.user.dto.UserDTO;
import com.grepp.funfun.app.model.user.service.UserService;
import com.grepp.funfun.util.ReferencedException;
import com.grepp.funfun.util.ReferencedWarning;
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
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserApiController {

    private final UserService userService;

    public UserApiController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "email") final String email) {
        return ResponseEntity.ok(userService.get(email));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createUser(@RequestBody @Valid final UserDTO userDTO) {
        final String createdEmail = userService.create(userDTO);
        return new ResponseEntity<>('"' + createdEmail + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{email}")
    public ResponseEntity<String> updateUser(@PathVariable(name = "email") final String email,
            @RequestBody @Valid final UserDTO userDTO) {
        userService.update(email, userDTO);
        return ResponseEntity.ok('"' + email + '"');
    }

    @DeleteMapping("/{email}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "email") final String email) {
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(email);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        userService.delete(email);
        return ResponseEntity.noContent().build();
    }

}
