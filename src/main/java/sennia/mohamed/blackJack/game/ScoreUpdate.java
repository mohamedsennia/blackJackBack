package sennia.mohamed.blackJack.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sennia.mohamed.blackJack.card.Card;
import sennia.mohamed.blackJack.gamePlayer.GamePlayerState;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreUpdate {
   private String updateType;
   private Card carDrawn;
   private int newScore;
   private GameStatus gameStatus;
   private GamePlayerState gamePlayerState;
   private boolean DrawnIce;

   public ScoreUpdate(Card carDrawn, int newScore, GameStatus gameStatus, GamePlayerState gamePlayerState, boolean drawnIce) {
      this.updateType="scoreUpdate";
      this.carDrawn = carDrawn;
      this.newScore = newScore;
      this.gameStatus = gameStatus;
      this.gamePlayerState = gamePlayerState;
      this.DrawnIce = drawnIce;
   }
}
