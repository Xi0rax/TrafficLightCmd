package org.xi0rax.trafficlight;

import org.xi0rax.trafficlight.viewmodel.TrafficView;

public class Program {
    public static void main(String[] args) {
        TrafficView trafficView = new TrafficView();
        trafficView.waitForInput();
    }
}
