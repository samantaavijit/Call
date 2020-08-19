package com.callingapp.avijitsamanta.call.Helper;

import java.util.HashMap;
import java.util.List;

public class GeneralItem extends ListItem {

    private Modal list;

    public GeneralItem() {
    }

    private HashMap<String,Integer> hashMap;

    @Override
    public int getType() {
        return TYPE_GENERAL;
    }

    public Modal getGeneralItem() {
        return list;
    }

    public void setGeneralItem(Modal list) {
        this.list = list;
    }

    public HashMap<String, Integer> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, Integer> hashMap) {
        this.hashMap = hashMap;
    }
}
