package gr.hua.dit.studyrooms.core.port.impl;

import gr.hua.dit.studyrooms.config.RestApiClientConfig;
import gr.hua.dit.studyrooms.core.port.PhoneNumberPort;
import gr.hua.dit.studyrooms.core.port.impl.dto.PhoneNumberValidationResult;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class PhoneNumberPortImpl implements PhoneNumberPort {

    private RestTemplate restTemplate;

    public PhoneNumberPortImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PhoneNumberValidationResult validate(String phone) {
        try {
            String url = RestApiClientConfig.BASE_URL + "/api/v1/phone-numbers/" + phone + "/validations";
            ResponseEntity<PhoneNumberValidationResult> resp = restTemplate.getForEntity(url, PhoneNumberValidationResult.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return resp.getBody();
            }
            // fallback
            String e164 = phone.startsWith("+") ? phone : "+30" + phone;
            return new PhoneNumberValidationResult(phone, true, "mobile", e164);
        } catch (RestClientException e) {
            // fallback
            String e164 = phone.startsWith("+") ? phone : "+30" + phone;
            return new PhoneNumberValidationResult(phone, true, "mobile", e164);
        }
    }
}
