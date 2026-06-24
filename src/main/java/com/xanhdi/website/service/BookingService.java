package com.xanhdi.website.service;

import com.xanhdi.website.model.Booking;
import com.xanhdi.website.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void confirmBooking(Long id) {
        bookingRepository.findByIdWithTour(id).ifPresent(b -> {
            b.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingRepository.save(b);
            emailService.sendBookingConfirmedEmail(b);
        });
    }

    @Transactional
    public void cancelBooking(Long id) {
        bookingRepository.findByIdWithTour(id).ifPresent(b -> {
            b.setStatus(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(b);
            emailService.sendBookingCancelledEmail(b);
        });
    }

    @Transactional
    public void rejectBooking(Long id, String reason) {
        bookingRepository.findByIdWithTour(id).ifPresent(b -> {
            b.setStatus(Booking.BookingStatus.CANCELLED);
            b.setRejectionReason(reason);
            bookingRepository.save(b);
            emailService.sendBookingCancelledEmail(b);
        });
    }
}
