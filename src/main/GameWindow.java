package main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;// It is used for making a window appear.

public class GameWindow {
	private JFrame jframe;
	public GameWindow(GamePanel gamPanel) {
		
		jframe=new JFrame();//this will creat a jframe object.
		
		
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//this 
			//helps in closing the programe as the window is closed.
		jframe.add(gamPanel);// This is the gamepannel it is like canvas
			//of the painting frame we will add all esential code in gamepanel.
		jframe.setResizable(false);//This code helps in disabling maximize and minimize option of gamewindow.
		jframe.pack();//this code helps in creating a best fit for window.
		jframe.setLocationRelativeTo(null);
		jframe.setVisible(true);//by this line we are cammanding to make the 
		//Frame or window visible on the screen.
		
		jframe.addWindowFocusListener(new WindowFocusListener(){

			@Override
			public void windowGainedFocus(WindowEvent e) {
				
				
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				System.out.println("again");
				gamPanel.getGame().windowFocusLost();
				
			}
		});
		
	}

}
