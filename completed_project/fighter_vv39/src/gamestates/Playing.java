package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utils.LoadSave;
import static utils.Constants.Environments.*;

public class Playing extends State implements Statemethods{
	
	
	private Player player;
	private LevelManager levelManager;
	private EnemyManager enemyManager;
	private ObjectManager objectManager;
	private PauseOverlay pouseOverlay;
	private GameOverOverlay gameOverOverlay;
	private LevelCompletedOverlay levelCompletedOverlay;
	private boolean paused = false;
	
	private int xLvlOffset;
	private int leftBorder = (int)(0.2*Game.GAME_WIDTH);
	private int rightBorder=(int)(0.8*Game.GAME_WIDTH);
	private int maxLvlOffsetX;
	
	private BufferedImage backgroundImg;
	private BufferedImage bigCloud,smallCloud;
	private int[] smallCloudPos;
	private Random rnd =new Random();
	
	private boolean gameOver;
	private boolean lvlCompleted;
	private boolean playerDying=false;
	
	public Playing(Game game) {
		super(game);
		initClasses();
		backgroundImg=LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
		bigCloud=LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
		smallCloud=LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
		smallCloudPos= new int[8];
		for(int i=0;i<smallCloudPos.length;i++)
			smallCloudPos[i] = (int)((65*Game.SCALE)+rnd.nextInt((int)(145*Game.SCALE)));
		
		calcLvlOffset();
		loadStartLevel();
		
	}
	
	private void initClasses() {
		levelManager = new LevelManager(game);
		enemyManager = new EnemyManager(this);
		objectManager =new ObjectManager(this);
		
		player = new Player(200,200,(int)(64*Game.SCALE),(int)(48*Game.SCALE), this);
		player.loadLvlDAta(levelManager.getCurrentLevel().getLevelData());
		player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
		
		pouseOverlay = new PauseOverlay(this);
		gameOverOverlay=new GameOverOverlay(this);
		levelCompletedOverlay =new LevelCompletedOverlay(this);
	}
	
	public void loadNextlevel() {
		levelManager.loadNextLevel();
		player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
		resetAll();
	}
	
	private void loadStartLevel() {
		enemyManager.loadEnemies(levelManager.getCurrentLevel());
		objectManager.loadObjects(levelManager.getCurrentLevel());
	}


	private void calcLvlOffset() {
		maxLvlOffsetX= levelManager.getCurrentLevel().getLvlOffset();
		
	}



	@Override
	public void update() {
		
		if(paused) {
			pouseOverlay.update();
		}else if(lvlCompleted) {
			levelCompletedOverlay.update();
		}else if(gameOver){
			gameOverOverlay.update();
		}else if(playerDying) {
			player.update();
		}else{
			levelManager.update();
			objectManager.update(levelManager.getCurrentLevel().getLevelData(),player);
			player.update();
			enemyManager.update(levelManager.getCurrentLevel().getLevelData(),player);
			CheckCloseToBorder();
		}
		
	}


	private void CheckCloseToBorder() {
		int playerX=(int)player.getHitbox().x;
		int diff=playerX-xLvlOffset;
		
		if(diff>rightBorder)
			xLvlOffset+=diff-rightBorder;
		else if(diff<leftBorder)
			xLvlOffset+=diff-leftBorder;
		
		if(xLvlOffset>maxLvlOffsetX)
			xLvlOffset=maxLvlOffsetX;
		else if(xLvlOffset<0)
			xLvlOffset=0;
		
	}


	@Override
	public void test() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		
		drawClouds(g);
		
		levelManager.draw(g,xLvlOffset);
		player.render(g,xLvlOffset);
		enemyManager.draw(g,xLvlOffset);
		objectManager.draw(g, xLvlOffset);
		
