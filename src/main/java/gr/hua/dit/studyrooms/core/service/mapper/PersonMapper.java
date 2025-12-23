package gr.hua.dit.studyrooms.core.service.mapper;

import gr.hua.dit.studyrooms.core.model.Person;
import gr.hua.dit.studyrooms.core.service.model.PersonView;

import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public PersonView toView(Person p) {
        if (p == null) return null;
        return new PersonView(
            p.getId(),
            p.getHuaId(),
            p.getFirstName(),
            p.getLastName(),
            p.getMobilePhoneNumber(),
            p.getEmailAddress(),
            p.getType()
        );
    }

    // keep old method for compatibility
    public PersonView convertPersonToPersonView(Person person) {
        return toView(person);
    }
}
