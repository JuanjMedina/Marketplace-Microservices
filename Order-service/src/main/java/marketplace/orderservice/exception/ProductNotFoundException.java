package marketplace.orderservice.exception;

public class ProductNotFoundException extends OrderException {
    public ProductNotFoundException(String productId) {
        super("Product not found with ID: " + productId);
    }
}
