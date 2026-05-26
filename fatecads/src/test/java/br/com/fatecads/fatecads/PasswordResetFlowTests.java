package br.com.fatecads.fatecads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

import br.com.fatecads.fatecads.entity.PasswordResetToken;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.PasswordResetTokenRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
class PasswordResetFlowTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void requestingPasswordResetCreatesSingleUseTokenForExistingEmail() throws Exception {
        usuarioRepository.save(new Usuario(
                null,
                "Usuario Reset",
                "reset@example.com",
                "usuario.reset",
                passwordEncoder.encode("senhaAntiga123"),
                "ROLE_USER"));

        mockMvc.perform(post("/recuperar-senha").param("email", "reset@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recuperar-senha"));

        assertEquals(1, passwordResetTokenRepository.count());
    }

    @Test
    void requestingPasswordResetForUnknownEmailDoesNotCreateToken() throws Exception {
        mockMvc.perform(post("/recuperar-senha").param("email", "nao-existe@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recuperar-senha"));

        assertEquals(0, passwordResetTokenRepository.count());
    }

    @Test
    void resetPasswordWithValidTokenUpdatesPasswordAndConsumesToken() throws Exception {
        Usuario usuario = usuarioRepository.save(new Usuario(
                null,
                "Usuario Token",
                "token@example.com",
                "usuario.token",
                passwordEncoder.encode("senhaAntiga123"),
                "ROLE_USER"));
        String rawToken = "token-valido-para-teste";
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.save(new PasswordResetToken(
                null,
                usuario,
                hashToken(rawToken),
                LocalDateTime.now().plusMinutes(30),
                LocalDateTime.now(),
                null));

        mockMvc.perform(post("/redefinir-senha")
                        .param("token", rawToken)
                        .param("senha", "novaSenha123")
                        .param("confirmacaoSenha", "novaSenha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Usuario updatedUsuario = usuarioRepository.findById(usuario.getIdUsuario()).orElseThrow();
        PasswordResetToken updatedToken = passwordResetTokenRepository
                .findById(passwordResetToken.getIdPasswordResetToken())
                .orElseThrow();

        assertTrue(passwordEncoder.matches("novaSenha123", updatedUsuario.getSenhaUsuario()));
        assertNotNull(updatedToken.getUsedAt());
    }

    @Test
    void resetPasswordWithExpiredTokenKeepsCurrentPassword() throws Exception {
        Usuario usuario = usuarioRepository.save(new Usuario(
                null,
                "Usuario Expirado",
                "expirado@example.com",
                "usuario.expirado",
                passwordEncoder.encode("senhaAntiga123"),
                "ROLE_USER"));
        String rawToken = "token-expirado-para-teste";
        passwordResetTokenRepository.save(new PasswordResetToken(
                null,
                usuario,
                hashToken(rawToken),
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().minusMinutes(31),
                null));

        mockMvc.perform(post("/redefinir-senha")
                        .param("token", rawToken)
                        .param("senha", "novaSenha123")
                        .param("confirmacaoSenha", "novaSenha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recuperar-senha"));

        Usuario updatedUsuario = usuarioRepository.findById(usuario.getIdUsuario()).orElseThrow();

        assertTrue(passwordEncoder.matches("senhaAntiga123", updatedUsuario.getSenhaUsuario()));
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] tokenHash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenHash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available.", ex);
        }
    }
}
