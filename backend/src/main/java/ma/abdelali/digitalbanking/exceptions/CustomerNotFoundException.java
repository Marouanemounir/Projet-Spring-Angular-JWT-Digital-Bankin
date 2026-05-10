package ma.abdelali.digitalbanking.exceptions;

public class CustomerNotFoundException extends ResourceNotFoundException {
    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with id: " + customerId);
    }

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
