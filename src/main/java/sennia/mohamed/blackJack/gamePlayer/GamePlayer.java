package sennia.mohamed.blackJack.gamePlayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import sennia.mohamed.blackJack.card.Card;
import sennia.mohamed.blackJack.player.Player;

import java.util.List;

@Data
@AllArgsConstructor
public class GamePlayer {
    private Player player;
    private int score;
    private GamePlayerState state;
    private boolean drawnAce;
    private List<Card> drawnCards;
    public int updateScore(int points){
        this.score=points;
        return this.score;
    }
    public void drawCard(Card card){
        this.drawnCards.add(card);
    }
}
