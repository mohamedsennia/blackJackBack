package sennia.mohamed.blackJack.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sennia.mohamed.blackJack.card.Card;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameRestController {
    private GameService gameService;
    @Autowired
    public GameRestController(GameService gameService){
        this.gameService=gameService;
    }
    @GetMapping("/opponentCards/{gameId}/{playerId}")
    public List<Card> getOpponentCards(@PathVariable int gameId,@PathVariable int playerId){
        return    this.gameService.getOpponentCards(gameId,playerId);
    }
}
