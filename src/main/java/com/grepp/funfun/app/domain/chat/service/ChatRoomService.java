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
    @Transactional
    public void createPersonalChatRoom(String currentUserEmail, String targetUserEmail) {

        //todo : 팔로워 팔로잉 관계가 맞는지 확인하기 -> 근데 굳이 해야할까? 애초에 팔로워 팔로잉 관계가 아니면 이 메서드가 호출될 수가없음

        String[] emails = {currentUserEmail, targetUserEmail};
        Arrays.sort(emails);

        Optional<PersonalChatRoom> existingRoom = personalChatRoomRepository
            .findByUserAEmailAndUserBEmail(emails[0], emails[1]);

        if (existingRoom.isPresent()) {
            throw new CommonException(ResponseCode.BAD_REQUEST,"이미 존재하는 채팅방입니다");
        }

        // 새 채팅방 생성
        PersonalChatRoom personalChatRoom = PersonalChatRoom.builder()
            .status(ChatRoomType.PERSONAL_CHAT)
            .userAEmail(emails[0])
            .userBEmail(emails[1])
            .name(targetUserEmail + "님과의 채팅")
            .build();

        personalChatRoomRepository.save(personalChatRoom);
    }

    // 개인 채팅방 조회
    @Transactional(readOnly = true)
    public List<PersonalChatRoomResponse> getMyPersonalChatRooms(String userEmail) {
        log.info("개인 채팅방 목록 조회 for user: {}", userEmail);

        List<PersonalChatRoom> chatRooms = personalChatRoomRepository
            .findByUserAEmailOrUserBEmail(userEmail, userEmail);

        User currentuser = userRepository.findByEmail(userEmail);

        return chatRooms.stream()
            .map(room -> {
                // 상대방 이메일 결정
                String targetUserEmail = room.getUserAEmail().equals(userEmail)
                    ? room.getUserBEmail()
                    : room.getUserAEmail();

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
    }
}
