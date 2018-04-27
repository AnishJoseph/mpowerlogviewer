package com.hexgen;

/**
 * Created by anishjoseph on 26/04/18.
 */
public class Filter {
    private boolean enabled;
    private String searchStr;

    public Filter(){}

    public Filter(boolean enabled, String searchStr) {
        this.enabled = enabled;
        this.searchStr = searchStr;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }
}
