package com.grepp.funfun.app.controller.api.contact;

import com.grepp.funfun.app.model.contact.dto.ContactDTO;
import com.grepp.funfun.app.model.contact.service.ContactService;
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
@RequestMapping(value = "/api/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContactApiController {

    private final ContactService contactService;

    public ContactApiController(final ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        return ResponseEntity.ok(contactService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getContact(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(contactService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createContact(@RequestBody @Valid final ContactDTO contactDTO) {
        final Long createdId = contactService.create(contactDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateContact(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ContactDTO contactDTO) {
        contactService.update(id, contactDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteContact(@PathVariable(name = "id") final Long id) {
        contactService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
