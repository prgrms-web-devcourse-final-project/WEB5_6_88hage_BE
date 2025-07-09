package com.grepp.funfun.app.domain.social.service;

import com.grepp.funfun.app.domain.social.dto.MessageDTO;
import com.grepp.funfun.app.domain.social.entity.Message;
import com.grepp.funfun.app.domain.social.repository.MessageRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(final MessageRepository messageRepository,
            final UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public List<MessageDTO> findAll() {
        final List<Message> messages = messageRepository.findAll(Sort.by("id"));
        return messages.stream()
                .map(message -> mapToDTO(message, new MessageDTO()))
                .toList();
    }

    public MessageDTO get(final Long id) {
        return messageRepository.findById(id)
                .map(message -> mapToDTO(message, new MessageDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final MessageDTO messageDTO) {
        final Message message = new Message();
        mapToEntity(messageDTO, message);
        return messageRepository.save(message).getId();
    }

    public void update(final Long id, final MessageDTO messageDTO) {
        final Message message = messageRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(messageDTO, message);
        messageRepository.save(message);
    }

    public void delete(final Long id) {
        messageRepository.deleteById(id);
    }

    private MessageDTO mapToDTO(final Message message, final MessageDTO messageDTO) {
        messageDTO.setId(message.getId());
        messageDTO.setContent(message.getContent());
        messageDTO.setIsRead(message.getIsRead());
        messageDTO.setReadAt(message.getReadAt());
        messageDTO.setSender(message.getSender() == null ? null : message.getSender().getEmail());
        messageDTO.setReceiver(message.getReceiver() == null ? null : message.getReceiver().getEmail());
        return messageDTO;
    }

    private Message mapToEntity(final MessageDTO messageDTO, final Message message) {
        message.setContent(messageDTO.getContent());
        message.setIsRead(messageDTO.getIsRead());
        message.setReadAt(messageDTO.getReadAt());
        final User sender = messageDTO.getSender() == null ? null : userRepository.findById(messageDTO.getSender())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        message.setSender(sender);
        final User receiver = messageDTO.getReceiver() == null ? null : userRepository.findById(messageDTO.getReceiver())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        message.setReceiver(receiver);
        return message;
    }

}
