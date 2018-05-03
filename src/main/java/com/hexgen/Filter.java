package com.hexgen;

/**
 * Created by anishjoseph on 26/04/18.
 */
public class Filter {
    private boolean enabled;
    private Object searchSpec;

    public Filter(){}

    public Filter(boolean enabled, Object searchSpec) {
        this.enabled = enabled;
        this.searchSpec = searchSpec;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Object getSearchSpec() {
        return searchSpec;
    }

    public void setSearchSpec(Object searchSpec) {
        this.searchSpec = searchSpec;
    }
}
