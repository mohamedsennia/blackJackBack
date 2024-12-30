package sennia.mohamed.blackJack.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
public class Player {
    private  static int counter=0;
    private int id;
    private String userName;
    public Player(String userName){
        Player.counter=Player.counter+1;
        this.id=Player.counter;
        this.userName=userName;
    }
}
