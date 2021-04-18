package org.xi0rax.trafficlight.viewmodel;

public class TrafficViewModel {
    private TrafficModel trafficModel;

    public TrafficViewModel() {
        this.trafficModel = new TrafficModel();
    }

    public String execute(String command) {
        return this.trafficModel.parse(command);
    }
}
