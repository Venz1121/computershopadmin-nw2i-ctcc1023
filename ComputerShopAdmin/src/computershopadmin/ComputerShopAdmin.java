package computershopadmin;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class ComputerShopAdmin {
    private JFrame frame;
    private JProgressBar[] progressBars;
    private JLabel[] totalSpentLabels, totalHoursLabels;
    private JCheckBox[] checkBoxes;
    private JButton[] shutdownButtons;
    private JButton[] timeButtons;
    private JButton[] openTimeButtons;
    private JToggleButton[] stopTimerButtons;

    private int[] timeRemaining; // in seconds
    private int[] totalSpent;
    private int[] openTimeElapsed; // in seconds
    private boolean[] openTimeActive; // Tracks if Open Time is active
    private boolean[] isTimerPaused; // Tracks if the timer is paused

    private Timer timer;

    public ComputerShopAdmin() {
        frame = new JFrame("Computer Shop Admin");
        frame.setSize(1000, 600);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Sideboard Panel
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(null);
        sidePanel.setBounds(20, 20, 250, 520);
        sidePanel.setBorder(new LineBorder(Color.BLACK, 2));
        frame.add(sidePanel);

        // Initialize components
        progressBars = new JProgressBar[3];
        totalSpentLabels = new JLabel[3];
        totalHoursLabels = new JLabel[3];
        checkBoxes = new JCheckBox[3];
        shutdownButtons = new JButton[3];
        openTimeButtons = new JButton[3];
        stopTimerButtons = new JToggleButton[3];
        timeRemaining = new int[3];
        totalSpent = new int[3];
        openTimeElapsed = new int[3];
        openTimeActive = new boolean[3];
        isTimerPaused = new boolean[3];

        int yPosition = 20;

        for (int i = 0; i < 3; i++) {
            JPanel computerPanel = new JPanel();
            computerPanel.setLayout(null);
            computerPanel.setBounds(300, yPosition, 650, 150);
            computerPanel.setBorder(new LineBorder(Color.GRAY, 2));
            frame.add(computerPanel);

            JLabel computerLabel = new JLabel("COMPUTER " + (i + 1));
            computerLabel.setBounds(20, 10, 100, 30);
            computerPanel.add(computerLabel);

            progressBars[i] = new JProgressBar(0, 36000); // 10 hours max in seconds
            progressBars[i].setBounds(20, 50, 200, 20);
            computerPanel.add(progressBars[i]);

            totalSpentLabels[i] = new JLabel("Total Spent: 0 PHP");
            totalSpentLabels[i].setBounds(250, 50, 200, 20);
            computerPanel.add(totalSpentLabels[i]);

            totalHoursLabels[i] = new JLabel("Total Time: 0h 0m 0s");
            totalHoursLabels[i].setBounds(250, 80, 200, 20);
            computerPanel.add(totalHoursLabels[i]);

            checkBoxes[i] = new JCheckBox();
            checkBoxes[i].setBounds(500, 20, 20, 20);
            computerPanel.add(checkBoxes[i]);

            openTimeButtons[i] = new JButton("Open Time");
            openTimeButtons[i].setBounds(250, 110, 120, 30);
            int computerIndex = i;
            openTimeButtons[i].addActionListener(e -> toggleOpenTime(computerIndex));
            computerPanel.add(openTimeButtons[i]);

            shutdownButtons[i] = new JButton("Shutdown PC");
            shutdownButtons[i].setBounds(400, 110, 120, 30);
            shutdownButtons[i].addActionListener(e -> shutdownComputer(computerIndex));
            computerPanel.add(shutdownButtons[i]);

            stopTimerButtons[i] = new JToggleButton("Pause Timer");
            stopTimerButtons[i].setBounds(20, 110, 120, 30);
            stopTimerButtons[i].addActionListener(e -> toggleTimerPause(computerIndex));
            computerPanel.add(stopTimerButtons[i]);

            yPosition += 170;
        }

        // Time addition buttons
        String[] timeOptions = {"15 minutes", "30 minutes", "1 hour", "1 hour & 30min", "4 hours", "10 hours"};
        int[] timeValues = {900, 1800, 3600, 5400, 14400, 36000}; // in seconds
        int[] costs = {5, 10, 20, 25, 75, 180};

        timeButtons = new JButton[timeOptions.length];
        yPosition = 20;
        for (int i = 0; i < timeOptions.length; i++) {
            timeButtons[i] = new JButton(timeOptions[i] + " - " + costs[i] + "PHP");
            timeButtons[i].setBounds(20, yPosition, 200, 30);
            int timeToAdd = timeValues[i];
            int cost = costs[i];
            timeButtons[i].addActionListener(e -> addTimeAndStartCountdown(timeToAdd, cost));
            sidePanel.add(timeButtons[i]);
            yPosition += 50;
        }

        // Timer for countdown
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimers();
            }
        }, 0, 1000); // Runs every second

        frame.setVisible(true);
    }

    private void toggleOpenTime(int index) {
        if (!openTimeActive[index] && !checkBoxes[index].isSelected()) {
            openTimeActive[index] = true;
            JOptionPane.showMessageDialog(frame, "Open Time started for Computer " + (index + 1));
        } else if (openTimeActive[index]) {
            openTimeActive[index] = false;
            JOptionPane.showMessageDialog(frame, "Open Time stopped for Computer " + (index + 1));
        }
    }

    private void toggleTimerPause(int index) {
        isTimerPaused[index] = !isTimerPaused[index];
        if (isTimerPaused[index]) {
            stopTimerButtons[index].setText("Resume Timer");
            JOptionPane.showMessageDialog(frame, "Timer paused for Computer " + (index + 1));
        } else {
            stopTimerButtons[index].setText("Pause Timer");
            JOptionPane.showMessageDialog(frame, "Timer resumed for Computer " + (index + 1));
        }
    }

    private void addTimeAndStartCountdown(int timeToAdd, int cost) {
        for (int i = 0; i < 3; i++) {
            if (checkBoxes[i].isSelected()) {
                timeRemaining[i] += timeToAdd;
                totalSpent[i] += cost;
                updateLabels(i);
                break;
            }
        }
    }

    private void updateTimers() {
        for (int i = 0; i < 3; i++) {
            if (!isTimerPaused[i]) {
                if (timeRemaining[i] > 0) {
                    timeRemaining[i]--;
                    updateLabels(i);
                    if (timeRemaining[i] == 0) {
                        shutdownComputer(i);
                    }
                }

                if (openTimeActive[i]) {
                    openTimeElapsed[i]++;
                    totalSpent[i] = (openTimeElapsed[i] / 900) * 5; // 15 minutes = 900 seconds = 5 PHP
                    updateLabels(i);
                }
            }
        }
    }

    private void updateLabels(int index) {
        progressBars[index].setValue(timeRemaining[index]);
        totalSpentLabels[index].setText("Total Spent: " + totalSpent[index] + " PHP");

        int totalElapsed = openTimeElapsed[index] + timeRemaining[index];
        int hours = totalElapsed / 3600;
        int minutes = (totalElapsed % 3600) / 60;
        int seconds = totalElapsed % 60;

        totalHoursLabels[index].setText(String.format("Total Time: %dh %dm %ds", hours, minutes, seconds));
    }

    private void shutdownComputer(int index) {
        openTimeActive[index] = false;
        timeRemaining[index] = 0;
        openTimeElapsed[index] = 0;
        totalSpent[index] = 0;
        updateLabels(index);
        JOptionPane.showMessageDialog(frame, "Computer " + (index + 1) + " has been shut down.");
    }

    public static void main(String[] args) {
        new ComputerShopAdmin();
    }
}
