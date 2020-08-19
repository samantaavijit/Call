package com.callingapp.avijitsamanta.call.Helper;

public class HeaderItem extends ListItem {

    private String key;


    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    public String getHeaderItem() {
        return key;
    }

    public void setHeaderItem(String key) {
        this.key = key;
    }


}
