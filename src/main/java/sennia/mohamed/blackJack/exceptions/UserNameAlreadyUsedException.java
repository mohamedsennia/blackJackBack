package sennia.mohamed.blackJack.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserNameAlreadyUsedException extends RuntimeException{
    public UserNameAlreadyUsedException(String message){
        super(message);
    }

}
