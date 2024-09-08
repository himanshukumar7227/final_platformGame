package levels;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Crabby;
import main.Game;
import objects.Cannon;
import objects.GameContainer;
import objects.Potion;
import objects.Spike;
import utils.HelpMethods;

import static utils.HelpMethods.GetLevelData;
import static utils.HelpMethods.GetCrabbs;
import static utils.HelpMethods.GetPlayerSpawn;

public class Level {
	
	private BufferedImage img;
	private int[][] lvlData;
	private ArrayList<Crabby> crabs;
	private ArrayList<Spike> spikes;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> gameContainers;
	private ArrayList<Cannon> cannons;
	private int lvlTilesWide;
	private int maxTileOffset;
	private int maxLvlOffsetX;
	private Point playerSpawn;
	
	public Level(BufferedImage img) {
		this.img=img;
		createLevelData();
		createEnemies();
		createPotions();
		createContainers();
		createSpikes();
		createCannons();
		calculateLvlOffsets();
		calcPlayerSpawn();
	}
	
	private void createCannons() {
		cannons=HelpMethods.GetCannons(img);
		
	}

	private void createSpikes() {
		spikes=HelpMethods.GetSpikes(img);
	}

	private void createContainers() {
		gameContainers=HelpMethods.GetContainers(img);
		
	}

	private void createPotions() {
		potions=HelpMethods.GetPotions(img);
		
	}

	private void calcPlayerSpawn() {
		playerSpawn=GetPlayerSpawn(img);
		
	}

	private void calculateLvlOffsets() {
		lvlTilesWide=img.getWidth();
		maxTileOffset=lvlTilesWide-Game.TILES_IN_WIDTH;
		maxLvlOffsetX= Game.TILES_SIZE*maxTileOffset;
		
	}

	private void createEnemies() {
		crabs = GetCrabbs(img);
		
	}

	private void createLevelData() {
		lvlData = GetLevelData(img);
		
	}

	public int getSpriteIndex(int x,int y) {
		return lvlData[y][x];
	}
	
	public int[][] getLevelData(){
		return lvlData;
	}
	public int getLvlOffset() {
		return maxLvlOffsetX;
	}
	
	public Point getPlayerSpawn() {
		return playerSpawn;
	}
	
	public ArrayList<Crabby> getCrabs(){
		return crabs;
	}
	
	public ArrayList<GameContainer> getGameContainers(){
		return gameContainers;
	}
	
	public ArrayList<Potion> getPotions(){
		return potions;
	}
	
	public ArrayList<Spike> getSpike(){
		return spikes;
	}
	
	public ArrayList<Cannon> getCannons(){
		return cannons;
	}
	
}
