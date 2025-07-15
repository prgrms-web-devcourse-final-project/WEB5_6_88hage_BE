package com.grepp.funfun.app.domain.chat.service;

import com.grepp.funfun.app.domain.chat.dto.ChatRoomDTO;
import com.grepp.funfun.app.domain.chat.dto.payload.PersonalChatRoomResponse;
import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.entity.GroupChatRoom;
import com.grepp.funfun.app.domain.chat.entity.PersonalChatRoom;
import com.grepp.funfun.app.domain.chat.repository.ChatRepository;
import com.grepp.funfun.app.domain.chat.repository.GroupChatRoomRepository;
import com.grepp.funfun.app.domain.chat.repository.PersonalChatRoomRepository;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import com.grepp.funfun.app.delete.util.ReferencedWarning;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final PersonalChatRoomRepository personalChatRoomRepository;
    private final UserRepository userRepository;

    // 개인 채팅방 생성
    public void createPersonalChatRoom(String currentUserEmail, String targetUserEmail) {

        String[] emails = {currentUserEmail, targetUserEmail};
        Arrays.sort(emails);

        Optional<PersonalChatRoom> existingRoom = personalChatRoomRepository
            .findByUser1EmailAndUser2Email(emails[0], emails[1]);

        if (existingRoom.isPresent()) {
            throw new IllegalStateException("이미 존재하는 채팅방입니다");
        }

        // 새 채팅방 생성
        PersonalChatRoom personalChatRoom = PersonalChatRoom.builder()
            .status(ChatRoomType.PERSONAL_CHAT)
            .user1Email(emails[0])
            .user2Email(emails[1])
            .name(targetUserEmail + "님과의 채팅")
            .build();

        personalChatRoomRepository.save(personalChatRoom);
    }

    // 개인 채팅방 조회
    @Transactional(readOnly = true)
    public List<PersonalChatRoomResponse> getMyPersonalChatRooms(String userEmail) {
        log.info("개인 채팅방 목록 조회 for user: {}", userEmail);

        List<PersonalChatRoom> chatRooms = personalChatRoomRepository
            .findByUser1EmailOrUser2Email(userEmail, userEmail);

        User currentuser = userRepository.findByEmail(userEmail);

        List<PersonalChatRoomResponse> responses = chatRooms.stream()
            .map(room -> {
                // 상대방 이메일 결정
                String targetUserEmail = room.getUser1Email().equals(userEmail)
                    ? room.getUser2Email()
                    : room.getUser1Email();

                // 상대방 조회
                User targetUser = userRepository.findByEmail(targetUserEmail);

                return PersonalChatRoomResponse.builder()
                    .roomId(room.getId())
                    .roomName(room.getName())
                    .status(room.getStatus())
                    .currentUserEmail(currentuser.getEmail())
                    .currentUserNickname(currentuser.getNickname())
                    .targetUserEmail(targetUserEmail)
                    .targetUserNickname(targetUser.getNickname())
                    .build();
            })
            .collect(Collectors.toList());

        log.info("조회된 개인 채팅방 수: {}", responses.size());

        return responses;
    }
}
