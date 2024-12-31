package sennia.mohamed.blackJack.game;

import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import sennia.mohamed.blackJack.Mapper;
import sennia.mohamed.blackJack.card.Card;
import sennia.mohamed.blackJack.card.CardRank;
import sennia.mohamed.blackJack.exceptions.SocketErrorMessage;
import sennia.mohamed.blackJack.gamePlayer.GamePlayer;
import sennia.mohamed.blackJack.gamePlayer.GamePlayerState;
import sennia.mohamed.blackJack.player.Player;

import java.util.*;

@Service
public class GameService {
    private Map<Integer,Game> games;
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    public GameService(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate=simpMessagingTemplate;
        this.games=new HashMap<Integer,Game>();
    }

    public void joinGame(Player p){
        GamePlayer gamePlayer=new GamePlayer(p,0, GamePlayerState.STOOD,false,new ArrayList<>());
        Iterator<Game> iterator = this.games.values().iterator();
        Game game = null;
        while (iterator.hasNext()) {
            game = iterator.next();
        }
        if(game!=null&&game.getStatus()==GameStatus.WAITING){
            game.setPlayer2(gamePlayer);
            System.out.println("Game Started between by "+game.getPlayer2().getPlayer().getUserName()+" And "+game.getPlayer1().getPlayer().getUserName());
            game.setStatus(GameStatus.ONGOING);
            System.out.println("SENDING MESSAGES TO "+String.valueOf(game.getPlayer1().getPlayer().getId())+" AND "+game.getPlayer2().getPlayer().getId() );
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(game.getPlayer1().getPlayer().getId()),
                    "/topic/games",
                    Mapper.toGameDTO(game,1)
            );
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(game.getPlayer2().getPlayer().getId()),
                    "/topic/games",
                    Mapper.toGameDTO(game,2)
            );


        }else{

            game=new Game(gamePlayer);

            this.games.put(game.getId(),game);
            System.out.println("Game created by "+p.getUserName());


        }

    }
    public void draw(int playerId,int gameId){

        Iterator<Game> iterator = this.games.values().iterator();
        Game game;
        game=this.games.get(gameId);
        // Unavlaible Game
        if(!game.getStatus().equals(GameStatus.ONGOING)){
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(playerId),
                    "/topic/errors",
                    new SocketErrorMessage("This game isn't avaliable")

            );
            return;
        }
        /* PLAYER OUT OF HIS TURN*/
        if(playerId!= game.getNexPlayer()){

            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(playerId),
                    "/topic/errors",
                    new SocketErrorMessage("You should wait for your turn")

            );
            return ;
        }
        //first player is playing
        if(game.getPlayer1().getPlayer().getId()==playerId){
            // player already folded
            if(game.getPlayer1().getState()==GamePlayerState.FOLDED){
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/games",
                        new SocketErrorMessage("You've already folded you can't draw anymore")

                );
                return;
            }
            Card card= game.draw();
            game.getPlayer1().drawCard(card);
            int newScore = game.getPlayer1().getScore();
            //player drew an ACE
           if(card.getCardRank()== CardRank.ACE){
               // already has an ACE BEFORE THIS ONE
               if(game.getPlayer1().isDrawnAce()){
               newScore =newScore+1;
               }
              if(newScore+11==21){
                  newScore=21;
                  game.getPlayer1().setDrawnAce(false);
              }else{
                  if(newScore+11>21){
                      newScore=newScore+1;
                      game.getPlayer1().setDrawnAce(false);
                  }else{
                      game.getPlayer1().setDrawnAce(true);
                  }
              }

           }
           //draw card other than ace
           else{
               // didn"t draw an ace before
               if(!game.getPlayer1().isDrawnAce()){
                   newScore =newScore+card.getCardRank().getValue();
               }
               else{
                   //draw an ice before and it would be better counted as an 11
                   if(newScore+11+card.getCardRank().getValue()==21){
                       newScore=21;
                   }
                   //draw an ice before and it would be better counted as a 1
                   else if (newScore+11+card.getCardRank().getValue()>21) {
                       newScore=newScore+card.getCardRank().getValue()+1;
                       game.getPlayer1().setDrawnAce(false);
                   }
                   //draw an ice before and we still don't know the best case for it
                   else {
                       newScore =newScore+card.getCardRank().getValue();
                   }
               }

           }

          game.getPlayer1().updateScore(newScore);

           // score > 21 (loss)
          if(newScore>21){
              game.setStatus(GameStatus.FINISHED);
              game.getPlayer1().setState(GamePlayerState.BUSTED);
              this.simpMessagingTemplate.convertAndSendToUser(
                      String.valueOf(playerId),
                      "/topic/updates",
                      new ScoreUpdate(card,newScore,game.getStatus(),game.getPlayer1().getState(),game.getPlayer1().isDrawnAce())
              );
              this.simpMessagingTemplate.convertAndSendToUser(
                      String.valueOf(game.getPlayer2().getPlayer().getId()),
                      "/topic/updates",
                      new StateUpdate(game.getPlayer1().getState(),game.getStatus())
              );
              return;
          }
          // score exactly 21
          if(newScore==21){


             //since player 1 started, the player 2 gets the chance to draw another card
            }
            //still playing
            if(game.getPlayer2().getState()==GamePlayerState.FOLDED && newScore>game.getPlayer2().getScore()){
                game.setStatus(GameStatus.FINISHED);
                game.getPlayer1().setState(GamePlayerState.WON);
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/updates",
                        new ScoreUpdate(card,newScore,game.getStatus(),game.getPlayer1().getState(),game.getPlayer1().isDrawnAce())
                );
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(game.getPlayer2().getPlayer().getId()),
                        "/topic/updates",
                        new StateUpdate(game.getPlayer1().getState(),game.getStatus())
                );
                return;
            }
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(playerId),
                    "/topic/updates",
                    new ScoreUpdate(card,newScore,game.getStatus(),game.getPlayer1().getState(),game.getPlayer1().isDrawnAce())
            );
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(game.getPlayer2().getPlayer().getId()),
                    "/topic/updates",
                    new StateUpdate(game.getPlayer1().getState(),game.getStatus())
            );
          // if the other playing still playing we pass the the tour else we keep it
            if(!game.getPlayer2().getState().equals(GamePlayerState.FOLDED)){
                game.setNexPlayer(game.getPlayer2().getPlayer().getId());
            }
        }
        //second player is playing
        else if (game.getPlayer2().getPlayer().getId()==playerId) {

            if(game.getPlayer2().getState()==GamePlayerState.FOLDED){
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/errors",
                        new SocketErrorMessage("You've already folded you can't draw anymore")

                );
                return;
            }
            Card card= game.draw();
            game.getPlayer2().drawCard(card);
            int newScore = game.getPlayer2().getScore();
            if(card.getCardRank()== CardRank.ACE){
                if(game.getPlayer2().isDrawnAce()){
                    newScore =newScore+1;
                }
                game.getPlayer2().setDrawnAce(true);
            }
            else{
                if(!game.getPlayer2().isDrawnAce()){
                    newScore =newScore+card.getCardRank().getValue();
                }
                else{
                    if(newScore+11+card.getCardRank().getValue()==21){
                        newScore=21;
                    }
                    else if (newScore+11+card.getCardRank().getValue()>21) {
                        newScore=newScore+card.getCardRank().getValue()+1;
                        game.getPlayer2().setDrawnAce(false);
                    }
                    else {
                        newScore =newScore+card.getCardRank().getValue();
                    }
                }

            }
            game.getPlayer2().updateScore(newScore);

            if(newScore>21){
                game.setStatus(GameStatus.FINISHED);
                game.getPlayer2().setState(GamePlayerState.BUSTED);
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/updates",
                        new ScoreUpdate(card,newScore,game.getStatus(),game.getPlayer2().getState(),game.getPlayer2().isDrawnAce())
                );
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(game.getPlayer1().getPlayer().getId()),
                        "/topic/updates",
                        new StateUpdate(game.getPlayer2().getState(),game.getStatus())
                );
                return;
            }
            if(newScore==21){
                if(game.getPlayer1().getScore()==21){
                    game.setStatus(GameStatus.FINISHED);
                    game.getPlayer2().setState(GamePlayerState.TIED);

                    this.simpMessagingTemplate.convertAndSendToUser(
                            String.valueOf(playerId),
                            "/topic/updates",
                            new ScoreUpdate(card,newScore,game.getStatus(),game.getPlayer2().getState(),game.getPlayer2().isDrawnAce())
                    );
                    this.simpMessagingTemplate.convertAndSendToUser(
                            String.valueOf(game.getPlayer1().getPlayer().getId()),
                            "/topic/updates",
                            new StateUpdate(game.getPlayer2().getState(),game.getStatus())
                    );
                }else{
                    game.setStatus(GameStatus.FINISHED);
                    game.getPlayer2().setState(GamePlayerState.WON);
                    this.simpMessagingTemplate.convertAndSendToUser(
                            String.valueOf(playerId),
                            "/topic/updates",
                            new ScoreUpdate(card,newScore,game.getStatus(),game.getPlayer2().getState(),game.getPlayer2().isDrawnAce())
                    );
                    this.simpMessagingTemplate.convertAndSendToUser(
                            String.valueOf(game.getPlayer1().getPlayer().getId()),
                            "/topic/updates",
                            new StateUpdate(game.getPlayer2().getState(),game.getStatus())
                    );
                }
                return;
            }
            if(game.getPlayer1().getScore()==21){
                game.setStatus(GameStatus.FINISHED);
                game.getPlayer2().setState(GamePlayerState.BUSTED);
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/updates",
                        new ScoreUpdate(card,newScore,game.getStatus(),game.getPlayer2().getState(),game.getPlayer2().isDrawnAce())
                );
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(game.getPlayer1().getPlayer().getId()),
                        "/topic/updates",
                        new StateUpdate(game.getPlayer2().getState(),game.getStatus())
                );
            }
            if(game.getPlayer1().getState()==GamePlayerState.FOLDED && newScore>game.getPlayer1().getScore()){
                game.setStatus(GameStatus.FINISHED);
                game.getPlayer2().setState(GamePlayerState.WON);
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/updates",
                        new ScoreUpdate(card,newScore,game.getStatus(),game.getPlayer2().getState(),game.getPlayer2().isDrawnAce())
                );
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(game.getPlayer1().getPlayer().getId()),
                        "/topic/updates",
                        new StateUpdate(game.getPlayer2().getState(),game.getStatus())
                );
                return;
            }
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(playerId),
                    "/topic/updates",
                    new ScoreUpdate(card,newScore,game.getStatus(),game.getPlayer2().getState(),game.getPlayer2().isDrawnAce())
            );
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(game.getPlayer1().getPlayer().getId()),
                    "/topic/updates",
                    new StateUpdate(game.getPlayer2().getState(),game.getStatus())
            );
            if(!game.getPlayer1().getState().equals(GamePlayerState.FOLDED)){
                game.setNexPlayer(game.getPlayer1().getPlayer().getId());
            }

        }

    }
    public void fold(int playerId,int gameId){
        Game game=this.games.get(gameId);
        if(playerId!= game.getNexPlayer()){

            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(playerId),
                    "/topic/errors",
                    new SocketErrorMessage("You should wait for your turn")

            );
            return ;
        }
        if(game.getPlayer1().getPlayer().getId()==playerId){
            if(game.getPlayer1().getState()==GamePlayerState.FOLDED){

                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/errors",
                        new SocketErrorMessage("You already folded")
                );
                return;
            }
            if(game.getPlayer1().getState()==GamePlayerState.BUSTED){
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/errors",
                        new SocketErrorMessage("You can't fold when you're already busted")
                );
                return;
            }
            if(game.getPlayer2().getState()==GamePlayerState.FOLDED){
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/errors",
                        new SocketErrorMessage("You can't fold when the other player has already folded")
                );
                return;
            }
            game.getPlayer1().setState(GamePlayerState.FOLDED);
            game.setNexPlayer(game.getPlayer1().getPlayer().getId());
            if(game.getPlayer1().isDrawnAce()){
                int oldScore=game.getPlayer1().getScore();
                if((oldScore+10)<=21){
                    game.getPlayer1().setScore(oldScore+10);
                }else {
                    game.getPlayer1().setScore(oldScore+1);
                }
            }
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(playerId),
                    "/topic/updates",
                    new ScoreUpdate(null,game.getPlayer1().getScore(),game.getStatus(),game.getPlayer1().getState(),game.getPlayer1().isDrawnAce())
            );
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(game.getPlayer2().getPlayer().getId()),
                    "/topic/updates",
                    new StateUpdate(game.getPlayer1().getState(),game.getStatus())
            );
            if(game.getPlayer2().getScore()>game.getPlayer1().getScore()){
                game.setStatus(GameStatus.FINISHED);
                game.getPlayer1().setState(GamePlayerState.BUSTED);
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/updates",
                        new ScoreUpdate(null,game.getPlayer1().getScore(),game.getStatus(),game.getPlayer1().getState(),game.getPlayer1().isDrawnAce())
                );
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(game.getPlayer2().getPlayer().getId()),
                        "/topic/updates",
                        new StateUpdate(game.getPlayer1().getState(),game.getStatus())
                );
            }


        }
        else if (game.getPlayer2().getPlayer().getId()==playerId) {
            if(game.getPlayer2().getState()==GamePlayerState.FOLDED){

                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/errors",
                        new SocketErrorMessage("You already folded")
                );
                return;
            }
            if(game.getPlayer2().getState()==GamePlayerState.BUSTED){
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/errors",
                        new SocketErrorMessage("You can't fold when you're already busted")
                );
                return;
            }
            if(game.getPlayer1().getState()==GamePlayerState.FOLDED){
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/errors",
                        new SocketErrorMessage("You can't fold when the other player has already folded")
                );
                return;
            }
            game.getPlayer2().setState(GamePlayerState.FOLDED);
            game.setNexPlayer(game.getPlayer1().getPlayer().getId());
            if(game.getPlayer2().isDrawnAce()){
                int oldScore=game.getPlayer2().getScore();
                if((oldScore+10)<=21){
                    game.getPlayer2().setScore(oldScore+10);
                }else {
                    game.getPlayer2().setScore(oldScore+1);
                }
            }
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(playerId),
                    "/topic/updates",
                    new ScoreUpdate(null,game.getPlayer2().getScore(),game.getStatus(),game.getPlayer2().getState(),game.getPlayer2().isDrawnAce())
            );
            this.simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(game.getPlayer1().getPlayer().getId()),
                    "/topic/updates",
                    new StateUpdate(game.getPlayer2().getState(),game.getStatus())
            );
            if(game.getPlayer1().getScore()>game.getPlayer2().getScore()){
                game.setStatus(GameStatus.FINISHED);
                game.getPlayer2().setState(GamePlayerState.BUSTED);
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(playerId),
                        "/topic/updates",
                        new ScoreUpdate(null,game.getPlayer2().getScore(),game.getStatus(),game.getPlayer2().getState(),game.getPlayer2().isDrawnAce())
                );
                this.simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(game.getPlayer1().getPlayer().getId()),
                        "/topic/updates",
                        new StateUpdate(game.getPlayer2().getState(),game.getStatus())
                );
            }
        }
    }

    public List<Card> getOpponentCards(int gameId, int playerId) {
        Game game=this.games.get(gameId);
        if(game.getStatus().equals(GameStatus.FINISHED)){

            if(game.getPlayer1().getPlayer().getId()==playerId){
                return game.getPlayer2().getDrawnCards();
            }else if(game.getPlayer2().getPlayer().getId()==playerId) {
                return game.getPlayer1().getDrawnCards();
            }
        }
        return null;
    }
}


