package ma.abdelali.digitalbanking.exceptions;

public class BankAccountNotFoundException extends ResourceNotFoundException {
    public BankAccountNotFoundException(String accountId) {
        super("Bank account not found with id: " + accountId);
    }
}
