package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.service.model.PersonView;

import java.util.List;
import java.util.Optional;

public interface PersonDataService {

    List<PersonView> getAllPeople();

    Optional<PersonView> getPersonById(long id);
}
