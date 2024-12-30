package sennia.mohamed.blackJack.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNameAlreadyUsedException.class)
    public ResponseEntity<String> handleUserNameAlreadyUsed(UserNameAlreadyUsedException userNameAlreadyUsedException){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(userNameAlreadyUsedException.getMessage());
    }

}
