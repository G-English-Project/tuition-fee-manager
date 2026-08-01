package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.entities.ActionLog;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ActionTypeEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ResourceTypeEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ActionLogRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ActionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionLogServiceImpl implements ActionLogService {

    private static final int MAX_LABEL_LENGTH = 255;

    private final ActionLogRepository actionLogRepository;
    private final UserRepository userRepository;

    @Override
    public void record(
            ActionTypeEnum action,
            ResourceTypeEnum resourceType,
            Long resourceId,
            String resourceLabel,
            String detail
    ) {
        try {
            Actor actor = resolveActor();

            ActionLog entry = ActionLog.builder()
                    .actorId(actor.id())
                    .actorEmail(actor.email())
                    .actorRole(actor.role())
                    .action(action)
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .resourceLabel(truncateLabel(resourceLabel))
                    .detail(detail)
                    .build();

            actionLogRepository.save(entry);
        } catch (Exception e) {
            // Swallow so a logging failure never rolls back the business action.
            log.warn("Failed to write action log: action={}, resource={}#{}, reason={}",
                    action, resourceType, resourceId, e.getMessage());
        }
    }

    private Actor resolveActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return Actor.anonymous();
        }

        String email = String.valueOf(auth.getPrincipal());
        if (email.isBlank() || "anonymousUser".equals(email)) {
            return Actor.anonymous();
        }

        return userRepository.findByEmail(email)
                .map(Actor::from)
                .orElse(new Actor(null, email, null));
    }

    private static String truncateLabel(String value) {
        if (value == null) {
            return null;
        }
        return value.length() <= MAX_LABEL_LENGTH
                ? value
                : value.substring(0, MAX_LABEL_LENGTH);
    }

    private record Actor(Long id, String email, RoleEnum role) {
        static Actor from(User user) {
            return new Actor(user.getUserId(), user.getEmail(), user.getRole());
        }

        static Actor anonymous() {
            return new Actor(null, null, null);
        }
    }
}
