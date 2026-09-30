package br.com.fatecads.fatecads.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsappService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarCodigo(String telefone, String codigo) {

        Map<String, String> body = new HashMap<>();

        body.put("phone", telefone);
        body.put(
            "message",
            "Seu código para redefinição de senha é: " + codigo
        );

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(body);

        restTemplate.postForEntity(
                "http://localhost:3000/send",
                request,
                String.class
        );
    }
}