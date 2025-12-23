package gr.hua.dit.studyrooms.core.port;

import gr.hua.dit.studyrooms.core.port.impl.dto.PhoneNumberValidationResult;

public interface PhoneNumberPort {

    PhoneNumberValidationResult validate(final String rawPhoneNumber);
}
