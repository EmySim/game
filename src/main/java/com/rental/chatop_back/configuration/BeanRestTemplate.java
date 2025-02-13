package com.rental.chatop_back.configuration;  // Place-le dans le package approprié, par exemple 'configuration'

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration  // Cette annotation indique que cette classe contient des définitions de beans.
public class BeanRestTemplate {

    @Bean  // Cette annotation crée un bean RestTemplate.
    public RestTemplate restTemplate() {
        return new RestTemplate();  // Retourne une instance de RestTemplate.
    }
}