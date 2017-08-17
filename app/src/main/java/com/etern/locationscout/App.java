package com.etern.locationscout;

/**
 * Created by etern on 8/13/2017.
 */

public final class App {
    private final static Network network = new Network();
    public static Network Network() {
        return network;
    }

    private App() {}
}
