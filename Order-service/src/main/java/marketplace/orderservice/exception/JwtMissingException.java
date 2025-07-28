package marketplace.orderservice.exception;

/**
 * Excepción específica para cuando no se proporciona token JWT.
 */
public class JwtMissingException extends AuthenticationException {

    public JwtMissingException(String message) {
        super(message);
    }

    public JwtMissingException() {
        super("Token de autenticación requerido");
    }
}
