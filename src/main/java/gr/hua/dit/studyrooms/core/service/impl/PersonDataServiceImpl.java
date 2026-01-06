package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.repository.PersonRepository;
import gr.hua.dit.studyrooms.core.service.PersonDataService;
import gr.hua.dit.studyrooms.core.service.mapper.PersonMapper;
import gr.hua.dit.studyrooms.core.service.model.PersonView;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonDataServiceImpl implements PersonDataService {

    private PersonRepository repo;
    private PersonMapper mapper;

    public PersonDataServiceImpl(PersonRepository repo, PersonMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<PersonView> getAllPeople() {
        return repo.findAll().stream().map(mapper::toView).toList();
    }

    @Override
    public Optional<PersonView> getPersonById(long id) {
        return repo.findById(id).map(mapper::toView);
    }
}
