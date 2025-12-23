package gr.hua.dit.studyrooms.core.security;

import java.util.Set;

public record ClientDetails(
    String id,
    String secret,
    Set<String> roles
) {}
