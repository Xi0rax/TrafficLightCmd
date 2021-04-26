package org.xi0rax.trafficlight.viewmodel;

import org.xi0rax.trafficlight.enums.ControlModes;
import org.xi0rax.trafficlight.enums.OperateModes;
import org.xi0rax.trafficlight.enums.Signals;
import org.xi0rax.trafficlight.interfaces.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TrafficModel {
    Timer timer = new Timer();
    MonitorTask monitor = null;

    public TrafficModel() {
        this.commands = new HashMap<String, Command>() {
            {
                put("tladd", new AddTrafficLight());
                put("tlrm", new RemoveTrafficLight());
                put("sigint", new SetSigInterval());
                put("conmode", new SetControlMode());
                put("opmode", new SetOperatingMode());
                put("setpower", new SetPower());
                put("setsignal", new SetSignal());
                put("getstate", new GetState());
                put("exit", new Shutdown());
                put("help", new Help());
                put("flush", new Flush());
                put("monitor", new Monitor());
                put("", args -> {
                    if (monitor != null) {
                        monitor.cancel();
                        monitor = null;
                        return "Interrupted";
                    }
                    return "";
                });
                put("tllist", new TrafficLightList());
            }
        };
    }

    Map<String, Command> commands;
    List<TrafficLight> trafficLights = new ArrayList<>();

    private class AddTrafficLight implements Command {
        @Override
        public String execute(String[] args) {
            TrafficLight trafficLight = new TrafficLight();
            trafficLights.add(trafficLight);
            new Thread(trafficLight).start();
            return "Traffic light " + (trafficLights.size() - 1) + " added successfully";
        }
    }

    private class RemoveTrafficLight implements Command {
        @Override
        public String execute(String[] args) {
            if (args.length >= 1) {
                int trafficLightNumber = Integer.parseInt(args[0]);
                trafficLights.get(trafficLightNumber).setPower(false);
                trafficLights.remove(trafficLightNumber);
                return "Traffic light " + trafficLightNumber + " removed successfully";
            } else {
                return "Error: too few parameters";
            }
        }
    }

    public class TrafficLightList implements Command {
        @Override
        public String execute(String[] args) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < trafficLights.size(); i++) {
                result.append("Traffic Light ").append(i).append('\n');
            }
            return result.toString();
        }
    }

    public class MonitorTask extends TimerTask {
        private Command command;
        private String[] args;

        public MonitorTask(Command command, String[] args) {
            this.command = command;
            this.args = args;
        }

        @Override
        public void run() {
            try {
                if (System.getProperty("os.name").contains("Windows")) {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } else {
                    Runtime.getRuntime().exec("clear");
                }
            } catch (java.io.IOException | InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(command.execute(args));
        }
    }

    private class Monitor implements Command {
        @Override
        public String execute(String[] args) {
            monitor = new MonitorTask(commands.get("getstate"), args);
            timer.scheduleAtFixedRate(monitor, 0,
                    Integer.parseInt(args[1]) * 1000L);
            return "monitoring traffic light " + args[0] + "...........";
        }
    }

    private class SetSigInterval implements Command {
        @Override
        public String execute(String[] args) {
            String signal = "";
            if (args.length >= 3) {
                int trafficLightNumber = Integer.parseInt(args[0]);
                TrafficLight trafficLight = trafficLights.get(trafficLightNumber);
                switch (args[1]) {
                    case "0":
                        signal = "red";
                        trafficLight.setRedInterval(Integer.parseInt(args[2]));
                        break;
                    case "1":
                        signal = "yellow";
                        trafficLight.setYellowInterval(Integer.parseInt(args[2]));
                        break;
                    case "2":
                        signal = "green";
                        trafficLight.setGreenInterval(Integer.parseInt(args[2]));
                        break;
                    case "3":
                        signal = "blink";
                        trafficLight.setYellowBlinkInterval(Integer.parseInt(args[2]));
                        break;
                    default:
                        break;
                }
                return "Interval of " + signal + " signal has been set to " + args[2] + " sec";
            } else {
                return "Error: too few parameters";
            }
        }
    }

    private class SetControlMode implements Command {
        @Override
        public String execute(String[] args) {
            if (args.length >= 2) {
                int trafficLightNumber = Integer.parseInt(args[0]);
                TrafficLight trafficLight = trafficLights.get(trafficLightNumber);
                if (trafficLight.getOperateMode() == OperateModes.NIGHT) {
                    return "Error: traffic light in night mode now";
                }
                trafficLight.setControlMode(ControlModes.values()[Integer.parseInt(args[1])]);
                return "Control mode of " + trafficLightNumber + " traffic light has been set to " + ControlModes.values()[Integer.parseInt(args[1])].name();
            } else {
                return "Error: too few parameters";
            }
        }
    }

    private class SetOperatingMode implements Command {
        @Override
        public String execute(String[] args) {
            if (args.length >= 2) {
                int trafficLightNumber = Integer.parseInt(args[0]);
                TrafficLight trafficLight = trafficLights.get(trafficLightNumber);
                trafficLight.setOperateMode(OperateModes.values()[Integer.parseInt(args[1])]);
                if (trafficLight.getControlMode() == ControlModes.MANUAL) {
                    trafficLight.setControlMode(ControlModes.AUTO);
                }
                return "Operating mode of " + trafficLightNumber + " traffic light has been set to " + OperateModes.values()[Integer.parseInt(args[1])].name();
            } else {
                return "Error: too few parameters";
            }
        }
    }

    private class SetPower implements Command {
        @Override
        public String execute(String[] args) {
            if (args.length >= 1) {
                int trafficLightNumber = Integer.parseInt(args[0]);
                trafficLights.get(trafficLightNumber).setPower(Boolean.parseBoolean(args[1]));
                return "Power of " + trafficLightNumber + " traffic light was set to " + args[1];
            } else {
                return "Error: too few parameters";
            }
        }
    }

    private class SetSignal implements Command {
        @Override
        public String execute(String[] args) {
            String signal = "";
            if (args.length >= 2) {
                int trafficLightNumber = Integer.parseInt(args[0]);
                TrafficLight trafficLight = trafficLights.get(trafficLightNumber);
                ControlModes controlMode = trafficLight.getControlMode();
                if (controlMode != ControlModes.MANUAL) {
                    return "Error: traffic light in auto mode now";
                }
                switch (args[1]) {
                    case "0":
                        signal = "red";
                        trafficLight.setSignal(Signals.RED);
                        break;
                    case "1":
                        signal = "yellow";
                        trafficLight.setSignal(Signals.YELLOW);
                        break;
                    case "2":
                        signal = "green";
                        trafficLight.setSignal(Signals.GREEN);
                        break;
                    default:
                        break;
                }
                return "Signal of " + trafficLightNumber + " traffic light has been set to " + signal;
            } else {
                return "Error: too few parameters";
            }
        }
    }

    private class GetState implements Command {
        @Override
        public String execute(String[] args) {
            if (args.length >= 1) {
                int trafficLightNumber = Integer.parseInt(args[0]);
                TrafficLight trafficLight = trafficLights.get(trafficLightNumber);
                int state = trafficLight.getSignal().ordinal();
                StringBuilder result = new StringBuilder("+---+\t\t+-----------------------+\t+-----------------------+\n")
                        .append("|(").append(state == 0 ? 1 : 0).append(")| R\t\t|TrafficLight ")
                        .append(args[0])
                        .append((" State\t|\t|Signal Intervals\t|\n"))
                        .append("|   |\t\t+-----------------------+\t+-----------------------+\n")
                        .append("|(").append(state == 1 ? 1 : 0).append(")| Y\t\t|Power: ")
                        .append(trafficLight.isPower()).append("\t\t|\t|Red: ")
                        .append(trafficLight.getRedInterval()).append(" sec\t\t|\n")
                        .append("|   |\t\t|Control Mode: ")
                        .append(trafficLight.getControlMode().name()).append(" \t|\t|Yellow: ")
                        .append(trafficLight.getYellowInterval()).append(" sec\t\t|\n")
                        .append("|(")
                        .append(state == 2 ? 1 : 0).append(")| G\t\t|Operating Mode: ")
                        .append(trafficLight.getOperateMode().name()).append("\t|\t|Green: ")
                        .append(trafficLight.getGreenInterval()).append(" sec\t\t|\n")
                        .append("+---+\t\t|Current Signal: ")
                        .append(trafficLight.getSignal().name()).append("\t|\t|NightYellow: ")
                        .append(trafficLight.getYellowBlinkInterval()).append(" sec\t|\n")
                        .append("  |\t\t+-----------------------+\t+-----------------------+");
                return result.toString();
            } else {
                return "Error: too few parameters";
            }
        }
    }

    private class Shutdown implements Command {
        @Override
        public String execute(String[] args) {
            for (TrafficLight trafficLight : trafficLights) {
                trafficLight.setPower(false);

            }
            timer.cancel();
            return "halted";
        }
    }

    private class Flush implements Command {
        @Override
        public String execute(String[] args) {
            return "CLEAR";
        }
    }

    private class Help implements Command {
        @Override
        public String execute(String[] args) {
            StringBuilder result = new StringBuilder();
            result.append(">---------------------------------------------------HELP-------------------------------------------------<\n")
                    .append(">>tladd<<\t\t \t\t\t\t\tadds new traffic light to system\n")
                    .append(">>tlrm<< parameters: >n< \t\t\tremoves traffic light with index >n< from system\n")
                    .append(">>sigint<< parameters: >n< >s< >i< \tsets for traffic light with index >n< interval >i< for signal >s<\n")
                    .append(">>conmode<< parameters: >n< >m< \tsets control mode >m< for traffic light with index >n<\n")
                    .append(">>opmode<< parameters: >n< >m< \t\tsets operating mode >m< for traffic light with index >n<\n")
                    .append(">>setpower<< parameters: >n< >p< \tsets power state >p< for traffic light with index >n<\n")
                    .append(">>setsignal<< parameters: >n< >s< \tsets signal >s< for traffic light with index >n<\n")
                    .append(">getstate<< parameters: >n< \t\tgets state for traffic light with index >n<\n")
                    .append(">>exit<< \t\t\t \t\t\t\tterminates the system\n")
                    .append(">--------------------------------------------------------------------------------------------------------<");
            return result.toString();
        }
    }

    public String parse(String cmd) {
        String[] substrings = cmd.split(" ");
        try {
            return commands.get(substrings[0]).execute(Arrays.copyOfRange(substrings, 1, substrings.length));
        } catch (NullPointerException exception) {
            return "No such command: " + substrings[0];
        }
    }
}
