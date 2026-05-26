package br.com.fatecads.fatecads.exception;

public class InvalidPasswordResetTokenException extends RuntimeException {
    public InvalidPasswordResetTokenException() {
        super("Token de recuperacao de senha invalido ou expirado.");
    }
}
