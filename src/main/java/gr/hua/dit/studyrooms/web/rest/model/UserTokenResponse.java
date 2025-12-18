package gr.hua.dit.studyrooms.web.rest.model;

public record UserTokenResponse(
    String token,
    String type,
    long expiresIn,
    String role
) {}

