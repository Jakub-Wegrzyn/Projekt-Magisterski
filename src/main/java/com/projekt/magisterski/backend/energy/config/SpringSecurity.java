package com.projekt.magisterski.backend.energy.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Properties;


@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(new MvcRequestMatcher(introspector, "/")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/login")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/registration")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/dashboard")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/admin")).hasRole("ADMIN")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/newcustomer")).hasRole("ADMIN")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/odczyty/**")).hasRole("ADMIN")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/listofcustomers/**")).hasRole("ADMIN")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/update-single-customer/**")).hasRole("ADMIN")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/faktury/**")).hasRole("ADMIN")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/customer/**")).hasRole("USER")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/customerdata/**")).hasRole("USER")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/counterdata/**")).hasRole("USER")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/diagrams/**")).hasRole("USER")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/customerinvoices/**")).hasRole("USER")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/kontakt/**")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/regulamin")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/correctmeasure/**")).hasRole("ADMIN")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/css/css-all/**")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/css/css-user/**")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/css/css-admin/**")).hasRole("ADMIN")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/js/js-customer/**")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/js/js-admin/**")).hasRole("ADMIN")
                                .requestMatchers(new MvcRequestMatcher(introspector, "/js/js-admin/**")).hasRole("ADMIN")

                )
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/dashboard")
                                .permitAll()
                )
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );

        return http.build();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
