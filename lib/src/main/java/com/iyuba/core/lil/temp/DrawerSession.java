package com.iyuba.core.lil.temp;

public class DrawerSession {

    private static DrawerSession instance;

    public static DrawerSession getInstance(){
        if (instance==null){
            synchronized (DrawerSession.class){
                if (instance==null){
                    instance = new DrawerSession();
                }
            }
        }
        return instance;
    }

    //是否开启左侧
    private boolean leftOpen = false;

    public void setLeftOpen(boolean leftOpen) {
        this.leftOpen = leftOpen;
    }

    public boolean isLeftOpen() {
        return leftOpen;
    }
}
