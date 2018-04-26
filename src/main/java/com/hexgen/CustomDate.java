package com.hexgen;

import java.text.SimpleDateFormat;

/**
 * Created by anishjoseph on 25/04/18.
 */
public class CustomDate extends java.sql.Date {

    public CustomDate(long date) {
        super(date);
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(this);
    }
}