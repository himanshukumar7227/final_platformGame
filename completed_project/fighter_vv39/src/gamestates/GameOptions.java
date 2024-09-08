package gamestates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.URMButtons.*;

import main.Game;
import ui.AudioOptions;
import ui.PauseButton;
import ui.UrmButton;
import utils.LoadSave;

public class GameOptions extends State implements Statemethods {

	private AudioOptions audioOptions;
	private BufferedImage backgroundImg,optionBackground;
	private int bgX,bgY,bgH,bgW;
	private UrmButton menuB;
	
	public GameOptions(Game game) {
		super(game);
		loadImgs();
		loadButtons();
		audioOptions=game.getAudioOptions();
	}

	private void loadButtons() {
		int menuX=(int)(387*Game.SCALE);
		int menuY=(int)(325*Game.SCALE);
		
		menuB=new UrmButton(menuX,menuY,URM_SIZE,URM_SIZE,2);
	}

	private void loadImgs() {
		backgroundImg= LoadSave.GetSpriteAtlas(LoadSave.OPTION_BACK);
		optionBackground=LoadSave.GetSpriteAtlas(LoadSave.OPTION_BACK_BOX);
		
		bgW=(int)(optionBackground.getWidth()*Game.SCALE);
		bgH=(int)(optionBackground.getHeight()*Game.SCALE);
		bgX=Game.GAME_WIDTH/2-bgW/2;
		bgY=(int)(33*Game.SCALE);
		
	}

	@Override
	public void update() {
		menuB.update();
		audioOptions.update();
	}

	@Override
	public void test() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		g.drawImage(optionBackground, bgX, bgY, bgW, bgH, null);
	
		menuB.draw(g);
		audioOptions.draw(g);
	}

	public void mouseDragged(MouseEvent e) {
		audioOptions.mouseDragged(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(isIn(e,menuB)) {
			menuB.setMousePressed(true);
		}else
			audioOptions.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(isIn(e,menuB)) {
			if(menuB.isMousePressed())
				Gamestate.state=Gamestate.MENU;
		}else
			audioOptions.mouseReleased(e);
		
		menuB.resetBools();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		menuB.setMouseOver(false);
		
		if(isIn(e,menuB)) {
			menuB.setMouseOver(true);
		}else
			audioOptions.mouseMoved(e);

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
			Gamestate.state=Gamestate.MENU;
	}

	@Override
	public void keyRealeased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	private boolean isIn(MouseEvent e,PauseButton b) {
		return b.getBounds().contains(e.getX(),e.getY());
	}
}
