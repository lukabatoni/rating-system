package com.lukaoniani.rating_systems.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//
//    public void sendTempPassword(String email, String tempPassword) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Your Temporary Password");
//        message.setText("Your temporary password is: " + tempPassword +
//                "\nPlease log in and change your password as soon as possible.");
//
//        mailSender.send(message);
//    }
//
//    public void sendApprovalNotification(String email) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Seller Profile Approved");
//        message.setText("Your seller profile has been approved. You can now log in and start using the platform.");
//
//        mailSender.send(message);
//    }
//
//    public void sendVerificationCode(String email, String code) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Email Verification Code");
//        message.setText("Your verification code is: " + code + "This code will expire in 24 hours.");
//
//        mailSender.send(message);
//    }
//
//}
