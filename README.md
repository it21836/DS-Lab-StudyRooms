# StudyRooms

Σύστημα κράτησης χώρων μελέτης για τη βιβλιοθήκη του Χαροκοπείου Πανεπιστημίου.

## Εκτέλεση

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

Η εφαρμογή ξεκινάει στο http://localhost:8080

## Test λογαριασμοί

- Staff: `maria.staff@hua.gr` / `1234`
- Student: `it2023001@hua.gr` / `1234`

## API

Swagger UI: http://localhost:8080/swagger-ui.html

Για authentication χρησιμοποιούμε JWT tokens. Παίρνεις token από το `/api/v1/auth/tokens` με POST.

## Τεχνολογίες

- Spring Boot 3.5
- Spring Security + JWT
- Spring Data JPA
- Thymeleaf
- H2 Database
- Bootstrap 5

## Εξωτερικές υπηρεσίες

- **Holiday API** (date.nager.at): Για να μην επιτρέπονται κρατήσεις σε αργίες
- **SMS**: Ειδοποιήσεις (απενεργοποιημένο by default)

## Κανόνες κρατήσεων

- Max 3 κρατήσεις/ημέρα
- Διάρκεια: 30 λεπτά - 4 ώρες
- Μόνο εντός ωραρίου λειτουργίας
- Penalty για no-show (3 μέρες ban)
