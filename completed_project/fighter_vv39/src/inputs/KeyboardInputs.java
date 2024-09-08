package inputs;



import java.awt.event.KeyEvent;//for key events.
import java.awt.event.KeyListener;//for key listening.

import gamestates.Gamestate;
import main.GamePanel;//importing gamePanel for linking and passing the keyboard commands.

public class KeyboardInputs implements KeyListener{
	
	private GamePanel gamePanel;
	
	public KeyboardInputs(GamePanel gamePanel) {
		
		this.gamePanel=gamePanel;//this line is required so that we can use the methods of gamepannel to change the 
			//rectangle position.
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(Gamestate.state) {
		case MENU:
			gamePanel.getGame().getMenu().keyPressed(e);
			break;
		case PLAYING:
			gamePanel.getGame().getPlaying().keyPressed(e);
			break;
		case OPTIONS:
			gamePanel.getGame().getPlaying().keyPressed(e);
			break;
		default:
			break;
		
		}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(Gamestate.state) {
		case MENU:
			gamePanel.getGame().getMenu().keyRealeased(e);
			break;
		case PLAYING:
			gamePanel.getGame().getPlaying().keyRealeased(e);
			break;
		default:
			break;
		
		}
		
	}

}
