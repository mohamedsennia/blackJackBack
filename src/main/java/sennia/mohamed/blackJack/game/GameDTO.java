package sennia.mohamed.blackJack.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sennia.mohamed.blackJack.gamePlayer.GamePlayer;
import sennia.mohamed.blackJack.gamePlayer.GamePlayerDTO;
import sennia.mohamed.blackJack.player.Player;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    int id;
    GamePlayer gamePlayer1;
    Player gamePlayer2;
    private GameStatus status;
    private boolean myturn;
}
