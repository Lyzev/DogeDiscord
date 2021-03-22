package eu.lyzev.utils.blackjack;

public enum BlackjackCards {

    ACE(1, 11, "A"),
    TWO(2, null, "2"),
    THREE(3, null, "3"),
    FOUR(4, null, "4"),
    FIVE(5, null, "5"),
    SIX(6, null, "6"),
    SEVEN(7, null, "7"),
    EIGHT(8, null, "8"),
    NINE(9, null, "9"),
    TEN(10, null, "10"),
    JACK(10, null, "J"),
    QUEEN(10, null, "Q"),
    KING(10, null, "K");


    private final Integer firstValue;
    private final Integer secondValue;
    private final String card;

    BlackjackCards(Integer firstValue, Integer secondValue, String card) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.card = card;
    }

    public Integer getFirstValue() {
        return firstValue;
    }

    public Integer getSecondValue() {
        return secondValue;
    }

    public String getCard() {
        return card;
    }
}
