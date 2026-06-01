package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.PasswordResetTokenRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public Usuario save(Usuario usuario) {
        usuario.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public void deleteById(Integer id) {
        passwordResetTokenRepository.deleteByUsuarioId(id);
        usuarioRepository.deleteById(id);
    }

    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }
}
