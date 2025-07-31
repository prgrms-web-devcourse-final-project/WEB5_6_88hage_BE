package com.grepp.funfun.app.domain.group.scheduler;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class GroupCompleteScheduler {

    private final GroupRepository groupRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void groupComplete(){
        List<Group> groups = groupRepository.findByStatusIn(
            Arrays.asList(GroupStatus.RECRUITING,GroupStatus.FULL));

        LocalDateTime now = LocalDateTime.now();

        for (Group group : groups){
            LocalDateTime endTime = group.getGroupDate()
                .plusHours(group.getDuring())
                .plusHours(24);

            // nowTime > endTime
            if(now.isAfter(endTime)){
                group.changeStatusAndActivated(GroupStatus.COMPLETED);

                for (Participant participant : group.getParticipants()){
                    participant.changeStatusAndActivated(ParticipantStatus.GROUP_COMPLETE);
                }
            }
        }
        groupRepository.saveAll(groups);
    }
}
