package sennia.mohamed.blackJack.game;

import lombok.Data;
import sennia.mohamed.blackJack.card.Card;
import sennia.mohamed.blackJack.card.CardRank;
import sennia.mohamed.blackJack.card.CardSuit;
import sennia.mohamed.blackJack.gamePlayer.GamePlayer;
import sennia.mohamed.blackJack.gamePlayer.GamePlayerState;
import sennia.mohamed.blackJack.player.Player;

import java.util.*;

@Data
public class Game {
public static int counter=0;
private int id;
private GamePlayer player1;
private GamePlayer player2;
private Stack<Card> deck;
private GameStatus status;
private int nexPlayer;
public Game(GamePlayer player1,GamePlayer player2){
    Game.counter=counter+1;

    List<Card> intialDeck=new LinkedList<Card>();
    int i=1;
    for(CardSuit suit:CardSuit.values()){
        for(CardRank rank :CardRank.values()){
            intialDeck.add(new Card(i,suit,rank,"image"));
                    i=i+1;
        }
    }
    Collections.shuffle(intialDeck);
    for(Card card :intialDeck){
        this.deck.push(card);
    }
    this.status=GameStatus.ONGOING;
}
    public Game(GamePlayer player1){

        Game.counter=counter+1;
        this.id=counter;
        this.player1=player1;
        List<Card> intialDeck=new LinkedList<Card>();
        int i=1;
        for(CardSuit suit:CardSuit.values()){
            for(CardRank rank :CardRank.values()){
                String imgSrc;
                if(rank.name().equals("KING") || rank.name().equals("QUEEN") || rank.name().equals("JACK") || rank.name().equals("ACE")){
                    imgSrc= rank.name().toLowerCase();
                }else{
                    imgSrc=String.valueOf(rank.getValue());
                }
                imgSrc=imgSrc+"_of_"+suit.name().toLowerCase()+".png";

                intialDeck.add(new Card(i,suit,rank,imgSrc));
                i=i+1;
            }
            System.out.println(intialDeck);
        }

        Collections.shuffle(intialDeck);
        this.deck=new Stack<>();
        for(Card card :intialDeck){
            this.deck.push(card);
        }
        this.status=GameStatus.WAITING;
        this.nexPlayer=player1.getPlayer().getId();
    }
public Card draw(){
    return deck.pop();
}


}
