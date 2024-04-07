public class IncorrectActionException extends Exception {
    protected String message;

    public IncorrectActionException(){}

    public IncorrectActionException(String message){
        super(message);
        this.message = message;
    }
}