		if(paused) {
			g.setColor(new Color(0,0,0,170));
			g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
			pouseOverlay.draw(g);
			
		}else if(gameOver)
			gameOverOverlay.draw(g);
		else if(lvlCompleted)
			levelCompletedOverlay.draw(g);
		
	}


	private void drawClouds(Graphics g) {
		for(int i=0;i<3;i++)
			g.drawImage(bigCloud, i*BIG_CLOUD_WIDTH-(int)(xLvlOffset*0.3), (int)(204*Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);
		
		for(int i=0;i<smallCloudPos.length;i++)
			g.drawImage(smallCloud, 4*i*SMALL_CLOUD_WIDTH-(int)(xLvlOffset*0.7), smallCloudPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
		
	}


	public void resetAll() {
		//reset playing, enemy,lvl etc;
		gameOver=false;
		paused=false;
		lvlCompleted=false;
		playerDying=false;
		player.resetAll();
		enemyManager.resetAllEnemies();
		objectManager.resetAllObjects();
		
	}
	
	public void setGameOver(boolean gameOver) {
		this.gameOver=gameOver;
	}
	
	public void checkObjectHit(Rectangle2D.Float attackBox) {
		objectManager.checkObjectHit(attackBox);
		
	}
	
	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		enemyManager.checkEnemyHit(attackBox);
		
	}
	
	public void checkPotionTouched(Rectangle2D.Float hitbox) {
		objectManager.checkObjectTouched(hitbox);
		
	}
	

	public void checkSpikesTouched(Player p) {
		objectManager.checkSpikesTouched(p);
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {
		if(!gameOver) {
			if(e.getButton()==MouseEvent.BUTTON1)
				player.setAttacking(true);
			else if(e.getButton()==MouseEvent.BUTTON3)
				player.powerAttack(true);
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if(!gameOver)
			if(paused)
				pouseOverlay.mouseDragged(e);
	}


	@Override
	public void mousePressed(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pouseOverlay.mousePressed(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mousePressed(e);
		}else
			gameOverOverlay.mousePressed(e);
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pouseOverlay.mouseReleased(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mouseReleased(e);
		}else
			gameOverOverlay.mouseReleased(e);
		
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pouseOverlay.mouseMoved(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mouseMoved(e);
		}else
			gameOverOverlay.mouseMoved(e);
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if(gameOver)
			gameOverOverlay.keyPressed(e);
		else
			switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:
				player.setJump(true);
				break;
			case KeyEvent.VK_RIGHT:
				player.setRight(true);
				break;
			case KeyEvent.VK_LEFT:
				player.setLeft(true);
				break;
			case KeyEvent.VK_W:
				player.setJump(true);
				break;
			case KeyEvent.VK_A:
				player.setLeft(true);
				break;
			case KeyEvent.VK_D:
				player.setRight(true);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(true);
				break;
			case KeyEvent.VK_ESCAPE:
				paused=!paused;
				break;
			}
		
	}


	@Override
	public void keyRealeased(KeyEvent e) {
		if(!gameOver)
			switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:
				player.setJump(false);
				break;
			case KeyEvent.VK_RIGHT:
				player.setRight(false);
				break;
			case KeyEvent.VK_LEFT:
				player.setLeft(false);
				break;
			case KeyEvent.VK_W:
				player.setJump(false);
				break;
			case KeyEvent.VK_A:
				player.setLeft(false);
				break;
			case KeyEvent.VK_D:
				player.setRight(false);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(false);
				break;
			}
		
	}
	
	public void setLevelCompleted(boolean lvlCompleted) {
		this.lvlCompleted=lvlCompleted;
		if(lvlCompleted)
			game.getAudioPlayer().lvlCompleted();
	}
	
	public void setMaxLvlOffset(int lvlOffset) {
		this.maxLvlOffsetX=lvlOffset;
	}
	
	public void unpauseGame() {
		paused= false;
	}
	
	public void windowFocusLost() {
		player.resetDirBooleans();
	}
	
	public Player getPlayer() {
		return player;
	}

	public EnemyManager getEnemyManager() {
		return enemyManager;
	}
	
	public ObjectManager getObjectMangar() {
		return objectManager;
	}
	
	public LevelManager getLevelManager() {
		return levelManager;
	}

	public void setPlayerDying(boolean playerDying) {
		this.playerDying=playerDying;
		
	}

}
