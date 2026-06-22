package com.xanhdi.website.controller;

import com.xanhdi.website.model.Booking;
import com.xanhdi.website.model.StaffUser;
import com.xanhdi.website.model.Tour;
import com.xanhdi.website.repository.BookingRepository;
import com.xanhdi.website.repository.StaffUserRepository;
import com.xanhdi.website.repository.TourRepository;
import com.xanhdi.website.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class StaffController {

    private final StaffUserRepository staffUserRepository;
    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    @Autowired
    public StaffController(StaffUserRepository staffUserRepository,
                           TourRepository tourRepository,
                           BookingRepository bookingRepository,
                           EmailService emailService) {
        this.staffUserRepository = staffUserRepository;
        this.tourRepository = tourRepository;
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    // =========================================================
    // AUTH
    // =========================================================

    @GetMapping("/staff/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("staff") != null) {
            return "redirect:/staffdashboard";
        }
        return "staff-login";
    }

    @PostMapping({"/staff/login", "/staff/login/"})
    public String processLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Optional<StaffUser> userOpt = staffUserRepository.findByUsername(username.trim());

        if (userOpt.isPresent()) {
            StaffUser user = userOpt.get();
            // Plain-text comparison
            if (user.getPassword().equals(password)) {
                session.setAttribute("staff", user);
                session.setMaxInactiveInterval(60 * 60 * 8); // 8-hour session
                return "redirect:/staffdashboard";
            }
        }

        model.addAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không đúng.");
        return "staff-login";
    }

    @GetMapping("/staff/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/staff/login";
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    @GetMapping("/staffdashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("staff") == null) {
            return "redirect:/staff/login";
        }
        StaffUser staff = (StaffUser) session.getAttribute("staff");
        List<Tour> tours = tourRepository.findAll();
        List<Booking> bookings = bookingRepository.findAll();

        model.addAttribute("staff", staff);
        model.addAttribute("tours", tours);
        model.addAttribute("bookings", bookings);
        return "staff-dashboard";
    }

    // =========================================================
    // BOOKING MANAGEMENT
    // =========================================================

    @PostMapping({"/staff/bookings/{id}/confirm", "/staff/bookings/{id}/confirm/"})
    public String confirmBooking(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        bookingRepository.findById(id).ifPresent(b -> {
            b.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingRepository.save(b);
            if (b.getTour() != null) {
                b.getTour().getTitle();
            }
            emailService.sendBookingConfirmedEmail(b);
        });
        ra.addFlashAttribute("dashMsg", "Đơn đặt #" + id + " đã được xác nhận.");
        return "redirect:/staffdashboard";
    }

    @PostMapping({"/staff/bookings/{id}/cancel", "/staff/bookings/{id}/cancel/"})
    public String cancelBooking(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        bookingRepository.findById(id).ifPresent(b -> {
            b.setStatus(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(b);
            if (b.getTour() != null) {
                b.getTour().getTitle();
            }
            emailService.sendBookingCancelledEmail(b);
        });
        ra.addFlashAttribute("dashMsg", "Đơn đặt #" + id + " đã bị huỷ.");
        return "redirect:/staffdashboard";
    }

    // =========================================================
    // TOUR CRUD
    // =========================================================

    @GetMapping("/staff/tours/new")
    public String newTourForm(HttpSession session, Model model) {
        if (session.getAttribute("staff") == null) return "redirect:/staff/login";
        model.addAttribute("tour", new Tour());
        model.addAttribute("isEdit", false);
        return "staff-tour-form";
    }

    @GetMapping("/staff/tours/edit/{id}")
    public String editTourForm(@PathVariable Long id,
                               HttpSession session,
                               Model model,
                               RedirectAttributes ra) {
        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        Optional<Tour> tourOpt = tourRepository.findById(id);
        if (tourOpt.isEmpty()) {
            ra.addFlashAttribute("dashMsg", "Tour không tồn tại.");
            return "redirect:/staffdashboard";
        }
        model.addAttribute("tour", tourOpt.get());
        model.addAttribute("isEdit", true);
        return "staff-tour-form";
    }

    @PostMapping({"/staff/tours/save", "/staff/tours/save/"})
    public String saveTour(
            @RequestParam("title") String title,
            @RequestParam(value = "price", defaultValue = "0") Double price,
            @RequestParam(value = "duration", required = false) String duration,
            @RequestParam(value = "activity", required = false) String activity,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "departure", required = false) String departure,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "inclusions", required = false) String inclusions,
            @RequestParam(value = "journalContent", required = false) String journalContent,
            @RequestParam(value = "journalQuote", required = false) String journalQuote,
            @RequestParam(value = "guideName", required = false) String guideName,
            HttpSession session,
            RedirectAttributes ra) {

        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        Tour tour = new Tour();
        tour.setRating(5.0);
        tour.setReviewCount(0);

        tour.setTitle(title.trim());
        tour.setPrice(price);
        tour.setDuration(duration);
        tour.setActivity(activity);
        tour.setTag(tag);
        tour.setDeparture(departure);
        tour.setImageUrl(imageUrl);
        tour.setDescription(description);
        tour.setInclusions(inclusions);
        tour.setJournalContent(journalContent);
        tour.setJournalQuote(journalQuote);
        tour.setGuideName(guideName);

        tourRepository.save(tour);

        ra.addFlashAttribute("dashMsg", "Tour \"" + title + "\" đã được tạo thành công!");
        return "redirect:/staffdashboard";
    }

    @PostMapping({"/staff/tours/update/{id}", "/staff/tours/update/{id}/"})
    public String updateTour(
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "price", defaultValue = "0") Double price,
            @RequestParam(value = "duration", required = false) String duration,
            @RequestParam(value = "activity", required = false) String activity,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "departure", required = false) String departure,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "inclusions", required = false) String inclusions,
            @RequestParam(value = "journalContent", required = false) String journalContent,
            @RequestParam(value = "journalQuote", required = false) String journalQuote,
            @RequestParam(value = "guideName", required = false) String guideName,
            HttpSession session,
            RedirectAttributes ra) {

        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        Tour tour = tourRepository.findById(id).orElse(null);
        if (tour == null) {
            ra.addFlashAttribute("dashMsg", "Tour không tồn tại.");
            return "redirect:/staffdashboard";
        }

        tour.setTitle(title.trim());
        tour.setPrice(price);
        tour.setDuration(duration);
        tour.setActivity(activity);
        tour.setTag(tag);
        tour.setDeparture(departure);
        tour.setImageUrl(imageUrl);
        tour.setDescription(description);
        tour.setInclusions(inclusions);
        tour.setJournalContent(journalContent);
        tour.setJournalQuote(journalQuote);
        tour.setGuideName(guideName);

        tourRepository.save(tour);

        ra.addFlashAttribute("dashMsg", "Tour \"" + title + "\" đã được cập nhật!");
        return "redirect:/staffdashboard";
    }

    @PostMapping({"/staff/tours/delete/{id}", "/staff/tours/delete/{id}/"})
    public String deleteTour(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes ra) {
        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        tourRepository.findById(id).ifPresent(t -> {
            ra.addFlashAttribute("dashMsg", "Tour \"" + t.getTitle() + "\" đã được xoá.");
            tourRepository.deleteById(id);
        });
        return "redirect:/staffdashboard";
    }
}
