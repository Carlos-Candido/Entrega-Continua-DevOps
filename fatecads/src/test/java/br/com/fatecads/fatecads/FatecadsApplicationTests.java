package br.com.fatecads.fatecads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.fatecads.fatecads.repository.AlunoRepository;
import br.com.fatecads.fatecads.repository.CursoRepository;
import br.com.fatecads.fatecads.repository.DisciplinaRepository;
import br.com.fatecads.fatecads.repository.ProfessorRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class FatecadsApplicationTests {

	@Autowired
	private ProfessorRepository professorRepository;

	@Autowired
	private DisciplinaRepository disciplinaRepository;

	@Autowired
	private CursoRepository cursoRepository;

	@Autowired
	private AlunoRepository alunoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
	}

	@Test
	void seedsInitialData() {
		assertEquals(2, professorRepository.count());
		assertEquals(2, disciplinaRepository.count());
		assertEquals(2, cursoRepository.count());
		assertEquals(3, alunoRepository.count());

		var admin = usuarioRepository.findByLoginUsuario("admin").orElseThrow();
		assertTrue(passwordEncoder.matches("admin123", admin.getSenhaUsuario()));
	}

}
