package com.example.dhruv.twitteranalyser;

/**
 * Created by Dhruv on 24-Nov-15.
 */
public class TweetItem {

    String message;
    Boolean isPositive = false;
    int percentage = 75;

    public TweetItem(String message, Boolean isPositive, int percentage) {
        this.message = message;
        this.isPositive = isPositive;
        this.percentage = percentage;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getIsPositive() {
        return isPositive;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIsPositive(Boolean isPositive) {
        this.isPositive = isPositive;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
