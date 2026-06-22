package com.xanhdi.website.service;

import com.xanhdi.website.model.Booking;
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

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    @Async
    public void sendBookingPendingEmail(Booking booking) {
        if (booking.getCustomerEmail() == null || booking.getCustomerEmail().trim().isEmpty()) {
            log.warn("Customer email is missing for booking #{}. Cannot send pending email.", booking.getId());
            return;
        }

        try {
            log.info("Sending booking pending email to {} for booking #{}", booking.getCustomerEmail(), booking.getId());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("booking", booking);

            String htmlContent = templateEngine.process("emails/booking-pending", context);

            helper.setFrom(fromEmail);
            helper.setTo(booking.getCustomerEmail().trim());
            helper.setSubject("Xanh Đi - Xác nhận yêu cầu đặt tour #" + booking.getId());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Successfully sent booking pending email for booking #{}", booking.getId());
        } catch (Exception e) {
            log.error("Failed to send booking pending email for booking #{}", booking.getId(), e);
        }
    }

    @Override
    @Async
    public void sendBookingConfirmedEmail(Booking booking) {
        if (booking.getCustomerEmail() == null || booking.getCustomerEmail().trim().isEmpty()) {
            log.warn("Customer email is missing for booking #{}. Cannot send confirmed email.", booking.getId());
            return;
        }

        try {
            log.info("Sending booking confirmed email to {} for booking #{}", booking.getCustomerEmail(), booking.getId());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("booking", booking);

            String htmlContent = templateEngine.process("emails/booking-confirmed", context);

            helper.setFrom(fromEmail);
            helper.setTo(booking.getCustomerEmail().trim());
            helper.setSubject("Xanh Đi - Xác nhận tour thành công #" + booking.getId());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Successfully sent booking confirmed email for booking #{}", booking.getId());
        } catch (Exception e) {
            log.error("Failed to send booking confirmed email for booking #{}", booking.getId(), e);
        }
    }

    @Override
    @Async
    public void sendBookingCancelledEmail(Booking booking) {
        if (booking.getCustomerEmail() == null || booking.getCustomerEmail().trim().isEmpty()) {
            log.warn("Customer email is missing for booking #{}. Cannot send cancelled email.", booking.getId());
            return;
        }

        try {
            log.info("Sending booking cancelled email to {} for booking #{}", booking.getCustomerEmail(), booking.getId());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("booking", booking);

            String htmlContent = templateEngine.process("emails/booking-cancelled", context);

            helper.setFrom(fromEmail);
            helper.setTo(booking.getCustomerEmail().trim());
            helper.setSubject("Xanh Đi - Thông báo hủy đơn đặt tour #" + booking.getId());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Successfully sent booking cancelled email for booking #{}", booking.getId());
        } catch (Exception e) {
            log.error("Failed to send booking cancelled email for booking #{}", booking.getId(), e);
        }
    }
}
