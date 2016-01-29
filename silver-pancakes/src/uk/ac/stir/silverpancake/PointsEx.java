package uk.ac.stir.silverpancake;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

class Surface extends JPanel implements ActionListener {

    private final int DELAY = 150;
    private Timer timer;

    public Surface() {

        initTimer();
    }

    private void initTimer() {

        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    public Timer getTimer() {
        
        return timer;
    }

    Map map = new Map();
    int iterations = 0;
    // our main method
    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(Color.blue);
        /*
        int w = getWidth();
        int h = getHeight();

        Random r = new Random();

        for (int i = 0; i < 2000; i++) {
            int x = Math.abs(r.nextInt()) % w;
            int y = Math.abs(r.nextInt()) % h;
            if (r.nextInt(2) == 0) {
            	g2d.setPaint(Color.blue);
            	g2d.drawLine(x, y, x, y);
            } else {
            	g2d.setPaint(Color.black);
            	g2d.drawLine(x, y, x, y);
            }
        }*/
        /*
        Random rng = new Random();
        
        for(int i = 0; i < map.values.length; i++)
        for(int j = 0; j < map.values.length; j++) {
        	map.values[i][j].setTemperature(map.values[i][j].getTemperature() + rng.nextInt(3)-1);
        }*/
        

        iterations++;
        if (iterations % 5 == 0) {
	        map.iteration();
        }
	        
	        
	    for(int i = 0; i < map.values.length; i++)
	    for(int j = 0; j < map.values.length; j++) {
	        	Cell c = map.values[i][j];
	        	g2d.setColor(c.temperatureColor());
	        	//g2d.setColor(c.getColor());
	        	g2d.fillRect(i*64, j*64, 64, 64);
	    }
        
        
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}

public class PointsEx extends JFrame {

    public PointsEx() {

        initUI();
    }

    private void initUI() {

        final Surface surface = new Surface();
        add(surface);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Timer timer = surface.getTimer();
                timer.stop();
            }
        });

        setTitle("Points");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                PointsEx ex = new PointsEx();
                ex.setVisible(true);
            }
        });
    }
}
