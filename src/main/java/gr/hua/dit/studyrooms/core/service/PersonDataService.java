package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.service.model.PersonView;

import java.util.List;

/**
 * Service for managing {@code Person} for data analytics purposes.
 */
public interface PersonDataService {

    List<PersonView> getAllPeople();
}
