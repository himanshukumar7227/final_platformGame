package main;

import java.awt.Graphics;

import audio.AudioPlayer;
import gamestates.GameOptions;
import gamestates.Gamestate;
import gamestates.Menu;
import gamestates.Playing;
import ui.AudioOptions;

public class Game implements Runnable{
	
	private GameWindow gameWindow;//calling gamewindow
	private GamePanel gamePanel;//calling gamepanel
	
	private Thread gameThread;//creating a thread for gameloop.
	private final int FPS_SET=120;
	private final int UPS_SET=200;
	
	private Playing playing ;
	private Menu menu;
	private AudioOptions audioOptions;
	private GameOptions gameOptions;
	private AudioPlayer audioPlayer;
	
	public final static int TILES_DEFAULT_SIZE = 32;
	public final static float SCALE = 1.5f;
	public final static int TILES_IN_WIDTH = 26;
	public final static int TILES_IN_HEIGHT = 14;
	public final static int TILES_SIZE = (int)(TILES_DEFAULT_SIZE * SCALE);
	public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
	public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;
	
	private void initClasses() {
		audioOptions=new AudioOptions(this);
		audioPlayer =new AudioPlayer();
		menu =new Menu(this);
		playing = new Playing(this);
		gameOptions=new GameOptions(this);
		
	}
	
	public Game() {
		
		initClasses();
		gamePanel=new GamePanel(this);//gamePanel called.
		gameWindow=new GameWindow(gamePanel);//gamewindow is created.
		gamePanel.setFocusable(true);
		gamePanel.requestFocus();// this line helps in inputs to apply in the game.
		startGameloop();
		
		
	}

	private void startGameloop() {
		/*
		 * method simply starts the thread for gameloop.
		 */
		gameThread = new Thread(this);
		gameThread.start();//here we are starting the thread.
		
	}
	
	public void update() {
		switch(Gamestate.state) {
		case MENU:
			menu.update();
			break;
		case OPTIONS:
			gameOptions.update();
			break;
		case PLAYING:
			playing.update();
			break;
		case QUIT:
		default:
			break;
		
		}
	}
	public void render(Graphics g) {
		switch(Gamestate.state) {
		case MENU:
			menu.draw(g);
			break;
		case PLAYING:
			playing.draw(g);
			break;
		case OPTIONS:
			gameOptions.draw(g);
			break;
		case QUIT:
		default:
			System.exit(0);
			break;
		
		}
		
	}

	@Override
	public void run() {
		/*
		 * We are doing this to make game loop stable.
		 * 
		 * run method is predefined method for 
		 * starting a thread from this the game loop starts
		 */
		
		double timePerFrame = 1000000000.0/FPS_SET;//here we are deving 
			//1 second from frame per second.
		double timePerUpdate = 1000000000.0/UPS_SET;
		
		long previousTime =System.nanoTime();
		
		int frames = 0;
		int updates= 0;
		
		long lastCheck=System.currentTimeMillis();
		
		double deltaU=0;
		double deltaF = 0;
		while(true) {
			
			
			long currentTime = System.nanoTime();
			deltaU+=(currentTime - previousTime)/timePerUpdate;
			deltaF+=(currentTime - previousTime)/timePerFrame;
			previousTime=currentTime;
			if(deltaU>=1) {
				update();
				updates++;
				deltaU--;
			}
			
			if(deltaF>=1) {
				gamePanel.repaint();
				frames++;
				deltaF--;
				
			}
									
			if(System.currentTimeMillis()-lastCheck>=1000) {
				/**
				 * this if stamente is used to record the no of frames which are printed on the screen in a millisecond.
				 * Here if the as the lastCheck increase from the current time frames will be reset.
				 */
				lastCheck=System.currentTimeMillis();//here we are again store the current time in lastChack.
				System.out.println("FPS: "+frames+" | UPS: "+updates);//here we are printing the no of frames painted by the system.
				frames=0;//here we are again reseting the frames count to 0 so that we get exact count and not counts from very be
					//begning.
				updates=0;
			}
			
		}
		
	}
	
	public void windowFocusLost() {
		if(Gamestate.state==Gamestate.PLAYING)
			playing.getPlayer().resetDirBooleans();
	}
	
	
	public Menu getMenu() {
		return menu;
	}
	public Playing getPlaying() {
		return playing;
	}
	
	public AudioOptions getAudioOptions() {
		return audioOptions;
	}
	public GameOptions getGameOptions() {
		return gameOptions;
	}
	
	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}

}
