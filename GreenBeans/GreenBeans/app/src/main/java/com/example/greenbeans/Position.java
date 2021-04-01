package com.example.greenbeans;

public class Position {

    String symbol, quantity;
    int quantityInt;
    double buyPrice, currentPrice, dayProfitLoss;

    public Position(String symbol, String quantity){
        this.symbol = symbol;
        this.quantity = quantity;
    }
    public Position(){}

}
