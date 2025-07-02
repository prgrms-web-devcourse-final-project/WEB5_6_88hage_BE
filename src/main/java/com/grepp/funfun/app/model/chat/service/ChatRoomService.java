package com.grepp.funfun.app.model.chat.service;

import com.grepp.funfun.app.model.chat.dto.ChatRoomDTO;
import com.grepp.funfun.app.model.chat.entity.Chat;
import com.grepp.funfun.app.model.chat.entity.ChatRoom;
import com.grepp.funfun.app.model.chat.repository.ChatRepository;
import com.grepp.funfun.app.model.chat.repository.ChatRoomRepository;
import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final GroupRepository groupRepository;
    private final ChatRepository chatRepository;

    public ChatRoomService(final ChatRoomRepository chatRoomRepository,
            final GroupRepository groupRepository, final ChatRepository chatRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.groupRepository = groupRepository;
        this.chatRepository = chatRepository;
    }

    public List<ChatRoomDTO> findAll() {
        final List<ChatRoom> chatRooms = chatRoomRepository.findAll(Sort.by("id"));
        return chatRooms.stream()
                .map(chatRoom -> mapToDTO(chatRoom, new ChatRoomDTO()))
                .toList();
    }

    public ChatRoomDTO get(final Long id) {
        return chatRoomRepository.findById(id)
                .map(chatRoom -> mapToDTO(chatRoom, new ChatRoomDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ChatRoomDTO chatRoomDTO) {
        final ChatRoom chatRoom = new ChatRoom();
        mapToEntity(chatRoomDTO, chatRoom);
        return chatRoomRepository.save(chatRoom).getId();
    }

    public void update(final Long id, final ChatRoomDTO chatRoomDTO) {
        final ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(chatRoomDTO, chatRoom);
        chatRoomRepository.save(chatRoom);
    }

    public void delete(final Long id) {
        chatRoomRepository.deleteById(id);
    }

    private ChatRoomDTO mapToDTO(final ChatRoom chatRoom, final ChatRoomDTO chatRoomDTO) {
        chatRoomDTO.setId(chatRoom.getId());
        chatRoomDTO.setGroup(chatRoom.getGroup() == null ? null : chatRoom.getGroup().getId());
        return chatRoomDTO;
    }

    private ChatRoom mapToEntity(final ChatRoomDTO chatRoomDTO, final ChatRoom chatRoom) {
        final Group group = chatRoomDTO.getGroup() == null ? null : groupRepository.findById(chatRoomDTO.getGroup())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        chatRoom.setGroup(group);
        return chatRoom;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        final Chat roomChat = chatRepository.findFirstByRoom(chatRoom);
        if (roomChat != null) {
            referencedWarning.setKey("chatRoom.chat.room.referenced");
            referencedWarning.addParam(roomChat.getId());
            return referencedWarning;
        }
        return null;
    }

}
