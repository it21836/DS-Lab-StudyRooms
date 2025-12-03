package gr.hua.dit.studyrooms.core.service.model;

import gr.hua.dit.studyrooms.core.model.TicketStatus;
import gr.hua.dit.studyrooms.core.service.TicketBusinessLogicService;

import java.time.Instant;

/**
 * General view of {@link gr.hua.dit.studyrooms.core.model.Ticket} entity.
 *
 * @see gr.hua.dit.studyrooms.core.model.Ticket
 * @see TicketBusinessLogicService
 */
public record TicketView(
    long id,
    PersonView student,
    PersonView teacher,
    TicketStatus status,
    String subject,
    String studentContent,
    String teacherContent,
    Instant queuedAt,
    Instant inProgressAt,
    Instant completedAt
) {}
