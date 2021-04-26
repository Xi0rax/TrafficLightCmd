package org.xi0rax.trafficlight.viewmodel;

import org.xi0rax.trafficlight.enums.ControlModes;
import org.xi0rax.trafficlight.enums.OperateModes;
import org.xi0rax.trafficlight.enums.Signals;

public class TrafficLight implements Runnable {
    public TrafficLight() {
        this.setPower(true);
        this.setSignal(Signals.GREEN);
        this.setRedInterval(3);
        this.setYellowInterval(1);
        this.setGreenInterval(6);
        this.setYellowBlinkInterval(1);
        this.controlMode = ControlModes.AUTO;
        this.operateMode = OperateModes.NORMAL;
    }

    public Signals getSignal() {
        return signal;
    }

    public void setSignal(Signals signal) {
        this.signal = signal;
    }

    public ControlModes getControlMode() {
        return controlMode;
    }

    public void setControlMode(ControlModes controlMode) {
        this.controlMode = controlMode;
    }

    public OperateModes getOperateMode() {
        return operateMode;
    }

    public void setOperateMode(OperateModes operateMode) {
        this.operateMode = operateMode;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public int getRedInterval() {
        return redInterval;
    }

    public void setRedInterval(int redInterval) {
        this.redInterval = redInterval;
    }

    public int getYellowInterval() {
        return yellowInterval;
    }

    public void setYellowInterval(int yellowInterval) {
        this.yellowInterval = yellowInterval;
    }

    public int getGreenInterval() {
        return greenInterval;
    }

    public void setGreenInterval(int greenInterval) {
        this.greenInterval = greenInterval;
    }

    public int getYellowBlinkInterval() {
        return yellowBlinkInterval;
    }

    public void setYellowBlinkInterval(int yellowBlinkInterval) {
        this.yellowBlinkInterval = yellowBlinkInterval;
    }

    private Signals signal;
    private ControlModes controlMode;
    private OperateModes operateMode;
    private boolean power;

    private int redInterval;
    private int yellowInterval;
    private int greenInterval;
    private int yellowBlinkInterval;

    private void switchSignal() {
        switch (this.operateMode) {
            case NIGHT:
                if (this.signal == Signals.BLINK) {
                    this.signal = Signals.YELLOW;
                } else {
                    this.signal = Signals.BLINK;
                }
                break;
            case NORMAL:
                if (this.signal == Signals.RED) {
                    this.setSignal(Signals.GREEN);
                } else {
                    this.setSignal(Signals.values()[this.signal.ordinal() - 1]);
                }
                break;
            default:
                break;
        }
    }

    private int getSignalInterval(Signals signal) {
        switch (signal) {
            case RED:
                return redInterval;
            case YELLOW:
                return yellowInterval;
            case GREEN:
                return greenInterval;
            case BLINK:
                return yellowBlinkInterval;
            default:
                return 0;
        }
    }

    @Override
    public void run() {
        while (this.isPower()) {
            try {
                if (controlMode == ControlModes.AUTO) {
                    Thread.sleep(getSignalInterval(this.signal) * 1000L);
                    switchSignal();
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }
}
