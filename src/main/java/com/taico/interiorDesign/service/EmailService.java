package com.taico.interiorDesign.service;


public interface EmailService {

    void sendContactMessage(
            String name,
            String email,
            String subject,
            String message
    );


    void sendRegistrationEmail(
            String email,
            String name
    );
}
