package sennia.mohamed.blackJack.card;

public enum CardRank {
    ACE(11),  // Can also be 1 in certain cases
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(10),
    QUEEN(10),
    KING(10);
    private final int value;
    CardRank(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
