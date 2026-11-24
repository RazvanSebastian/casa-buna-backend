package ro.casabuna.casabuna_backend.access.team;

import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import ro.casabuna.casabuna_backend.access.CurrentUser;
import ro.casabuna.casabuna_backend.repository.TeamRepository;

import java.lang.reflect.Method;

@Aspect
@Component
@AllArgsConstructor
public class IsAssociatedManagerAspect {

    private TeamRepository teamRepository;
    private final CurrentUser currentUser;

    @Before("@within(IsAssociatedManager) || @annotation(IsAssociatedManager)")
    public void checkManagerAccess(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = AopUtils.getMostSpecificMethod(
                signature.getMethod(),
                joinPoint.getTarget().getClass()
        );

        IsAssociatedManager annotation = AnnotatedElementUtils.findMergedAnnotation(method, IsAssociatedManager.class);

        // Method-level wins, class-level is the fallback
        if (annotation == null) {
            annotation = AnnotatedElementUtils.findMergedAnnotation(joinPoint.getTarget().getClass(), IsAssociatedManager.class);
        }

        if (annotation == null || !annotation.required()) {
            return;
        }

        Long teamId = resolveTeamId(signature, joinPoint.getArgs(), annotation.teamIdParam());

        Long currentUserId = currentUser.get().id();
        if (!teamRepository.existsByIdAndUsersId(teamId, currentUserId)) {
            throw new AccessDeniedException("User " + currentUserId + " is not associated with team " + teamId);
        }
    }

    private Long resolveTeamId(MethodSignature signature, Object[] args, String paramName) {
        String[] names = signature.getParameterNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(paramName)) {
                return (Long) args[i];
            }
        }
        // Fail loudly instead of silently skipping the check
        throw new IllegalStateException("Method " + signature.toShortString()
                + " has no parameter named '" + paramName + "'");
    }
}
