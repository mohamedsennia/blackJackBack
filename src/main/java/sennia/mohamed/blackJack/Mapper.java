package sennia.mohamed.blackJack;

import sennia.mohamed.blackJack.game.Game;
import sennia.mohamed.blackJack.game.GameDTO;
import sennia.mohamed.blackJack.gamePlayer.GamePlayer;
import sennia.mohamed.blackJack.gamePlayer.GamePlayerDTO;

public class Mapper {
    public static GamePlayerDTO toGamePlayerDTO(GamePlayer gamePlayer){
        return  new GamePlayerDTO(gamePlayer.getPlayer().getUserName(), gamePlayer.getScore(), gamePlayer.getState());
    }
    public static GameDTO toGameDTO(Game game,int player){
        boolean myTurn;
        System.out.println(game.getNexPlayer());
        System.out.println(player);

        if(player==1){
            myTurn= game.getNexPlayer() == game.getPlayer1().getPlayer().getId();
            return new GameDTO(game.getId(), game.getPlayer1(),game.getPlayer2()!=null ?game.getPlayer2().getPlayer():null,game.getStatus(),myTurn);

        }else {

            myTurn= game.getNexPlayer() == game.getPlayer2().getPlayer().getId();
            return new GameDTO(game.getId(), game.getPlayer2(),game.getPlayer1()!=null? game.getPlayer1().getPlayer():null,game.getStatus(),myTurn);

        }
    }
}
