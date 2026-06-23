package com.xanhdi.website.controller;

import com.xanhdi.website.model.Booking;
import com.xanhdi.website.model.StaffUser;
import com.xanhdi.website.model.Tour;
import com.xanhdi.website.model.TourImage;
import com.xanhdi.website.model.TourTimeline;
import com.xanhdi.website.model.TourGuide;
import com.xanhdi.website.repository.BookingRepository;
import com.xanhdi.website.repository.StaffUserRepository;
import com.xanhdi.website.repository.TourRepository;
import com.xanhdi.website.repository.TourTimelineRepository;
import com.xanhdi.website.repository.TourGuideRepository;
import com.xanhdi.website.service.EmailService;
import com.xanhdi.website.service.StorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class StaffController {

    private final StaffUserRepository staffUserRepository;
    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;
    private final StorageService storageService;
    private final TourTimelineRepository tourTimelineRepository;
    private final TourGuideRepository tourGuideRepository;

    @Autowired
    public StaffController(StaffUserRepository staffUserRepository,
                           TourRepository tourRepository,
                           BookingRepository bookingRepository,
                           EmailService emailService,
                           StorageService storageService,
                           TourTimelineRepository tourTimelineRepository,
                           TourGuideRepository tourGuideRepository) {
        this.staffUserRepository = staffUserRepository;
        this.tourRepository = tourRepository;
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
        this.storageService = storageService;
        this.tourTimelineRepository = tourTimelineRepository;
        this.tourGuideRepository = tourGuideRepository;
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
    public String dashboard(@RequestParam(value = "tab", required = false) String tab,
                            HttpSession session, Model model) {
        if (session.getAttribute("staff") == null) {
            return "redirect:/staff/login";
        }
        StaffUser staff = (StaffUser) session.getAttribute("staff");
        List<Tour> tours = tourRepository.findAll();
        List<Booking> bookings = bookingRepository.findAll();

        model.addAttribute("staff", staff);
        model.addAttribute("tours", tours);
        model.addAttribute("bookings", bookings);
        model.addAttribute("activeTab", tab != null ? tab : "bookings");
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

    @PostMapping({"/staff/bookings/{id}/reject", "/staff/bookings/{id}/reject/"})
    public String rejectBooking(@PathVariable Long id,
                                @RequestParam(value = "rejectionReason", required = false) String rejectionReason,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        bookingRepository.findById(id).ifPresent(b -> {
            b.setStatus(Booking.BookingStatus.CANCELLED);
            String reason = (rejectionReason != null && !rejectionReason.trim().isEmpty())
                    ? rejectionReason.trim() : null;
            b.setRejectionReason(reason);
            bookingRepository.save(b);
            // Ensure lazy association is initialized before async email thread reads it
            if (b.getTour() != null) {
                b.getTour().getTitle();
            }
            emailService.sendBookingCancelledEmail(b);
        });
        ra.addFlashAttribute("dashMsg", "Đơn đặt #" + id + " đã bị từ chối.");
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
            @RequestParam(value = "galleryFiles", required = false) MultipartFile[] galleryFiles,
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
        tour.setDescription(description);
        tour.setInclusions(inclusions);
        tour.setJournalContent(journalContent);
        tour.setJournalQuote(journalQuote);
        tour.setGuideName(guideName);

        // Upload gallery files and add to tour images
        if (galleryFiles != null && galleryFiles.length > 0) {
            for (MultipartFile file : galleryFiles) {
                if (file != null && !file.isEmpty()) {
                    try {
                        String uploadedUrl = storageService.uploadFile(file);
                        TourImage tourImage = new TourImage(uploadedUrl, tour);
                        tour.getImages().add(tourImage);
                    } catch (Exception e) {
                        System.err.println("Failed to upload gallery file: " + e.getMessage());
                    }
                }
            }
        }

        // Set primary imageUrl to first gallery image if present
        if (!tour.getImages().isEmpty()) {
            tour.setImageUrl(tour.getImages().get(0).getImageUrl());
        }

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
            @RequestParam(value = "galleryFiles", required = false) MultipartFile[] galleryFiles,
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
        tour.setDescription(description);
        tour.setInclusions(inclusions);
        tour.setJournalContent(journalContent);
        tour.setJournalQuote(journalQuote);
        tour.setGuideName(guideName);

        // Upload gallery files and add to tour images
        if (galleryFiles != null && galleryFiles.length > 0) {
            for (MultipartFile file : galleryFiles) {
                if (file != null && !file.isEmpty()) {
                    try {
                        String uploadedUrl = storageService.uploadFile(file);
                        TourImage tourImage = new TourImage(uploadedUrl, tour);
                        tour.getImages().add(tourImage);
                    } catch (Exception e) {
                        System.err.println("Failed to upload gallery file: " + e.getMessage());
                    }
                }
            }
        }

        // Set primary imageUrl to first gallery image if current is blank and we have images
        if ((tour.getImageUrl() == null || tour.getImageUrl().isEmpty()) && !tour.getImages().isEmpty()) {
            tour.setImageUrl(tour.getImages().get(0).getImageUrl());
        }

        tourRepository.save(tour);

        ra.addFlashAttribute("dashMsg", "Tour \"" + title + "\" đã được cập nhật!");
        return "redirect:/staffdashboard";
    }

    @PostMapping({"/staff/tours/{tourId}/images/delete/{imageId}", "/staff/tours/{tourId}/images/delete/{imageId}/"})
    public String deleteGalleryImage(
            @PathVariable("tourId") Long tourId,
            @PathVariable("imageId") Long imageId,
            HttpSession session,
            RedirectAttributes ra) {

        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        Optional<Tour> tourOpt = tourRepository.findById(tourId);
        if (tourOpt.isPresent()) {
            Tour tour = tourOpt.get();
            tour.getImages().removeIf(img -> {
                if (img.getId().equals(imageId)) {
                    if (img.getImageUrl().equals(tour.getImageUrl())) {
                        tour.setImageUrl(null);
                    }
                    return true;
                }
                return false;
            });

            if (tour.getImageUrl() == null && !tour.getImages().isEmpty()) {
                tour.setImageUrl(tour.getImages().get(0).getImageUrl());
            }

            tourRepository.save(tour);
            ra.addFlashAttribute("dashMsg", "Đã xoá ảnh khỏi album thành công.");
        }
        return "redirect:/staff/tours/edit/" + tourId;
    }

    @PostMapping({"/staff/tours/{tourId}/timeline/save", "/staff/tours/{tourId}/timeline/save/"})
    public String saveTimelineStep(
            @PathVariable("tourId") Long tourId,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("timeSlot") String timeSlot,
            @RequestParam("title") String title,
            @RequestParam(value = "locationTitle", required = false) String locationTitle,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sortOrder", defaultValue = "1") Integer sortOrder,
            @RequestParam(value = "timelineImageFile", required = false) MultipartFile file,
            HttpSession session,
            RedirectAttributes ra) {

        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        Tour tour = tourRepository.findById(tourId).orElse(null);
        if (tour == null) {
            ra.addFlashAttribute("dashMsg", "Tour không tồn tại.");
            return "redirect:/staffdashboard";
        }

        TourTimeline timeline;
        if (id != null) {
            timeline = tourTimelineRepository.findById(id).orElse(new TourTimeline());
        } else {
            timeline = new TourTimeline();
            timeline.setTour(tour);
        }

        timeline.setTimeSlot(timeSlot.trim());
        timeline.setTitle(title.trim());
        timeline.setLocationTitle(locationTitle != null ? locationTitle.trim() : null);
        timeline.setDescription(description != null ? description.trim() : null);
        timeline.setSortOrder(sortOrder);
        if (timeline.getIcon() == null || timeline.getIcon().isEmpty()) {
            timeline.setIcon("location_on");
        }

        if (file != null && !file.isEmpty()) {
            try {
                String imageUrl = storageService.uploadFile(file);
                timeline.setImageUrl(imageUrl);
            } catch (Exception e) {
                System.err.println("=== ERROR UPLOADING TIMELINE IMAGE ===");
                e.printStackTrace();
                ra.addFlashAttribute("dashMsg", "Lịch trình đã lưu nhưng tải ảnh thất bại: " + e.getMessage());
            }
        }

        tourTimelineRepository.save(timeline);
        if (ra.getFlashAttributes().get("dashMsg") == null) {
            ra.addFlashAttribute("dashMsg", "Lịch trình đã được cập nhật thành công!");
        }
        return "redirect:/staff/tours/edit/" + tourId;
    }

    @PostMapping({"/staff/tours/{tourId}/timeline/delete/{id}", "/staff/tours/{tourId}/timeline/delete/{id}/"})
    public String deleteTimelineStep(
            @PathVariable("tourId") Long tourId,
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes ra) {

        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        tourTimelineRepository.findById(id).ifPresent(t -> {
            tourTimelineRepository.delete(t);
        });

        ra.addFlashAttribute("dashMsg", "Đã xoá bước lịch trình.");
        return "redirect:/staff/tours/edit/" + tourId;
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

    // =========================================================
    // STAFF TOUR GUIDE CRUD
    // =========================================================

    @GetMapping({"/staff/guides", "/staff/guides/"})
    public String staffGuidesList(HttpSession session, Model model) {
        if (session.getAttribute("staff") == null) return "redirect:/staff/login";
        List<TourGuide> guides = tourGuideRepository.findAll();
        model.addAttribute("guides", guides);
        model.addAttribute("activeTab", "guides");
        return "staff-guide-list";
    }

    @GetMapping({"/staff/guides/new", "/staff/guides/new/"})
    public String newGuideForm(HttpSession session, Model model) {
        if (session.getAttribute("staff") == null) return "redirect:/staff/login";
        model.addAttribute("guide", new TourGuide());
        model.addAttribute("isEdit", false);
        return "staff-guide-form";
    }

    @GetMapping({"/staff/guides/edit/{id}", "/staff/guides/edit/{id}/"})
    public String editGuideForm(@PathVariable("id") Long id,
                                HttpSession session,
                                Model model,
                                RedirectAttributes ra) {
        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        Optional<TourGuide> guideOpt = tourGuideRepository.findById(id);
        if (guideOpt.isEmpty()) {
            ra.addFlashAttribute("dashMsg", "Hướng dẫn viên không tồn tại.");
            return "redirect:/staff/guides";
        }
        model.addAttribute("guide", guideOpt.get());
        model.addAttribute("isEdit", true);
        return "staff-guide-form";
    }

    @PostMapping({"/staff/guides/save", "/staff/guides/save/"})
    public String saveGuide(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "specialty", required = false) String specialty,
            @RequestParam(value = "languages", required = false) String languages,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirthStr,
            @RequestParam(value = "rating", defaultValue = "5.0") Double rating,
            @RequestParam(value = "reviewCount", defaultValue = "0") Integer reviewCount,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            HttpSession session,
            RedirectAttributes ra) {

        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        TourGuide guide;
        if (id != null) {
            guide = tourGuideRepository.findById(id).orElse(new TourGuide());
        } else {
            guide = new TourGuide();
            guide.setRating(rating);
            guide.setReviewCount(reviewCount);
        }

        guide.setName(name.trim());
        guide.setSpecialty(specialty != null ? specialty.trim() : null);
        guide.setLanguages(languages != null ? languages.trim() : null);
        guide.setBio(bio != null ? bio.trim() : null);

        if (dateOfBirthStr != null && !dateOfBirthStr.trim().isEmpty()) {
            try {
                guide.setDateOfBirth(java.time.LocalDate.parse(dateOfBirthStr));
            } catch (Exception e) {
                System.err.println("Failed to parse dateOfBirth: " + dateOfBirthStr);
            }
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String avatarUrl = storageService.uploadFile(avatarFile);
                guide.setAvatarUrl(avatarUrl);
            } catch (Exception e) {
                System.err.println("Failed to upload avatar: " + e.getMessage());
                ra.addFlashAttribute("dashMsg", "Đã lưu thông tin nhưng tải ảnh đại diện thất bại: " + e.getMessage());
            }
        }

        tourGuideRepository.save(guide);
        if (ra.getFlashAttributes().get("dashMsg") == null) {
            ra.addFlashAttribute("dashMsg", "Hướng dẫn viên \"" + name + "\" đã được lưu thành công.");
        }

        return "redirect:/staff/guides";
    }

    @PostMapping({"/staff/guides/delete/{id}", "/staff/guides/delete/{id}/"})
    public String deleteGuide(@PathVariable("id") Long id,
                              HttpSession session,
                              RedirectAttributes ra) {
        if (session.getAttribute("staff") == null) return "redirect:/staff/login";

        tourGuideRepository.findById(id).ifPresent(g -> {
            tourGuideRepository.deleteById(id);
            ra.addFlashAttribute("dashMsg", "Hướng dẫn viên \"" + g.getName() + "\" đã được xoá.");
        });

        return "redirect:/staff/guides";
    }
}
