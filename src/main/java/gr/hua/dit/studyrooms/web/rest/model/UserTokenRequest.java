package gr.hua.dit.studyrooms.web.rest.model;

import jakarta.validation.constraints.NotBlank;

public record UserTokenRequest(
    @NotBlank String email,
    @NotBlank String password
) {}

