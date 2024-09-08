package utils;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Crabby;
import main.Game;
import objects.Cannon;
import objects.GameContainer;
import objects.Potion;
import objects.Projectile;
import objects.Spike;

import static utils.Constants.Directions.LEFT;
import static utils.Constants.EnemyConstants.CRABBY;
import static utils.Constants.ObjectConstants.*;

public class HelpMethods {
	public static boolean canMoveHere(float x,float y, float width,float height,int[][] lvlData) {
		
		if(!IsSolid(x,y,lvlData)) {
			if(!IsSolid(x+width,y+height,lvlData)) {
				if(!IsSolid(x+width,y,lvlData)) {
					if(!IsSolid(x,y+height,lvlData)) {
						return true;
					}
				}
			}
				
		}
		return false;
		
	}
	private static boolean IsSolid(float x,float y, int[][] lvlData) {
		int maxWidth=lvlData[0].length*Game.TILES_SIZE;
		
		if(x<0||x>=maxWidth) {
			return true;
		}
		if(y<0||y>=Game.GAME_HEIGHT) {
			return true;
		}
		float xIndex =x/Game.TILES_SIZE;
		float yIndex =y/Game.TILES_SIZE;
		
		return IsTileSolid((int)xIndex,(int)yIndex,lvlData);

	}
	
	public static boolean isProjectileHitingLevel(Projectile p, int[][] lvlData) {
		
		return IsSolid((int)(p.getHitbox().x+p.getHitbox().width/2),
				(int)(p.getHitbox().y+p.getHitbox().height/2),lvlData);
	}
	
	public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
		int value = lvlData[yTile][xTile];
		
