package sennia.mohamed.blackJack.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sennia.mohamed.blackJack.exceptions.UserNameAlreadyUsedException;

@RestController
@RequestMapping("/api/Players")
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @PostMapping
    public Player addPlayer(@RequestBody Player player){
        Player newPlayer=this.playerService.addPlayer(player);
       if (newPlayer!=null){
           return  newPlayer;
       }
       throw  new UserNameAlreadyUsedException("This user Name is already used, please choose another one");
    }
}
