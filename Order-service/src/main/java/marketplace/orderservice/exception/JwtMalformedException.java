package marketplace.orderservice.exception;

/**
 * Excepción específica para tokens JWT malformados o inválidos.
 */
public class JwtMalformedException extends AuthenticationException {

    private final String tokenType;

    public JwtMalformedException(String message, String tokenType) {
        super(message);
        this.tokenType = tokenType;
    }

    public JwtMalformedException(String message) {
        super(message);
        this.tokenType = "JWT";
    }

    public String getTokenType() {
        return tokenType;
    }
}
