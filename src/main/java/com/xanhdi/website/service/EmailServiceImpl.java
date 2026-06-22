package com.xanhdi.website.service;

import com.xanhdi.website.model.Booking;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.host:NOT_SET}")
    private String mailHost;

    @Value("${spring.mail.port:0}")
    private int mailPort;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // =========================================================
    // @PostConstruct: Boot-time diagnostics — visible in Render logs
    // =========================================================
    @PostConstruct
    public void logMailDiagnostics() {
        log.info("============================================================");
        log.info("[Mail Diagnostics] EmailServiceImpl initialized successfully.");
        log.info("[Mail Diagnostics] Resolved SMTP Host : {}", mailHost);
        log.info("[Mail Diagnostics] Resolved SMTP Port : {}", mailPort);
        log.info("[Mail Diagnostics] Resolved From Email: {}", fromEmail != null ? fromEmail : "NULL — ENV VAR NOT INJECTED");
        log.info("[Mail Diagnostics] Username length    : {}", fromEmail != null ? fromEmail.length() : 0);
        log.info("[Mail Diagnostics] Username looks valid: {}", fromEmail != null && fromEmail.contains("@") ? "YES" : "NO — check SPRING_MAIL_USERNAME env var on Render");
        log.info("============================================================");
    }

    // =========================================================
    // PENDING EMAIL
    // =========================================================
    @Override
    @Async
    public void sendBookingPendingEmail(Booking booking) {
        String recipient = booking.getCustomerEmail();
        Long bookingId = booking.getId();

        if (recipient == null || recipient.trim().isEmpty()) {
            log.warn("[Email] Booking #{} has no customer email — PENDING email skipped.", bookingId);
            return;
        }

        log.info("[Email] Attempting PENDING email → recipient={} booking=#{}", recipient, bookingId);
        log.info("[Email] Using SMTP from address: {}", fromEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("booking", booking);
            String htmlContent = templateEngine.process("emails/booking-pending", context);

            helper.setFrom(fromEmail);
            helper.setTo(recipient.trim());
            helper.setSubject("Xanh Đi - Xác nhận yêu cầu đặt tour #" + bookingId);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("[Email] ✓ PENDING email sent successfully to {} for booking #{}", recipient, bookingId);

        } catch (Exception e) {
            log.error("=== CRITICAL EMAIL SYSTEM ERROR: PENDING ===");
            log.error("[Email] Failed to send PENDING email to={} booking=#{}", recipient, bookingId);
            log.error("[Email] Exception type   : {}", e.getClass().getName());
            log.error("[Email] Exception message: {}", e.getMessage());
            // Print full cause chain for nested SMTP / SSL / Auth errors
            Throwable cause = e.getCause();
            int depth = 1;
            while (cause != null) {
                log.error("[Email] Caused by [{}]: {} — {}", depth, cause.getClass().getName(), cause.getMessage());
                cause = cause.getCause();
                depth++;
            }
            log.error("[Email] Full stack trace:", e);
            log.error("=== END OF EMAIL ERROR ===");
        }
    }

    // =========================================================
    // CONFIRMED EMAIL
    // =========================================================
    @Override
    @Async
    public void sendBookingConfirmedEmail(Booking booking) {
        String recipient = booking.getCustomerEmail();
        Long bookingId = booking.getId();

        if (recipient == null || recipient.trim().isEmpty()) {
            log.warn("[Email] Booking #{} has no customer email — CONFIRMED email skipped.", bookingId);
            return;
        }

        log.info("[Email] Attempting CONFIRMED email → recipient={} booking=#{}", recipient, bookingId);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("booking", booking);
            String htmlContent = templateEngine.process("emails/booking-confirmed", context);

            helper.setFrom(fromEmail);
            helper.setTo(recipient.trim());
            helper.setSubject("Xanh Đi - Xác nhận tour thành công #" + bookingId);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("[Email] ✓ CONFIRMED email sent successfully to {} for booking #{}", recipient, bookingId);

        } catch (Exception e) {
            log.error("=== CRITICAL EMAIL SYSTEM ERROR: CONFIRMED ===");
            log.error("[Email] Failed to send CONFIRMED email to={} booking=#{}", recipient, bookingId);
            log.error("[Email] Exception type   : {}", e.getClass().getName());
            log.error("[Email] Exception message: {}", e.getMessage());
            Throwable cause = e.getCause();
            int depth = 1;
            while (cause != null) {
                log.error("[Email] Caused by [{}]: {} — {}", depth, cause.getClass().getName(), cause.getMessage());
                cause = cause.getCause();
                depth++;
            }
            log.error("[Email] Full stack trace:", e);
            log.error("=== END OF EMAIL ERROR ===");
        }
    }

    // =========================================================
    // CANCELLED EMAIL
    // =========================================================
    @Override
    @Async
    public void sendBookingCancelledEmail(Booking booking) {
        String recipient = booking.getCustomerEmail();
        Long bookingId = booking.getId();

        if (recipient == null || recipient.trim().isEmpty()) {
            log.warn("[Email] Booking #{} has no customer email — CANCELLED email skipped.", bookingId);
            return;
        }

        log.info("[Email] Attempting CANCELLED email → recipient={} booking=#{}", recipient, bookingId);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("booking", booking);
            String htmlContent = templateEngine.process("emails/booking-cancelled", context);

            helper.setFrom(fromEmail);
            helper.setTo(recipient.trim());
            helper.setSubject("Xanh Đi - Thông báo hủy đơn đặt tour #" + bookingId);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("[Email] ✓ CANCELLED email sent successfully to {} for booking #{}", recipient, bookingId);

        } catch (Exception e) {
            log.error("=== CRITICAL EMAIL SYSTEM ERROR: CANCELLED ===");
            log.error("[Email] Failed to send CANCELLED email to={} booking=#{}", recipient, bookingId);
            log.error("[Email] Exception type   : {}", e.getClass().getName());
            log.error("[Email] Exception message: {}", e.getMessage());
            Throwable cause = e.getCause();
            int depth = 1;
            while (cause != null) {
                log.error("[Email] Caused by [{}]: {} — {}", depth, cause.getClass().getName(), cause.getMessage());
                cause = cause.getCause();
                depth++;
            }
            log.error("[Email] Full stack trace:", e);
            log.error("=== END OF EMAIL ERROR ===");
        }
    }
}
