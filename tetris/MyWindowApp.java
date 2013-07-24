package tetris;

import javax.swing.*;
import java.awt.*;

public class MyWindowApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		
		ProcessingPanel pPanel = new ProcessingPanel();
		JFrame myWindow = new JFrame("Tetris");
		myWindow.setLayout(new BorderLayout());

		myWindow.add(pPanel, BorderLayout.CENTER);
		
		myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myWindow.setSize(640, 480);
		myWindow.setVisible(true);
		pPanel.init();
	}
}
