//package com.letssoccer.letssoccer.email;
//
//import com.letssoccer.letssoccer.email.EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/email")
//public class EmailController {
//
//    @Autowired
//    private EmailService emailService;
//
//    @PostMapping("/enviar")
//    public String enviarEmail() {
//        emailService.enviarEmail(
//                "alanpaullo@yahoo.com.br",
//                "Bem-vindo!",
//                "Olá, seu cadastro foi realizado com sucesso."
//        );
//        return "Email enviado com sucesso!";
//    }
//}