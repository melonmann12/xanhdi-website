package com.xanhdi.website.controller;

import com.xanhdi.website.model.Booking;
import com.xanhdi.website.model.Tour;
import com.xanhdi.website.repository.BookingRepository;
import com.xanhdi.website.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class BookingController {

    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingController(TourRepository tourRepository, BookingRepository bookingRepository) {
        this.tourRepository = tourRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Handles the booking form POST from the tour detail page.
     * Calculates total price, saves the booking as PENDING,
     * then redirects to the same tour page with a success/error flash.
     */
    @PostMapping("/bookings/create")
    public String createBooking(
            @RequestParam("tourId") Long tourId,
            @RequestParam("customerName") String customerName,
            @RequestParam("customerPhone") String customerPhone,
            @RequestParam(value = "customerEmail", required = false) String customerEmail,
            @RequestParam("departureDate") String departureDateStr,
            @RequestParam(value = "numAdults", defaultValue = "1") Integer numAdults,
            @RequestParam(value = "numChildren", defaultValue = "0") Integer numChildren,
            @RequestParam(value = "specialRequests", required = false) String specialRequests,
            RedirectAttributes redirectAttributes) {

        Tour tour = tourRepository.findById(tourId)
                .orElse(null);

        if (tour == null) {
            redirectAttributes.addFlashAttribute("bookingError", "Không tìm thấy tour. Vui lòng thử lại.");
            return "redirect:/tours";
        }

        try {
            Booking booking = new Booking();
            booking.setTour(tour);
            booking.setCustomerName(customerName.trim());
            booking.setCustomerPhone(customerPhone.trim());
            booking.setCustomerEmail(customerEmail != null ? customerEmail.trim() : null);
            booking.setDepartureDate(LocalDate.parse(departureDateStr));
            booking.setNumAdults(numAdults < 1 ? 1 : numAdults);
            booking.setNumChildren(numChildren < 0 ? 0 : numChildren);
            booking.setSpecialRequests(specialRequests);
            booking.setStatus(Booking.BookingStatus.PENDING);

            // Calculate total price: full price per adult, 50% for children
            double total = (tour.getPrice() * booking.getNumAdults())
                         + (tour.getPrice() * 0.5 * booking.getNumChildren());
            booking.setTotalPrice(total);

            bookingRepository.save(booking);

            redirectAttributes.addFlashAttribute("bookingSuccess",
                    "Đặt tour thành công! Chúng tôi sẽ liên hệ xác nhận trong thời gian sớm nhất. Mã đặt tour: #" + booking.getId());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("bookingError",
                    "Có lỗi xảy ra khi đặt tour: " + e.getMessage() + ". Vui lòng thử lại hoặc liên hệ hotline.");
        }

        return "redirect:/tour-detail/" + tourId;
    }
}
