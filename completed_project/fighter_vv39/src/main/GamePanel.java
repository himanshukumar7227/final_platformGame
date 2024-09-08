package main;

import java.awt.*;// For all type of Graphics Related work.
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;// Panel is like canvas of the Frame.

import inputs.*;//importing inputs.
import static utils.Constants.PlayerConstants.*;
import static utils.Constants.Directions.*;
import static main.Game.GAME_WIDTH;
import static main.Game.GAME_HEIGHT;;




public class GamePanel extends JPanel{
	
	private MouseInputs mouseInputs;
	private KeyboardInputs keyInput;
	
	private Game game;

	
	public GamePanel(Game game) {
		
		this.game=game;
		mouseInputs=new MouseInputs(this);/*"this is for gamepanal as we have passed a paremeter in keyboard inputs"*/
			//This is important becase without this "mouseInput" will not be used by "addMouseListener"
			//"addMouseMotionListener"
		keyInput=new KeyboardInputs(this);
		addKeyListener(keyInput);/*"this is for gamepanal as we have passed a paremeter in keyboard inputs"*/
			// Through this line we are connecting keyboardinputs with gamepanel.
		addMouseListener(mouseInputs);// For mouse inputs.
		addMouseMotionListener(mouseInputs);// For mouse movements.
		setPanelSize();//This method is for creating a game window.
		
		
	}
	

	private void setPanelSize() {
		Dimension size = new Dimension(GAME_WIDTH,GAME_HEIGHT);
		setPreferredSize(size);
		System.out.println("Size: "+GAME_WIDTH+"X"+GAME_HEIGHT);
		
	}


	public void updateGame() {
		
	}

	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);//this code paints the evrything in the canvas.
		
		game.render(g);
	}
	public Game getGame() {
		return game;
	}
	
}
