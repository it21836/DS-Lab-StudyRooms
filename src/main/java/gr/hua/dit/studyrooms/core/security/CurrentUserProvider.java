package gr.hua.dit.studyrooms.core.security;

import gr.hua.dit.studyrooms.core.model.PersonType;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class CurrentUserProvider {

    public Optional<CurrentUser> getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof ApplicationUserDetails userDetails) {
            return Optional.of(new CurrentUser(userDetails.personId(), userDetails.getUsername(), userDetails.type()));
        }
        return Optional.empty();
    }

    public CurrentUser requireCurrentUser() {
        return this.getCurrentUser().orElseThrow(() -> new SecurityException("not authenticated"));
    }

    public long requiredStudentId() {
        var currentUser = requireCurrentUser();
        if (currentUser.type() != PersonType.STUDENT) {
            throw new SecurityException("Student role required");
        }
        return currentUser.id();
    }

    public long requiredStaffId() {
        var currentUser = requireCurrentUser();
        if (currentUser.type() != PersonType.STAFF) {
            throw new SecurityException("Staff role required");
        }
        return currentUser.id();
    }

    public boolean isStaff() {
        return getCurrentUser()
            .map(u -> u.type() == PersonType.STAFF)
            .orElse(false);
    }
}
