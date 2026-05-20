package br.com.fatecads.fatecads.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.Curso;
import br.com.fatecads.fatecads.entity.Disciplina;
import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.AlunoRepository;
import br.com.fatecads.fatecads.repository.CursoRepository;
import br.com.fatecads.fatecads.repository.DisciplinaRepository;
import br.com.fatecads.fatecads.repository.ProfessorRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;

@Component
@ConditionalOnProperty(name = "fatecads.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements ApplicationRunner {

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private final ProfessorRepository professorRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final CursoRepository cursoRepository;
    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            ProfessorRepository professorRepository,
            DisciplinaRepository disciplinaRepository,
            CursoRepository cursoRepository,
            AlunoRepository alunoRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.professorRepository = professorRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.cursoRepository = cursoRepository;
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<Professor> professores = seedProfessores();
        List<Disciplina> disciplinas = seedDisciplinas(professores);
        List<Curso> cursos = seedCursos(disciplinas);
        seedAlunos(cursos);
        seedUsuarios();
    }

    private List<Professor> seedProfessores() {
        if (professorRepository.count() == 0) {
            professorRepository.saveAll(List.of(
                    new Professor(null, "Ana Souza", "11988887777", "Mestrado", "RM001"),
                    new Professor(null, "Carlos Lima", "11977776666", "Doutorado", "RM002")));
        }

        return professorRepository.findAll();
    }

    private List<Disciplina> seedDisciplinas(List<Professor> professores) {
        if (disciplinaRepository.count() == 0 && !professores.isEmpty()) {
            disciplinaRepository.saveAll(List.of(
                    new Disciplina(null, "DevOps", "DEV", 80, professores.get(0)),
                    new Disciplina(null, "Programacao Web", "PWEB", 80, professores.get(1 % professores.size()))));
        }

        return disciplinaRepository.findAll();
    }

    private List<Curso> seedCursos(List<Disciplina> disciplinas) {
        if (cursoRepository.count() == 0 && !disciplinas.isEmpty()) {
            cursoRepository.saveAll(List.of(
                    new Curso(null, "ADS", "Noturno", 2800, disciplinas.get(0)),
                    new Curso(null, "Gestao de TI", "Matutino", 2400, disciplinas.get(1 % disciplinas.size()))));
        }

        return cursoRepository.findAll();
    }

    private void seedAlunos(List<Curso> cursos) {
        if (alunoRepository.count() == 0 && !cursos.isEmpty()) {
            alunoRepository.saveAll(List.of(
                    new Aluno(null, "Mariana Alves", "mariana.alves@example.com", "11999990001",
                            "Rua das Flores, 100", "12345678901", "RA2026001", cursos.get(0)),
                    new Aluno(null, "Joao Pereira", "joao.pereira@example.com", "11999990002",
                            "Avenida Brasil, 200", "12345678902", "RA2026002", cursos.get(1 % cursos.size())),
                    new Aluno(null, "Beatriz Santos", "beatriz.santos@example.com", "11999990003",
                            "Rua Central, 300", "12345678903", "RA2026003", cursos.get(0))));
        }
    }

    private void seedUsuarios() {
        usuarioRepository.findByLoginUsuario(ADMIN_LOGIN)
                .orElseGet(() -> usuarioRepository.save(new Usuario(
                        null,
                        "Administrador",
                        "admin@fatecads.com.br",
                        ADMIN_LOGIN,
                        passwordEncoder.encode(ADMIN_PASSWORD),
                        "ROLE_ADMIN")));
    }
}
