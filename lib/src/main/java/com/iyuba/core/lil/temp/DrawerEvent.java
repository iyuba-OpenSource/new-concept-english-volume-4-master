package com.iyuba.core.lil.temp;

//主界面左侧操作的功能
public class DrawerEvent {

    //是否开启
    private boolean open;

    public DrawerEvent(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }
}
