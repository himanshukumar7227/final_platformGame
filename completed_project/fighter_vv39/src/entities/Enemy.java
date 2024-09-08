package entities;

import static utils.Constants.EnemyConstants.*;
import static utils.HelpMethods.*;

import java.awt.geom.Rectangle2D;
import static utils.Constants.GRAVITY;
import static utils.Constants.ANI_SPEED;

import static utils.Constants.Directions.*;

import main.Game;

public abstract class Enemy extends Entity{

	protected int enemyType;
	protected boolean firstUpdate=true;	

	protected int walkDir=LEFT;
	protected int tileY;
	protected float attackDistance = Game.TILES_SIZE;
	protected boolean active=true;
	protected boolean attackChecked;
			
	public Enemy(float x, float y, int width, int height, int ememyType) {
		super(x, y, width, height);
		this.walkSpeed=0.35f*Game.SCALE;
		maxHealth=GetMaxHealth(enemyType);
		currentHealth=maxHealth;
		
	}
	
	protected void updateAnimationTick() {
		aniTick++;
		if(aniTick>=ANI_SPEED) {
			aniTick=0;
			aniIndex++;
			if(aniIndex>=GetSpriteAmount(enemyType, State)) {
				aniIndex=0;
				
				switch(State) {
				case ATTACK,HIT->State=IDLE;
				case DEAD->active=false;
				}
				
			}
		}
	}
	
	protected void firstUpdateCheck(int[][] lvlData) {
		if(!IsEntityOnFloor(hitbox,lvlData))
			inAir =true;
		firstUpdate = false;
	}
	
	protected void move(int[][] lvlData) {
		float xSpeed=0;
		if(walkDir== LEFT)
			xSpeed=-walkSpeed;
		else
			xSpeed=walkSpeed;
		
		if(canMoveHere(hitbox.x+xSpeed,hitbox.y,hitbox.width,hitbox.height,lvlData))
			if(IsFloor(hitbox,xSpeed,walkDir,lvlData)) {
				hitbox.x+=xSpeed;
				return;
			}
		chengeWalkDir();
	}
	
	protected void turnTowardsPlayer(Player player) {
		if(player.hitbox.x>hitbox.x)
			walkDir=RIGHT;
		else
			walkDir=LEFT;
		
	}
	
	protected boolean canSeePlayer(int[][] lvlData,Player player) {
		int playerTileY=(int)(player.getHitbox().y/Game.TILES_SIZE);
		if(playerTileY==tileY)
			if(isPlayerInRange(player)){
				if(IsSightClear(lvlData,hitbox,player.hitbox,tileY))
					return true;
		}
		return false;
	}
	
	protected boolean isPlayerInRange(Player player) {
		int absValue =(int)Math.abs(player.hitbox.x-hitbox.x);
		return absValue<=attackDistance*5;
	}
	
	protected boolean isPlayerCloserForAttack(Player player) {
		int absValue =(int)Math.abs(player.hitbox.x-hitbox.x);
		return absValue<=attackDistance;
	}

	protected void newState(int enemyState) {
		this.State=enemyState;
		aniTick=0;
		aniIndex=0;
	}
	
	public void hurt(int amount) {
		currentHealth-=amount;
		if(currentHealth<=0)
			newState(DEAD);
		else
			newState(HIT);
	}
	
	protected void checkEnemyHit(Rectangle2D.Float attackBox,Player player) {
		if(attackBox.intersects(player.hitbox))
			player.changeHealth(-GetEnemyDmg(enemyType));
		attackChecked=true;
			
	}
	
	protected void updateInAir(int[][] lvlData) {
		if(canMoveHere(hitbox.x,hitbox.y+airSpeed,hitbox.width,hitbox.height,lvlData)) {
			hitbox.y+=airSpeed;
			airSpeed+=GRAVITY;
		}
		else {
			inAir=false;
			hitbox.y=GetEntityYPosUnderRoofOraboveFloor(hitbox,airSpeed);
			tileY=(int)(hitbox.y/Game.TILES_SIZE);
		}
	}
	
	protected void chengeWalkDir() {
		if(walkDir ==LEFT) {
			walkDir=RIGHT;
			
		}
		else
			walkDir=LEFT;
		
	}
	
	public void resetEnemy() {
		hitbox.x=x;
		hitbox.y=y;
		firstUpdate=true;
		currentHealth=maxHealth;
		newState(IDLE);
		active=true;
		airSpeed=0;
	}

	public boolean isActive() {
		return active;
	}
	

}
