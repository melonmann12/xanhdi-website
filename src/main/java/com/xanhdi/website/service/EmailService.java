package com.xanhdi.website.service;

import com.xanhdi.website.model.Booking;

public interface EmailService {
    void sendBookingPendingEmail(Booking booking);
    void sendBookingConfirmedEmail(Booking booking);
    void sendBookingCancelledEmail(Booking booking);
}
