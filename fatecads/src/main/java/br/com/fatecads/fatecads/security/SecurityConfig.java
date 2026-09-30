package br.com.fatecads.fatecads.security;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/",
                                                                "/login",
                                                                "/fatecads",
                                                                "/css/**",
                                                                "/images/**",
                                                                "/usuarios/cadastro",
                                                                "/usuarios/cadastro/salvar",
                                                                "/recuperacao/**")
                                                .permitAll()
                                                // Área da loja: exclusiva de CLIENTE
                                                .requestMatchers("/loja/**").hasRole("CLIENTE")
                                                // Imagem de produto é usada pela vitrine
                                                // da loja: CLIENTE precisa visualizá-la
                                                .requestMatchers("/produtos/imagem/**")
                                                .hasAnyRole("USER", "ADMIN", "CLIENTE")
                                                // Telas administrativas existentes (usuários,
                                                // produtos, pedidos acadêmicos): abertas aos
                                                // perfis de gestão, fechadas a CLIENTE
                                                .requestMatchers(
                                                                "/usuarios/**",
                                                                "/produtos/**",
                                                                "/pedidos/**",
                                                                "/alunos/**",
                                                                "/cursos/**",
                                                                "/professores/**",
                                                                "/disciplinas/**")
                                                .hasAnyRole("USER", "ADMIN")
                                                // Área administrativa: ADMIN não acessa a loja,
                                                // CLIENTE não acessa a administração
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                // Telas administrativas existentes continuam
                                                // exigindo autenticação
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .successHandler(redirecionamentoPorPerfil())
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        // Após o login: CLIENTE vai para a loja, os demais seguem para /home
        private AuthenticationSuccessHandler redirecionamentoPorPerfil() {
                return new AuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
                                boolean isCliente = authentication.getAuthorities().stream()
                                                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
                                response.sendRedirect(isCliente ? "/loja" : "/home");
                        }
                };
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
                        throws Exception {
                return config.getAuthenticationManager();
        }

}
