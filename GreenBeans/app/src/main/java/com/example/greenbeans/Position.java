package com.example.greenbeans;

public class Position {

    String symbol, quantity, buyPrice;
    int quantityInt;
    double buyPriceNum, currentPrice, dayProfitLoss;

    public Position(String symbol, String quantity, String buyPrice){
        this.symbol = symbol;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
    }
    public Position(){}

}
