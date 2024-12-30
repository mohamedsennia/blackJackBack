package sennia.mohamed.blackJack.player;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Service
public class PlayerService {
    private Map<String,Player> players;
    public PlayerService(){
       this.players =new HashMap<String,Player>();
    }
    public Player addPlayer(Player player){
        if(this.players.get(player.getUserName())==null){
            Player newPlayer=new Player(player.getUserName());
            this.players.put(player.getUserName(),newPlayer);
            return newPlayer;
        }else{
            return null;
        }
    }
}
