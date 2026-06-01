package br.com.fatecads.fatecads;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.Curso;
import br.com.fatecads.fatecads.entity.Disciplina;
import br.com.fatecads.fatecads.entity.PasswordResetToken;
import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.AlunoRepository;
import br.com.fatecads.fatecads.repository.CursoRepository;
import br.com.fatecads.fatecads.repository.DisciplinaRepository;
import br.com.fatecads.fatecads.repository.PasswordResetTokenRepository;
import br.com.fatecads.fatecads.repository.ProfessorRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
class FatecadsWebTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void professorPagesRender() throws Exception {
        mockMvc.perform(get("/professor/listar"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/professor/criar"))
                .andExpect(status().isOk());
    }

    @Test
    void disciplinaPagesRender() throws Exception {
        mockMvc.perform(get("/disciplina/listar"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/disciplina/criar"))
                .andExpect(status().isOk());
    }

    @Test
    void alunoPagesRender() throws Exception {
        mockMvc.perform(get("/alunos/listar"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/alunos/criar"))
                .andExpect(status().isOk());
    }

    @Test
    void cursoPagesRender() throws Exception {
        mockMvc.perform(get("/curso/listar"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/curso/criar"))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioPagesRender() throws Exception {
        mockMvc.perform(get("/usuario/listar"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/usuario/criar"))
                .andExpect(status().isOk());
    }

    @Test
    void savesAlunoWithCurso() throws Exception {
        Curso curso = cursoRepository.save(new Curso(null, "ADS", "Noturno", 2800, null));

        mockMvc.perform(post("/alunos/salvar")
                        .param("nomeAluno", "Aluno Teste")
                        .param("emailAluno", "aluno.teste@example.com")
                        .param("telefoneAluno", "11999999999")
                        .param("cpfAluno", "12345678901")
                        .param("enderecoAluno", "Rua Teste")
                        .param("raAluno", "RA001")
                        .param("curso", curso.getIdCurso().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alunos/listar"));

        assertTrue(alunoRepository.findAll().stream()
                .anyMatch(aluno -> aluno.getCurso() != null
                        && curso.getIdCurso().equals(aluno.getCurso().getIdCurso())));
    }

    @Test
    void deletesAluno() throws Exception {
        Aluno aluno = alunoRepository.save(new Aluno(
                null,
                "Aluno Remover",
                "aluno.remover@example.com",
                "11999999999",
                "Rua Teste",
                "12345678999",
                "RA999",
                null));

        mockMvc.perform(get("/alunos/excluir/" + aluno.getIdAluno()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alunos/listar"));

        assertFalse(alunoRepository.existsById(aluno.getIdAluno()));
    }

    @Test
    void savesCursoWithDisciplina() throws Exception {
        Disciplina disciplina = disciplinaRepository.save(
                new Disciplina(null, "DevOps", "DEV", 80, null));

        mockMvc.perform(post("/curso/salvar")
                        .param("nomeCurso", "ADS")
                        .param("periodo", "Noturno")
                        .param("cargaHoraria", "2800")
                        .param("disciplina", disciplina.getIdDisciplina().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curso/listar"));

        assertTrue(cursoRepository.findAll().stream()
                .anyMatch(curso -> curso.getDisciplina() != null
                        && disciplina.getIdDisciplina().equals(curso.getDisciplina().getIdDisciplina())));
    }

    @Test
    void savesDisciplinaWithProfessor() throws Exception {
        Professor professor = professorRepository.save(
                new Professor(null, "Professor Teste", "11999999999", "Mestrado", "RM001"));

        mockMvc.perform(post("/disciplina/salvar")
                        .param("nomeDisciplina", "DevOps")
                        .param("siglaDisciplina", "DEV")
                        .param("cargaHorariaDisciplina", "80")
                        .param("professor", professor.getIdProfessor().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/disciplina/listar"));

        assertTrue(disciplinaRepository.findAll().stream()
                .anyMatch(disciplina -> disciplina.getProfessor() != null
                        && professor.getIdProfessor().equals(disciplina.getProfessor().getIdProfessor())));
    }

    @Test
    void savesUsuario() throws Exception {
        mockMvc.perform(post("/usuario/salvar")
                        .param("nomeUsuario", "Usuario Teste")
                        .param("emailUsuario", "usuario.teste@example.com")
                        .param("loginUsuario", "usuario.teste")
                        .param("senhaUsuario", "senha123")
                        .param("role", "ROLE_ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/listar"));

        assertTrue(usuarioRepository.findAll().stream()
                .anyMatch(usuario -> "usuario.teste".equals(usuario.getLoginUsuario())));
    }

    @Test
    void deletesUsuarioWithPasswordResetToken() throws Exception {
        Usuario usuario = usuarioRepository.save(new Usuario(
                null,
                "Usuario Remover",
                "usuario.remover@example.com",
                "usuario.remover",
                "senha123",
                "ROLE_USER"));
        PasswordResetToken resetToken = passwordResetTokenRepository.save(new PasswordResetToken(
                null,
                usuario,
                "hash-usuario-remover",
                LocalDateTime.now().plusMinutes(30),
                LocalDateTime.now(),
                null));

        mockMvc.perform(get("/usuario/excluir/" + usuario.getIdUsuario()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario/listar"));

        assertFalse(usuarioRepository.existsById(usuario.getIdUsuario()));
        assertFalse(passwordResetTokenRepository.existsById(resetToken.getIdPasswordResetToken()));
    }
}
