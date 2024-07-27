package ui;

import static utils.Constants.UI.URMButtons.URM_SIZE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utils.LoadSave;

public class GameOverOverlay {

	private Playing playing;
	private BufferedImage img;
	private UrmButton menu,replay;
	private int imgX, imgY, imgW, imgH;
	
	public GameOverOverlay(Playing playing) {
		this.playing=playing;
		createImg();
		createButtons();
	}
	private void createButtons() {
		int menuX=(int)(335*Game.SCALE);
		int replayX=(int)(440*Game.SCALE);
		int y=(int)(195*Game.SCALE);		
		replay = new UrmButton(replayX,y,URM_SIZE,URM_SIZE,1);
		menu = new UrmButton(menuX,y,URM_SIZE,URM_SIZE,2);
		
	}
	private void createImg() {
		img= LoadSave.GetSpriteAtlas(LoadSave.DEATH_SCREEN);
		imgW=(int)(img.getWidth()*Game.SCALE);
		imgH=(int)(img.getHeight()*Game.SCALE);
		imgX=Game.GAME_WIDTH/2-imgW/2;
		imgY=(int)(100*Game.SCALE);
		
	}
	
	public void draw(Graphics g) {
		g.setColor(new Color(0,0,0,200));
		g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
		
		g.drawImage(img, imgX, imgY, imgW, imgH, null);
		menu.draw(g);
		replay.draw(g);
		
//		g.setColor(Color.white);
//		g.drawString("GAME OVER", Game.GAME_WIDTH/2, 150);
//		g.drawString("Press esc to enter Main Menu",Game.GAME_WIDTH/2,300);
	}
	
	public void update() {
		menu.update();
		replay.update();
	}
	
	public void keyPressed(KeyEvent e) {
		
	}
	
	private boolean isIn(UrmButton b,MouseEvent e) {
		return b.getBounds().contains(e.getX(),e.getY());
	}
	
	public void mouseMoved(MouseEvent e) {
		replay.setMouseOver(false);
		menu.setMouseOver(false);
		
		if(isIn(menu,e))
			menu.setMouseOver(true);
		else if(isIn(replay,e))
			replay.setMouseOver(true);
	}
	
	public void mouseReleased(MouseEvent e) {
		if(isIn(menu,e)) {
			if(menu.isMousePressed()) {
				playing.resetAll();
				playing.setGameState(Gamestate.MENU);
			}
				
		}
		else if(isIn(replay,e)) {
			if(replay.isMousePressed()) {
				playing.resetAll();
				playing.getGame().getAudioPlayer().setLevelSong(playing.getLevelManager().getLvlIndex());
			}
		}
		menu.resetBools();
		replay.resetBools();
	}
	
	public void mousePressed(MouseEvent e) {
		if(isIn(menu,e))
			menu.setMousePressed(true);
		else if(isIn(replay,e))
			replay.setMousePressed(true);
	}
}
