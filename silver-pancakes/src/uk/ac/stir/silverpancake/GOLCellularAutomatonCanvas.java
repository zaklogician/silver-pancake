package uk.ac.stir.silverpancake;
/*
 *  GOLCellularAutomatonCanvas.
 *  Copyright (C) 2002  Frank Buﬂ (fb@frank-buss.de)
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You can get the GNU General Public License at
 *  http://www.gnu.org/licenses/gpl.html
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

/**
 * Game Of Life cellular automaton canvas.
 *
 * @author      Frank Buﬂ
 * @see step() for the rules for this CA.
 */
public class GOLCellularAutomatonCanvas
	extends Canvas
	implements ImageProducer, MouseListener, MouseMotionListener {

	/**
	 * Width of the CA state array.
	 */
	private final static int width = 256;

	/**
	 * Height of the CA state array.
	 */
	private final static int height = 256;

	/**
	 * Current CA state array.
	 */
	private int current[];

	/**
	 * Destination CA state array, used by step(), swapped with 'current', after
	 * filled.
	 */
	private int next[];

	/**
	 * Display with and height for one CA cell.
	 */
	private final static int zoom = 2;

	/**
	 * Image buffer.
	 */
	private final int pixels[];

	/**
	 * ColorModel for the pixels array.
	 */
	private final ColorModel cm =
		new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF);

	/**
	 * Image for displaying the CA.
	 */
	private Image image;

	/**
	 * Reference to the consumer of the image for displaying the CA.
	 */
	private ImageConsumer consumer;

	/**
	 * Creates a new automaton canvas.
	 */
	public GOLCellularAutomatonCanvas() {
		// init arrays
		current = new int[width * height];
		next = new int[width * height];
		pixels = new int[width * height * zoom * zoom];

		// add listener
		addMouseListener(this);
		addMouseMotionListener(this);

		// init CA
		init();

		// create rendering image with this as ImageProducer
		image = createImage(this);
	}

	/**
	 * Clears the CA array and initialized it with random values.
	 */
	public void init() {
		// fill cellular automaton with random values
		int adr = width + 1;
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++)
				current[adr++] = Math.random() > 0.7 ? 1 : 0;
			adr += 2;
		}
	}

	/**
	 * Clears the CA array and initialized it with the rabbits pattern.
	 */
	public void initRabbits() {
		// clear CA
		int adr = width + 1;
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++)
				current[adr++] = 0;
			adr += 2;
		}

		// set rabbits pattern
		int center = height / 2 * width + width / 2;
		current[center - 3] = 1;
		current[center + 1] = 1;
		current[center + 2] = 1;
		current[center + 3] = 1;
		current[center - 3 + width] = 1;
		current[center - 2 + width] = 1;
		current[center - 1 + width] = 1;
		current[center + width + 2] = 1;
		current[center + 2 * width - 2] = 1;
	}

	/**
	 * Clears the CA array and initialised it with the symmetric rabbits pattern.
	 */
	public void initSymmetricRabbits() {
		// clear CA
		int adr = width + 1;
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++)
				current[adr++] = 0;
			adr += 2;
		}

		// set rabbits pattern
		int center = height / 2 * width + width / 2;

		current[center] = 1;
		current[center + 1] = 1;
		current[center + 2] = 1;
		current[center + 3] = 1;
		current[center + 2 * width + 4] = 1;
		current[center + 2 * width + 5] = 1;
		current[center + 3 * width + 6] = 1;
	}

	/**
	 * Calculate next CA step.
	 */
	public void step() {
		int adr = width + 1;
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int center = current[adr];
				int result = center;

				// game of life rules
				int n = current[adr - width];
				int ne = current[adr - width + 1];
				int e = current[adr + 1];
				int se = current[adr + width + 1];
				int s = current[adr + width];
				int sw = current[adr + width - 1];
				int w = current[adr - 1];
				int nw = current[adr - width - 1];
				int sum = n + ne + e + se + s + sw + w + nw;
				if (sum == 3)
					result = 1;
				else if (sum != 2)
					result = 0;

				// set next state
				next[adr++] = result;
			}
			adr += 2;
		}

		// swap buffers
		int tmp[] = next;
		next = current;
		current = tmp;
	}

	/**
	 * Show current CA state array.
	 * @see java.awt.Component#update(Graphics)
	 */
	synchronized public void update(Graphics g) {
		// copy to pixels
		int adr = width + 1;
		for (int y = 1; y < height - 1; y++) {
			int adr2 = zoom * zoom * width * y + zoom;
			for (int x = 1; x < width - 1; x++) {
				int t = current[adr++];
				int c = t == 1 ? 0xffffff : 0;
				int i = adr2;
				for (int cy = 0; cy < zoom; cy++) {
					for (int cx = 0; cx < zoom; cx++) {
						pixels[i++] = c;
					}
					i += (width - 1) * zoom;
				}
				adr2 += zoom;
			}
			adr += 2;
		}

		// draw
		if (consumer != null)
			startProduction(consumer);
		g.drawImage(image, 0, 0, null);
	}

	/**
	 * Set states.
	 * @param x x coordinate in the state array.
	 * @param y y coordinate in the state array.
	 * @param pixel true, if state should be set, false otherwise.
	 */
	private void mouseCellAction(int x, int y, boolean pixel) {
		x /= zoom;
		y /= zoom;
		if (x >= 0 && x < width && y >= 0 && y < height) {
			current[x + y * width] = pixel ? 0 : 1;
		}
		repaint();
	}

	// Java specific code (listeners etc.)

	/**
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(width * zoom, height * zoom);
	}

	/**
	 * @see java.awt.Component#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/**
	 * @see java.awt.Component#getMaximumSize()
	 */
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	/**
	 * @see java.awt.image.ImageProducer#addConsumer(java.awt.image.ImageConsumer)
	 */
	public void addConsumer(ImageConsumer c) {
	}

	/**
	 * @see java.awt.image.ImageProducer#isConsumer(java.awt.image.ImageConsumer)
	 */
	public boolean isConsumer(ImageConsumer consumer) {
		return consumer != null;
	}

	/**
	 * @see java.awt.image.ImageProducer#removeConsumer(java.awt.image.ImageConsumer)
	 */
	public void removeConsumer(ImageConsumer consumer) {
	}

	/**
	 * @see java.awt.image.ImageProducer#startProduction(java.awt.image.ImageConsumer)
	 */
	public void startProduction(ImageConsumer c) {
		if (c != null)
			consumer = c;
		if (consumer != null) {
			consumer.setDimensions(width * zoom, height * zoom);
			consumer.setProperties(null);
			consumer.setColorModel(cm);
			consumer.setHints(
				ImageConsumer.TOPDOWNLEFTRIGHT
					| ImageConsumer.COMPLETESCANLINES
					| ImageConsumer.SINGLEPASS
					| ImageConsumer.SINGLEFRAME);
			consumer.setPixels(
				0,
				0,
				width * zoom,
				height * zoom,
				cm,
				pixels,
				0,
				width * zoom);
			consumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
		}
	}

	/**
	 * @see java.awt.image.ImageProducer#requestTopDownLeftRightResend(java.awt.image.ImageConsumer)
	 */
	public void requestTopDownLeftRightResend(ImageConsumer consumer) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		mouseCellAction(
			e.getX(),
			e.getY(),
			(e.getModifiers() & MouseEvent.META_MASK) > 0);
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		mouseCellAction(
			e.getX(),
			e.getY(),
			(e.getModifiers() & MouseEvent.META_MASK) > 0);
	}
	/**
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
	}
	
	public static void main(String s[]) {

		JFrame frame = new JFrame("JFrame Example");

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		JLabel label = new JLabel("This is a label!");

		JButton button = new JButton();
		button.setText("Press me");

		panel.add(label);
		panel.add(button);

        Canvas s1 = new Canvas();
		panel.add(s1);
		frame.add(panel);
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
}