		switch(value) {
		case 11,48,49:
			return false;
		default:
			return true;
		}
	}
	
	public static float GetEntityxPosNextTotal(Rectangle2D.Float hitbox,float xSpeed) {
		
		int currentTile = (int)(hitbox.x/Game.TILES_SIZE);
		
		if(xSpeed>0) {
			//Right
			int tilexPos = currentTile*Game.TILES_SIZE;
			int xOffset =(int)(Game.TILES_SIZE-hitbox.width);
			return tilexPos + xOffset-1;
			
		}else {
			//Left
			return currentTile*Game.TILES_SIZE;
		}
	}
	public static float GetEntityYPosUnderRoofOraboveFloor(Rectangle2D.Float hitbox,float airSpeed) {
		int currentTile = (int)(hitbox.y/Game.TILES_SIZE);
		if(airSpeed >0) {
			//falling - touching floor.
			int tileYPos = currentTile*Game.TILES_SIZE;
			int yOffset =(int)(Game.TILES_SIZE-hitbox.height);
			return tileYPos + yOffset-1;
			
		}else {
			//Jumping
			return currentTile*Game.TILES_SIZE;
		}
	}
	
	public static boolean IsEntityInWater(Rectangle2D.Float hitbox, int[][] lvlData) {
		// Will only check if entity touch top water. Can't reach bottom water if not
		// touched top water.
		if (GetTileValue(hitbox.x, hitbox.y + hitbox.height, lvlData) != 48)
			if (GetTileValue(hitbox.x + hitbox.width, hitbox.y + hitbox.height, lvlData) != 48)
				return false;
		return true;
	}
	
	private static int GetTileValue(float xPos, float yPos, int[][] lvlData) {
		int xCord = (int) (xPos / Game.TILES_SIZE);
		int yCord = (int) (yPos / Game.TILES_SIZE);
		return lvlData[yCord][xCord];
	}
	
	public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox,int[][] lvlData) {
		//check the pixel below bottomleft and bottomright corner.
		if(!IsSolid(hitbox.x,hitbox.y+hitbox.height+1,lvlData))
			if(!IsSolid(hitbox.x+hitbox.width,hitbox.y+hitbox.height+1,lvlData))
				return false;
		return true;
	}
	
	public static boolean IsFloor(Rectangle2D.Float hitbox,float xSpeed,int walkDir,int[][] lvlData) {
		if(walkDir==LEFT)
			return IsSolid(hitbox.x+xSpeed,hitbox.y+hitbox.height+1,lvlData);
		else
			return IsSolid(hitbox.x+xSpeed+hitbox.width,hitbox.y+hitbox.height+1,lvlData);
	}
	
	public static boolean CanCannonSeePlayer(int[][] lvlData,
			Rectangle2D.Float firstHitbox, 
			Rectangle2D.Float secondHitbox, int yTile) {
		int firstXTile = (int) (firstHitbox.x/Game.TILES_SIZE);
		int secondXTile = (int) (secondHitbox.x/Game.TILES_SIZE);
		
		if(firstXTile>secondXTile)
			return IsAllTilesClear(secondXTile,firstXTile,yTile,lvlData);
		else
			return IsAllTilesClear(firstXTile,secondXTile,yTile,lvlData);
		
	}
	
	public static boolean IsAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData) {
		for(int i=0;i<xEnd-xStart; i++)
			if(IsTileSolid(xStart+i,y,lvlData))
				return false;
		return true;
	}
	
	public static boolean IsAllTileWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
		if(IsAllTilesClear(xStart,xEnd,y,lvlData))
			for(int i=0;i<xEnd-xStart; i++) {
				if(!IsTileSolid(xStart+i,y+1,lvlData))
					return false;
			}
		return true;
	}
	
	public static boolean IsSightClear(int[][] lvlData,
			Rectangle2D.Float enemyBox, 
			Rectangle2D.Float playerBox, int yTile) {
		int firstXTile = (int) (enemyBox.x/Game.TILES_SIZE);
		int secondXTile;
		if(IsSolid(playerBox.x,playerBox.y+playerBox.height+1,lvlData))
			secondXTile= (int) (playerBox.x/Game.TILES_SIZE);
		else
			secondXTile= (int) ((playerBox.x+playerBox.width)/Game.TILES_SIZE);
		
		if(firstXTile>secondXTile)
			return IsAllTileWalkable(secondXTile,firstXTile,yTile,lvlData);
		else
			return IsAllTileWalkable(firstXTile,secondXTile,yTile,lvlData);
	}
	
	public static int[][] GetLevelData(BufferedImage img){
		
		int[][] lvlData = new int [img.getHeight()][img.getWidth()];
		for (int j=0;j<img.getHeight();j++) {
			for(int i=0;i<img.getWidth();i++) {
				Color color =new Color(img.getRGB(i, j));
				int value = color.getRed();
				if(value>=50) {
					value = 0;
				}
				lvlData[j][i] = value;
			}
		}
		
		return lvlData;
	}
	
	public static ArrayList<Crabby> GetCrabbs(BufferedImage img){
		ArrayList<Crabby> list = new ArrayList<>();
		
		for (int j=0;j<img.getHeight();j++) 
			for(int i=0;i<img.getWidth();i++) {
				Color color =new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if(value==CRABBY) 
					list.add(new Crabby(i*Game.TILES_SIZE,j*Game.TILES_SIZE));
			}
		return list;
	}
	
	public static Point GetPlayerSpawn(BufferedImage img) {
		for (int j=0;j<img.getHeight();j++) 
			for(int i=0;i<img.getWidth();i++) {
				Color color =new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if(value==100) 
					return new Point(i*Game.TILES_SIZE,j*Game.TILES_SIZE);
			}
		return new Point(1*Game.TILES_SIZE,1*Game.TILES_SIZE);
	}

	public static ArrayList<Potion> GetPotions(BufferedImage img){
		ArrayList<Potion> list = new ArrayList<>();
		
		for (int j=0;j<img.getHeight();j++) 
			for(int i=0;i<img.getWidth();i++) {
				Color color =new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if(value==RED_POTION||value==BLUE_POTION) 
					list.add(new Potion(i*Game.TILES_SIZE,j*Game.TILES_SIZE,value));
			}
		return list;
	}
	
	public static ArrayList<GameContainer> GetContainers(BufferedImage img){
		ArrayList<GameContainer> list = new ArrayList<>();
		
		for (int j=0;j<img.getHeight();j++) 
			for(int i=0;i<img.getWidth();i++) {
				Color color =new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if(value==BOX||value==BARREL) 
					list.add(new GameContainer(i*Game.TILES_SIZE,j*Game.TILES_SIZE,value));
			}
		return list;
	}
	public static ArrayList<Spike> GetSpikes(BufferedImage img) {
		ArrayList<Spike> list = new ArrayList<>();
		
		for (int j=0;j<img.getHeight();j++) 
			for(int i=0;i<img.getWidth();i++) {
				Color color =new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if(value==SPIKE) 
					list.add(new Spike(i*Game.TILES_SIZE,j*Game.TILES_SIZE,SPIKE));
			}
		return list;
	}
	
	public static ArrayList<Cannon> GetCannons(BufferedImage img) {
		ArrayList<Cannon> list = new ArrayList<>();
		
		for (int j=0;j<img.getHeight();j++) 
			for(int i=0;i<img.getWidth();i++) {
				Color color =new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if(value==CANNON_LEFT||value==CANNON_RIGHT) 
					list.add(new Cannon(i*Game.TILES_SIZE,j*Game.TILES_SIZE,value));
			}
		return list;
	}
}
