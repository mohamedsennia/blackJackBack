package sennia.mohamed.blackJack.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sennia.mohamed.blackJack.gamePlayer.GamePlayerState;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateUpdate {
    private String updateType;
    private GamePlayerState gamePlayerState;
    private GameStatus gameStatus;

    public StateUpdate(GamePlayerState gamePlayerState, GameStatus gameStatus) {
        this.updateType="stateUpdate";
        this.gamePlayerState = gamePlayerState;
        this.gameStatus = gameStatus;
    }
}
