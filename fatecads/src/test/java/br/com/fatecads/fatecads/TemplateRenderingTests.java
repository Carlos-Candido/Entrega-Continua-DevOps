package br.com.fatecads.fatecads;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@AutoConfigureMockMvc
class TemplateRenderingTests {
    @Autowired MockMvc mvc;

    private MockHttpSession sessionWithRole(String role) {
        var session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", new SecurityContextImpl(
            new UsernamePasswordAuthenticationToken("ui-review", "", List.of(new SimpleGrantedAuthority(role)))));
        return session;
    }

    @Test
    void allScreensRenderWithSharedStylesAndNavigation() throws Exception {
        var session = sessionWithRole("ROLE_USER");
        var routes = List.of("/fatecads", "/login", "/home", "/alunos/listar", "/alunos/criar",
            "/cursos/listar", "/cursos/criar", "/professores/listar", "/professores/criar",
            "/disciplinas/listar", "/disciplinas/criar", "/usuarios/listar", "/usuarios/criar", "/usuarios/cadastro",
            "/produtos/listar", "/produtos/criar", "/pedidos/criar");
        for (var route : routes) {
            var html = mvc.perform(get(route).session(session)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
            assertTrue(html.contains("/css/app.css"), route);
            assertFalse(html.contains("<style>"), route);
            assertFalse(html.contains("th:replace"), route);
            if (!List.of("/login", "/fatecads", "/usuarios/cadastro").contains(route)) {
                assertTrue(html.contains("aria-current=\"page\""), route);
                assertTrue(html.contains("aria-controls=\"main-nav\""), route);
            }
            if (route.equals("/usuarios/cadastro")) {
                assertTrue(html.contains("cadastro-page"), route);
                assertFalse(html.contains("aria-controls=\"main-nav\""), route);
            }
            // Optional export for browser review; only GET requests are made.
            var output = System.getProperty("ui.review.output");
            if (output != null) {
                Files.createDirectories(Path.of(output));
                Files.writeString(Path.of(output, route.substring(1).replace('/', '-') + ".html"), html);
            }
        }
        mvc.perform(get("/css/app.css")).andExpect(status().isOk());
        mvc.perform(get("/images/icons/graduation-cap.svg")).andExpect(status().isOk());
        mvc.perform(get("/images/icons/logout.svg")).andExpect(status().isOk());
    }

    @Test
    void lojaScreensRenderWithSharedStylesForCliente() throws Exception {
        var session = sessionWithRole("ROLE_CLIENTE");
        var routes = List.of("/loja", "/loja/produtos", "/loja/produtos?q=caderno", "/loja/carrinho");
        for (var route : routes) {
            var html = mvc.perform(get(route).session(session)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
            assertTrue(html.contains("/css/app.css"), route);
            assertFalse(html.contains("<style>"), route);
            assertFalse(html.contains("th:replace"), route);
            assertTrue(html.contains("aria-current=\"page\""), route);
            assertTrue(html.contains("id=\"main-nav\""), route);
            assertFalse(html.contains("class=\"sidebar\""), route);
            assertTrue(html.contains("class=\"loja-header\""), route);
            assertTrue(html.contains("id=\"drawer-toggle\""), route);
        }
        mvc.perform(get("/loja/produto/999999").session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/loja/produtos"));
    }

    @Test
    void clienteCannotAccessAdminArea() throws Exception {
        var session = sessionWithRole("ROLE_CLIENTE");
        // Rotas administrativas existentes: CLIENTE recebe 403
        mvc.perform(get("/usuarios/listar").session(session)).andExpect(status().isForbidden());
        mvc.perform(get("/produtos/listar").session(session)).andExpect(status().isForbidden());
        mvc.perform(get("/alunos/listar").session(session)).andExpect(status().isForbidden());
        // Sem autenticação: redirect para o login
        mvc.perform(get("/loja")).andExpect(status().is3xxRedirection());
        // Perfil sem CLIENTE não entra na loja
        var adminSession = sessionWithRole("ROLE_ADMIN");
        mvc.perform(get("/loja").session(adminSession)).andExpect(status().isForbidden());
    }
}
