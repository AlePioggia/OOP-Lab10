package it.unibo.oop.lab.reactivegui03;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class AnotherConcurrentGUI extends JFrame{

    private static final long serialVersionUID = -7249181363918880432L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel displayValue = new JLabel();
    private final JButton up = new JButton("Up");
    private final JButton down = new JButton("Down");
    private final JButton stop = new JButton("Stop");
    
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(displayValue);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final tAgent tAgent = new tAgent();
        new Thread(tAgent).start();
        final Agent agent = new Agent();
        new Thread(agent).start();
        stop.addActionListener(new ActionListener() { 
        @Override
        public void actionPerformed(final ActionEvent e) {
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        }
        });
        up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.goUp();
            } 
        });
       down.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
                agent.goDown();
        }   
       });
        
    }

    private class tAgent implements Runnable {
        public void run() {
            try {
                Thread.sleep(10000);
                AnotherConcurrentGUI.this.stop.doClick();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile int counter;
        private volatile boolean up;
        
        @Override
        public void run() {
            while (!this.stop) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            AnotherConcurrentGUI.this.displayValue.setText(Integer.toString(Agent.this.counter));
                        }
                    });
                    upDownClick();
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }
        
        public void goUp() {
            this.up = true;
        }
        
        public void goDown() {
            this.up = false;
        }
        
        private int upDownClick(){
            return this.up ? this.counter++ : this.counter--;
        }
}
}
