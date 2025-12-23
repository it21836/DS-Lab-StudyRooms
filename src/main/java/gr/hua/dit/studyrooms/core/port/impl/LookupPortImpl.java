package gr.hua.dit.studyrooms.core.port.impl;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import gr.hua.dit.studyrooms.config.RestApiClientConfig;
import gr.hua.dit.studyrooms.core.model.PersonType;
import gr.hua.dit.studyrooms.core.port.LookupPort;
import gr.hua.dit.studyrooms.core.port.impl.dto.LookupResult;

@Service
public class LookupPortImpl implements LookupPort {

    private RestTemplate restTemplate;

    public LookupPortImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<PersonType> lookup(String huaId) {
        try {
            String url = RestApiClientConfig.BASE_URL + "/api/v1/lookups/" + huaId;
            ResponseEntity<LookupResult> resp = restTemplate.getForEntity(url, LookupResult.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return Optional.ofNullable(resp.getBody().type());
            }
            return Optional.empty();
        } catch (RestClientException e) {
            // fallback
            if (huaId.toLowerCase().startsWith("staff")) {
                return Optional.of(PersonType.STAFF);
            } else if (huaId.toLowerCase().startsWith("it")) {
                return Optional.of(PersonType.STUDENT);
            }
            return Optional.empty();
        }
    }
}
