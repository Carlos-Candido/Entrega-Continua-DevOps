package br.com.fatecads.fatecads.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.fatecads.fatecads.entity.PasswordResetToken;
import br.com.fatecads.fatecads.entity.Usuario;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update PasswordResetToken token
            set token.usedAt = :usedAt
            where token.usuario = :usuario
              and token.usedAt is null
            """)
    int invalidateActiveTokens(@Param("usuario") Usuario usuario, @Param("usedAt") LocalDateTime usedAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from PasswordResetToken token
            where token.usuario.idUsuario = :idUsuario
            """)
    int deleteByUsuarioId(@Param("idUsuario") Integer idUsuario);
}
