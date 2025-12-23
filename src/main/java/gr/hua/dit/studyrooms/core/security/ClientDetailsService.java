package gr.hua.dit.studyrooms.core.security;

import java.util.Optional;

public interface ClientDetailsService {

    Optional<ClientDetails> authenticate(final String id, final String secret);
}
