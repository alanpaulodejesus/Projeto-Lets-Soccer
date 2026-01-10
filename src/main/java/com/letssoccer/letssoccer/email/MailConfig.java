//package com.letssoccer.letssoccer.email;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
//@Configuration
//public class MailConfig {
//
//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl sender = new JavaMailSenderImpl();
//        sender.setHost("smtp.mail.yahoo.com");
//        sender.setPort(587);
//        sender.setUsername("alanpaullo@yahoo.com.br");
//        sender.setPassword("abcd-efgh-ijkl-mnop");
//
//        Properties props = sender.getJavaMailProperties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//
//        return sender;
//    }
//}
