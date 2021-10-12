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

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.antonilol.mc_multimeter.Utils;

public class Frame extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		if (args.length == 0) {
			return;
		}
		EventQueue.invokeLater(() -> {
			new Frame();
		});
	}
	
	private GridBagConstraints gbc;
	
	private HashMap<String,Graph> graphs = new HashMap<String,Graph>();

	public static int verticalOffset = 0;
	
	public Frame() {
		super("Minecraft Multimeter");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		
		gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		pack();
		setVisible(true);
		
		new Thread(this).start();
	}
	
	private void onMessage(String msg) {
		if (msg == null || msg.length() == 0) {
			return;
		}
		
		String[] a = msg.split(" ");
		
		if (a.length == 0) {
			return;
		}
		
		boolean needPack = false;
		
		if (a[0].equals(Utils.DATA)) {
			if (verticalOffset <= 0) {
				verticalOffset = 20;
			}
			verticalOffset--;
			for (int i = 1; i < a.length; i++) {
				String[] p = a[i].split(",");
				if (p.length == 3) {
					Graph g = graphs.get(p[0]);
					String title = p[0] + ": " + p[1].replace('+', ' ');
					if (g == null) {
						g = new Graph(title);
						add(g, gbc);
						gbc.gridy++;
						needPack = true;
						graphs.put(p[0], g);
					}
					g.setTitle(title);
					g.addData(Integer.parseInt(p[2]));
					g.verticalOffset_ = verticalOffset;
					g.repaint();
				}
			}
		} else if (a[0].equals(Utils.REMOVE)) {
			Graph g = graphs.get(a[1]);
			if (g != null) {
				graphs.remove(a[1]);
				remove(g);
				needPack = true;
			}
		} else if (a[0].equals(Utils.REMOVEALL)) {
			for (Graph g : graphs.values()) {
				remove(g);
			}
			graphs.clear();
			needPack = true;
		} else if (a[0].equals(Utils.LINE_GRAPH)) {
			Graph.type = Utils.LINE_GRAPH;
			repaint();
		} else if (a[0].equals(Utils.BAR_GRAPH)) {
			Graph.type = Utils.BAR_GRAPH;
			repaint();
		}
		
		if (needPack) {
			pack();
		}
	}
	
	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			String line;
			try {
				line = sc.nextLine();
				if (Utils.EXIT.equals(line)) {
					throw new RuntimeException();
				}
			} catch (RuntimeException e) { // IllegalStateException and NoSuchElementException both extends RuntimeException
				sc.close();
				dispose();
				break;
			}
			
			onMessage(line);
		}
	}
}

