package marketplace.orderservice.exception;

/**
 * Excepción específica para tokens JWT expirados.
 */
public class JwtExpiredException extends AuthenticationException {

    private final String expiredAt;

    public JwtExpiredException(String message, String expiredAt) {
        super(message);
        this.expiredAt = expiredAt;
    }

    public JwtExpiredException(String message) {
        super(message);
        this.expiredAt = null;
    }

    public String getExpiredAt() {
        return expiredAt;
    }
}
