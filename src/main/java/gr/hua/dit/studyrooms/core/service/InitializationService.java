package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.model.Client;
import gr.hua.dit.studyrooms.core.model.PersonType;
import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.repository.ClientRepository;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.service.model.CreatePersonRequest;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class InitializationService {

    private ClientRepository clientRepo;
    private StudyRoomRepository roomRepo;
    private PersonBusinessLogicService personService;
    private AtomicBoolean done = new AtomicBoolean(false);

    public InitializationService(ClientRepository clientRepo,
                                 StudyRoomRepository roomRepo,
                                 PersonBusinessLogicService personService) {
        this.clientRepo = clientRepo;
        this.roomRepo = roomRepo;
        this.personService = personService;
    }

    @PostConstruct
    public void init() {
        if (done.getAndSet(true)) return;
        
        // clients
        clientRepo.saveAll(List.of(
            new Client(null, "client01", "s3cr3t", "INTEGRATION_READ,INTEGRATION_WRITE"),
            new Client(null, "client02", "s3cr3t", "INTEGRATION_READ")
        ));

        // test users
        List<CreatePersonRequest> users = List.of(
            new CreatePersonRequest(PersonType.STAFF, "staff001", "Maria", "Papadopoulou", 
                "maria.staff@hua.gr", "+306900000000", "1234"),
            new CreatePersonRequest(PersonType.STUDENT, "it2023001", "Giorgos", "Nikolaou", 
                "it2023001@hua.gr", "+306900000001", "1234"),
            new CreatePersonRequest(PersonType.STUDENT, "it2023002", "Anna", "Konstantinou", 
                "it2023002@hua.gr", "+306900000002", "1234")
        );
        for (var req : users) {
            try {
                personService.createPerson(req, false);
            } catch (Exception ignored) {}
        }

        // rooms
        addRoom("Room A1", 10, LocalTime.of(8, 0), LocalTime.of(20, 0), "Individual study room");
        addRoom("Room A2", 10, LocalTime.of(8, 0), LocalTime.of(20, 0), "Individual study room");
        addRoom("Group Room B1", 6, LocalTime.of(9, 0), LocalTime.of(18, 0), "Group study room with whiteboard");
        addRoom("Computer Lab C1", 20, LocalTime.of(8, 0), LocalTime.of(22, 0), "Computer lab");
        addRoom("Reading Hall", 30, LocalTime.of(7, 0), LocalTime.of(23, 0), "Quiet area");
    }

    private void addRoom(String name, int cap, LocalTime start, LocalTime end, String desc) {
        if (roomRepo.existsByName(name)) return;
        StudyRoom r = new StudyRoom();
        r.setName(name);
        r.setCapacity(cap);
        r.setOperatingHoursStart(start);
        r.setOperatingHoursEnd(end);
        r.setDescription(desc);
        r.setIsActive(true);
        roomRepo.save(r);
    }
}
