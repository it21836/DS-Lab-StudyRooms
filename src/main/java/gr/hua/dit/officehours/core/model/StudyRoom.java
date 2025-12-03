package gr.hua.dit.officehours.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalTime;

/**
 * StudyRoom entity.
 */
@Entity
@Table(
    name = "study_room",
    indexes = {
        @Index(name = "idx_study_room_name", columnList = "name"),
        @Index(name = "idx_study_room_active", columnList = "is_active")
    }
)
public final class StudyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull
    @Column(name = "operating_hours_start", nullable = false)
    private LocalTime operatingHoursStart;

    @NotNull
    @Column(name = "operating_hours_end", nullable = false)
    private LocalTime operatingHoursEnd;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public StudyRoom() {
    }

    public StudyRoom(Long id,
                     String name,
                     Integer capacity,
                     LocalTime operatingHoursStart,
                     LocalTime operatingHoursEnd,
                     String description,
                     Boolean isActive,
                     Instant createdAt) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.operatingHoursStart = operatingHoursStart;
        this.operatingHoursEnd = operatingHoursEnd;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LocalTime getOperatingHoursStart() {
        return operatingHoursStart;
    }

    public void setOperatingHoursStart(LocalTime operatingHoursStart) {
        this.operatingHoursStart = operatingHoursStart;
    }

    public LocalTime getOperatingHoursEnd() {
        return operatingHoursEnd;
    }

    public void setOperatingHoursEnd(LocalTime operatingHoursEnd) {
        this.operatingHoursEnd = operatingHoursEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StudyRoom{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", capacity=").append(capacity);
        sb.append(", isActive=").append(isActive);
        sb.append('}');
        return sb.toString();
    }
}

