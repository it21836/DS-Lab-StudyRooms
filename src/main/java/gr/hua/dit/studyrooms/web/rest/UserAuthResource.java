package gr.hua.dit.studyrooms.web.rest;

import gr.hua.dit.studyrooms.core.security.ApplicationUserDetails;
import gr.hua.dit.studyrooms.core.security.JwtService;
import gr.hua.dit.studyrooms.web.rest.model.UserTokenRequest;
import gr.hua.dit.studyrooms.web.rest.model.UserTokenResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "User authentication endpoints")
public class UserAuthResource {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserAuthResource(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Authenticate user", description = "Returns a JWT token for API access")
    @PostMapping("/tokens")
    public UserTokenResponse authenticate(@RequestBody @Valid UserTokenRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            ApplicationUserDetails user = (ApplicationUserDetails) auth.getPrincipal();
            String role = "ROLE_" + user.type().name();
            String token = jwtService.issue("user:" + user.personId(), List.of(role));

            return new UserTokenResponse(token, "Bearer", 3600, user.type().name());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }
}
