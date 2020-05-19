package com.example.beacons.Admin.Models;

/**
 * Deal is a model of the real Deal. In thi case we are only working with a deal,
 * a price or discount, a time a compId and a deal link to a website Id and a company id
 */
public class Deal {
    private String Deal;
    private String Price_Discount;
    private String Time;
    private String compId;
    private String Deal_Link;
    // an empty constructor needed for the recycling view
    public Deal(){
        //needed
    }

    public Deal(String Deal, String Price_Discount, String Time, String compId,String Deal_Link) {
        this.Deal = Deal;
        this.Price_Discount = Price_Discount;
        this.Time = Time;
        this.compId = compId;
        this.Deal_Link=Deal_Link;
    }

    public String getDeal() {
        return Deal;
    }

    public String getPrice_Discount() {
        return Price_Discount;
    }

    public String getTime() {
        return Time;
    }

    public String getCompId() { return compId; }

    public String getDeal_Link() { return Deal_Link; }

}
