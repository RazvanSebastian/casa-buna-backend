package ro.casabuna.casabuna_backend.exception;

public class ResourceConditionUnsatisfiedException extends RuntimeException {
    public ResourceConditionUnsatisfiedException(String message) {
        super(message);
    }
}
