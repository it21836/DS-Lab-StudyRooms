package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.service.model.BookingView;

import java.util.List;

public interface BookingDataService {

    List<BookingView> getAllBookings();
}

