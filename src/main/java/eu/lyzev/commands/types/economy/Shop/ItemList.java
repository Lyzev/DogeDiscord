package eu.lyzev.commands.types.economy.Shop;

import eu.lyzev.main.DogeDiscord;

public enum ItemList {

    PIZZA(":pizza: Pizza", "It's just a pizza!", 30L),
    WATER_BOTTLE(":potable_water: Water Bottle", "Stay hydrated!", 20L),
    GAME_BOY(":video_game: Game Boy", "Play some games and relax!", 5000L),
    ROBBERY(":money_with_wings: Robbery", "If you use this item, you can rob someone!", 2500L),
    HUMAN(":bust_in_silhouette: Human", "Do you think cats and dogs are boring? Then here is a human!", 10000L),
    COIN_BOMB(":bomb: Coin Bomb", "If you detonate this bomb, every user will receive money!", 1000L),
    XL_COIN_BOMB(":bomb: XL Coin Bomb", "If you detonate this bomb, every user will receive much money!", 5000L),
    XXL_COIN_BOMB(":bomb: XXL Coin Bomb", "If you detonate this bomb, every user will receive very much money!", 10000L),
    DOGECOIN(":coin: Dogecoin", "The price of the coin will change overtime!", DogeDiscord.dogecoin),
    DOGE(":dog: Doge", "This item is just for the flex!", 100000L);

    private final String name;
    private final String description;
    private final Long price;

    ItemList(String name, String description, Long price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
