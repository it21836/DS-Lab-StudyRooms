package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.Person;
import gr.hua.dit.studyrooms.core.model.PersonType;
import gr.hua.dit.studyrooms.core.port.LookupPort;
import gr.hua.dit.studyrooms.core.port.PhoneNumberPort;
import gr.hua.dit.studyrooms.core.port.impl.dto.PhoneNumberValidationResult;
import gr.hua.dit.studyrooms.core.repository.PersonRepository;
import gr.hua.dit.studyrooms.core.service.PersonBusinessLogicService;
import gr.hua.dit.studyrooms.core.service.mapper.PersonMapper;
import gr.hua.dit.studyrooms.core.service.model.CreatePersonRequest;
import gr.hua.dit.studyrooms.core.service.model.CreatePersonResult;
import gr.hua.dit.studyrooms.core.service.model.PersonView;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PersonBusinessLogicServiceImpl implements PersonBusinessLogicService {

    private Validator validator;
    private PasswordEncoder passwordEncoder;
    private PersonRepository personRepo;
    private PersonMapper mapper;
    private PhoneNumberPort phonePort;
    private LookupPort lookupPort;

    public PersonBusinessLogicServiceImpl(Validator validator,
                                          PasswordEncoder passwordEncoder,
                                          PersonRepository personRepo,
                                          PersonMapper mapper,
                                          PhoneNumberPort phonePort,
                                          LookupPort lookupPort) {
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.personRepo = personRepo;
        this.mapper = mapper;
        this.phonePort = phonePort;
        this.lookupPort = lookupPort;
    }

    @Transactional
    @Override
    public CreatePersonResult createPerson(CreatePersonRequest req, boolean notify) {
        // validation
        Set<ConstraintViolation<CreatePersonRequest>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (var v : violations) {
                sb.append(v.getPropertyPath()).append(": ").append(v.getMessage()).append("\n");
            }
            return CreatePersonResult.fail(sb.toString());
        }

        PersonType type = req.type();
        String huaId = req.huaId().strip();
        String fname = req.firstName().strip();
        String lname = req.lastName().strip();
        String email = req.emailAddress().strip();
        String phone = req.mobilePhoneNumber().strip();
        String password = req.rawPassword();

        // email check
        if (!email.endsWith("@hua.gr")) {
            return CreatePersonResult.fail("Επιτρέπονται μόνο ακαδημαϊκά email (@hua.gr)");
        }

        // phone validation
        PhoneNumberValidationResult phoneResult = phonePort.validate(phone);
        if (!phoneResult.isValidMobile()) {
            return CreatePersonResult.fail("Μη έγκυρος αριθμός κινητού τηλεφώνου");
        }
        phone = phoneResult.e164();

        // check duplicates
        if (personRepo.existsByHuaIdIgnoreCase(huaId)) {
            return CreatePersonResult.fail("Το HUA ID χρησιμοποιείται ήδη");
        }
        if (personRepo.existsByEmailAddressIgnoreCase(email)) {
            return CreatePersonResult.fail("Το email χρησιμοποιείται ήδη");
        }
        if (personRepo.existsByMobilePhoneNumber(phone)) {
            return CreatePersonResult.fail("Ο αριθμός κινητού χρησιμοποιείται ήδη");
        }

        // lookup
        PersonType lookupType = lookupPort.lookup(huaId).orElse(null);
        if (lookupType == null) {
            return CreatePersonResult.fail("Μη έγκυρο HUA ID");
        }
        if (lookupType != type) {
            return CreatePersonResult.fail("Ο τύπος χρήστη δεν ταιριάζει με το HUA ID");
        }

        String hash = passwordEncoder.encode(password);

        Person p = new Person();
        p.setHuaId(huaId);
        p.setType(type);
        p.setFirstName(fname);
        p.setLastName(lname);
        p.setEmailAddress(email);
        p.setMobilePhoneNumber(phone);
        p.setPasswordHash(hash);

        // validate entity
        Set<ConstraintViolation<Person>> pViolations = validator.validate(p);
        if (!pViolations.isEmpty()) {
            throw new RuntimeException("invalid Person");
        }

        p = personRepo.save(p);
        PersonView view = mapper.toView(p);
        return CreatePersonResult.success(view);
    }
}
