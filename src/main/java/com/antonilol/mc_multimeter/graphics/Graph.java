/*
 * Copyright (c) 2021 Antoni Spaanderman
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.antonilol.mc_multimeter.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.antonilol.mc_multimeter.Utils;

public class Graph extends JPanel {
	
	public static final int GRAPH_H = 150;
	public static final int GRAPH_W = 400;
	public static final int MARGIN = 20;
	
	private static final long serialVersionUID = 1L;
	
	public int verticalOffset_ = 0;
	
	public static final String X_LABEL = "ticks"; // TODO labels
	public static final int X_MAX = 100;
	public static final int X_MIN = 0;
	public static final int X_LEN = X_MAX - X_MIN;
	
	public static final String Y_LABEL = "power";
	public static final int Y_MAX = 15;
	public static final int Y_MIN = 0;
	public static final int Y_LEN = Y_MAX - Y_MIN;
	
	public static String type = Utils.LINE_GRAPH;
	
	private final ArrayList<Integer> data = new ArrayList<Integer>(X_LEN + 1);
	
	private String title;

	public Graph(String title) {
		this.title = title;
    }

	public void addData(int n) {
		n = Math.max(Math.min(n, Y_MAX), Y_MIN);
		
		while (data.size() >= X_LEN + 1) {
			data.remove(0); // index zero, not the value zero
		}
		
		data.add(n);
		
		repaint();
	}

	private void drawGraph(Graphics g) {
    	g.setColor(new Color(150, 150, 150));
    	g.drawRect(MARGIN, MARGIN, GRAPH_W, GRAPH_H);
    	
    	for (int y = Y_MIN + 1; y < Y_MAX; y++) {
    		int sy = mapY(y);
    		g.drawLine(MARGIN, sy, GRAPH_W + MARGIN, sy);
    	}
    	
    	for (int x = X_MIN + 1 + verticalOffset_; x < X_MAX; x += 20) {
    		int sx = mapX(x);
    		g.drawLine(sx, MARGIN, sx, GRAPH_H + MARGIN);
    	}
    	
    	g.setColor(Color.BLACK);
    	int prevX = 0;
    	int prevY = 0;
    	for (int x = 0; x < data.size(); x++) {
    		int y = data.get(x);
    		int sx = mapX(x + X_LEN - data.size() + 1);
    		int sy = mapY(y);
    		if (type == Utils.LINE_GRAPH) {
    			if (x > 0) {
    				g.drawLine(prevX, prevY, sx, sy);
    			}
    			g.fillRect(sx - 1, sy - 1, 3, 3);
    		} else {
    			if (x > 0) {
    				g.fillRect(prevX, sy, sx - prevX, GRAPH_H + MARGIN - sy);
    			}
    		}
    		prevX = sx;
    		prevY = sy;
    	}
    }
	
	private static int mapX(int x) {
		return Utils.map(x, X_MIN, X_MAX, MARGIN, GRAPH_W + MARGIN);
	}
	
	private static int mapY(int y) {
		return Utils.map(y, Y_MIN, Y_MAX, GRAPH_H + MARGIN, MARGIN);
	}
	
	public Dimension getPreferredSize() {
        return new Dimension(
        	GRAPH_W + MARGIN * 2,
        	GRAPH_H + MARGIN * 2
        );
    }

    public String getTitle() {
		return title;
	}
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   
        g.setColor(Color.BLACK);
        g.drawString(title, MARGIN, 15);
        
        drawGraph(g);
    }
    
    public void setTitle(String title) {
		this.title = title;
	}
}

