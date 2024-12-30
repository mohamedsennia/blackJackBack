package sennia.mohamed.blackJack.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sennia.mohamed.blackJack.card.Card;
import sennia.mohamed.blackJack.player.Player;

import java.util.List;

@Controller
@RequestMapping("/games")
public class GameSocketsController {
    private GameService gameService;

    @Autowired
    private GameSocketsController(GameService gameService){
        this.gameService=gameService;

    }
    @MessageMapping("/joinGame")
    public void joinGame(@Payload Player player, SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username", player.getUserName());
        this.gameService.joinGame(player);
    }
    @MessageMapping("/draw")
    public void draw(@Payload ActionRequest actionRequest){


            this.gameService.draw(actionRequest.getPlayerId(), actionRequest.getGameId());


    }
    @MessageMapping("/fold")
    public void fold(@Payload ActionRequest actionRequest){
        this.gameService.fold(actionRequest.getPlayerId(),actionRequest.getGameId());
    }

}
