package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.model.Client;
import gr.hua.dit.studyrooms.core.model.PersonType;
import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.repository.ClientRepository;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.service.model.CreatePersonRequest;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class InitializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    private final ClientRepository clientRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final PersonBusinessLogicService personBusinessLogicService;
    private final AtomicBoolean initialized;

    public InitializationService(final ClientRepository clientRepository,
                                 final StudyRoomRepository studyRoomRepository,
                                 final PersonBusinessLogicService personBusinessLogicService) {
        if (clientRepository == null) throw new NullPointerException();
        if (studyRoomRepository == null) throw new NullPointerException();
        if (personBusinessLogicService == null) throw new NullPointerException();
        this.clientRepository = clientRepository;
        this.studyRoomRepository = studyRoomRepository;
        this.personBusinessLogicService = personBusinessLogicService;
        this.initialized = new AtomicBoolean(false);
    }

    @PostConstruct
    public void populateDatabaseWithInitialData() {
        final boolean alreadyInitialized = this.initialized.getAndSet(true);
        if (alreadyInitialized) {
            LOGGER.warn("Database initialization skipped: initial data has already been populated.");
            return;
        }
        LOGGER.info("Starting database initialization with initial data...");
        final List<Client> clientList = List.of(
            new Client(null, "client01", "s3cr3t", "INTEGRATION_READ,INTEGRATION_WRITE"),
            new Client(null, "client02", "s3cr3t", "INTEGRATION_READ")
        );
        this.clientRepository.saveAll(clientList);
        final List<CreatePersonRequest> createPersonRequestList = List.of(
            new CreatePersonRequest(
                PersonType.STAFF,
                "staff001",
                "Maria",
                "Papadopoulou",
                "maria.staff@hua.gr",
                "+306900000000",
                "1234"
            ),
            new CreatePersonRequest(
                PersonType.STUDENT,
                "it2023001",
                "Giorgos",
                "Nikolaou",
                "it2023001@hua.gr",
                "+306900000001",
                "1234"
            ),
            new CreatePersonRequest(
                PersonType.STUDENT,
                "it2023002",
                "Anna",
                "Konstantinou",
                "it2023002@hua.gr",
                "+306900000002",
                "1234"
            )
        );
        for (final var createPersonRequest : createPersonRequestList) {
            try {
                final var result = this.personBusinessLogicService.createPerson(createPersonRequest, false);
                if (!result.created()) {
                    LOGGER.warn("Failed to create person {}: {}", createPersonRequest.huaId(), result.reason());
                }
            } catch (final Exception e) {
                LOGGER.warn("Failed to create person {} during initialization: {}", createPersonRequest.huaId(), e.getMessage());
            }
        }
        createStudyRoomIfNotExists("Room A1", 10, LocalTime.of(8, 0), LocalTime.of(20, 0), "Individual study room", true);
        createStudyRoomIfNotExists("Room A2", 10, LocalTime.of(8, 0), LocalTime.of(20, 0), "Individual study room", true);
        createStudyRoomIfNotExists("Group Room B1", 6, LocalTime.of(9, 0), LocalTime.of(18, 0), "Group study room with whiteboard", true);
        createStudyRoomIfNotExists("Computer Lab C1", 20, LocalTime.of(8, 0), LocalTime.of(22, 0), "Computer lab for digital resources", true);
        createStudyRoomIfNotExists("Reading Hall", 30, LocalTime.of(7, 0), LocalTime.of(23, 0), "Quiet reading area", true);
        LOGGER.info("Database initialization completed successfully.");
    }

    private void createStudyRoomIfNotExists(String name, int capacity, LocalTime start, LocalTime end, String desc, boolean active) {
        if (this.studyRoomRepository.existsByName(name)) {
            return;
        }
        final StudyRoom room = new StudyRoom();
        room.setName(name);
        room.setCapacity(capacity);
        room.setOperatingHoursStart(start);
        room.setOperatingHoursEnd(end);
        room.setDescription(desc);
        room.setIsActive(active);
        this.studyRoomRepository.save(room);
    }
}
