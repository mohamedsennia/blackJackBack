package sennia.mohamed.blackJack.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card {
private int id;
private CardSuit cardSuit;
private CardRank cardRank;
private String image;
}
