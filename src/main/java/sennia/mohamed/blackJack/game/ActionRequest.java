package sennia.mohamed.blackJack.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionRequest {
    private int playerId;
    private int gameId;
}
