package sennia.mohamed.blackJack.gamePlayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayerDTO {
    private String userName;
    private int score;
    private GamePlayerState state;
}
