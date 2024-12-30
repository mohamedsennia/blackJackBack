package sennia.mohamed.blackJack.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor

public class SocketErrorMessage {
    private String error;
}
