package com.grepp.funfun.app.domain.chat.service;

import com.grepp.funfun.app.domain.chat.dto.payload.PersonalChatRoomResponse;
import com.grepp.funfun.app.domain.chat.entity.PersonalChatRoom;
import com.grepp.funfun.app.domain.chat.repository.PersonalChatRoomRepository;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.domain.social.repository.FollowRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final PersonalChatRoomRepository personalChatRoomRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // 개인 채팅방 생성
    @Transactional
    public void createPersonalChatRoom(String currentUserEmail, String targetUserEmail) {

        boolean exists = followRepository.existsByFollowerEmailAndFolloweeEmail(currentUserEmail, targetUserEmail);
        if (!exists) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "팔로우 관계가 아닙니다.");
        }

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

    // 채팅방 삭제
    @Transactional
    public void deletePersonalChatRoom(String currentUserEmail, String targetUserEmail) {
        String[] emails = {currentUserEmail, targetUserEmail};
        Arrays.sort(emails);

        Optional<PersonalChatRoom> existingRoom = personalChatRoomRepository
            .findByUserAEmailAndUserBEmail(emails[0], emails[1]);

        if (existingRoom.isEmpty()) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "존재하지 않는 채팅방입니다");
        }

        PersonalChatRoom changeRoom = existingRoom.get();

        if (currentUserEmail.equals(changeRoom.getUserAEmail())) {
            changeRoom.changeDeleted(currentUserEmail);
        } else if (currentUserEmail.equals(changeRoom.getUserBEmail())) {
            changeRoom.changeDeleted(currentUserEmail);
        } else {
            throw new IllegalStateException("현재 사용자가 채팅방에 속해있지 않습니다.");
        }
    }

    // 개인 채팅방 조회
    @Transactional(readOnly = true)
    public List<PersonalChatRoomResponse> getMyPersonalChatRooms(String userEmail) {
        log.info("개인 채팅방 목록 조회 for user: {}", userEmail);

        List<PersonalChatRoom> chatRooms = personalChatRoomRepository
            .findByUserAEmailOrUserBEmail(userEmail, userEmail);

        User currentuser = userRepository.findByEmail(userEmail);

        return chatRooms.stream()
            .filter(room -> {
                if (room.getUserAEmail().equals(userEmail)) {
                    return Boolean.FALSE.equals(room.getUserADeleted());
                } else if (room.getUserBEmail().equals(userEmail)) {
                    return Boolean.FALSE.equals(room.getUserBDeleted());
                } else {
                    return false;
                }
            })
            .map(room -> {
                // 상대방 이메일 결정
                String targetUserEmail = room.getUserAEmail().equals(userEmail)
                    ? room.getUserBEmail()
                    : room.getUserAEmail();

                User targetUser = userRepository.findByEmail(targetUserEmail);

                return PersonalChatRoomResponse.builder()
                    .roomId(room.getId())
                    .status(room.getStatus())
                    .currentUserEmail(currentuser.getEmail())
                    .currentUserNickname(currentuser.getNickname())
                    .currentUserDeleted(room.getUserAEmail().equals(userEmail)
                        ? room.getUserADeleted()
                        : room.getUserBDeleted())
                    .targetUserEmail(targetUserEmail)
                    .targetUserNickname(targetUser.getNickname())
                    .targetUserDeleted(room.getUserAEmail().equals(userEmail)
                        ? room.getUserBDeleted()
                        : room.getUserADeleted())
                    .build();
            })
            .collect(Collectors.toList());
    }

}
