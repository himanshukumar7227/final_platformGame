package entities;


import static utils.Constants.PlayerConstants.GetSpriteAmount;
import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;
import static utils.HelpMethods.IsEntityInWater;
import static utils.Constants.GRAVITY;
import static utils.Constants.ANI_SPEED;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utils.LoadSave;

public class Player extends Entity{
	
	private BufferedImage[][] animations;// image array for loading images.
	private boolean left, right,jump;
	private boolean moving = false,attacking = false;// this variable will be true then only player will move.
	private Playing playing;
	
	private int[][] lvlData;
	private float xDrawOffset = 21*Game.SCALE;
	private float yDrawOffset = 8*Game.SCALE;
	
	//Jumping & Gravity.
	private float jumpSpeed = -2.50f*Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f* Game.SCALE;

	//Status bar Ui.
	private BufferedImage statusBarImg;
	
	private int statusBarWidth=(int)(192*Game.SCALE);
	private int statusBarHeight=(int)(58*Game.SCALE);
	private int statusBarX=(int)(10*Game.SCALE);
	private int statusBarY=(int)(10*Game.SCALE);
	
	private int healthBarWidth=(int)(150*Game.SCALE);
	private int healthBarHeight=(int)(4*Game.SCALE);
	private int healthBarXStart=(int)(34*Game.SCALE);
	private int healthBarYStart=(int)(14*Game.SCALE);
	
	private int powerBarWidth=(int)(104*Game.SCALE);
	private int powerBarHeight=(int)(2*Game.SCALE);
	private int powerBarXStart=(int)(44*Game.SCALE);
	private int powerBarYStart=(int)(34*Game.SCALE);
	private int powerWidth=powerBarWidth;	
	private int powerMaxValue=200;
	private int powerValue=powerMaxValue;
	
	private int healthWidth=healthBarWidth;
		
	private int flipX=0,flipW=1;
	
	private boolean attackChecked=false;
	
	private int tileY=0;
	private boolean powerAttackActive;
	private int powerAttackTick;
	private int powerGrowSpeed=15;
	private int powerGrowTick;	

	public Player(float x, float y,int width,int height,Playing playing) {
		super(x, y,width,height);
		this.playing=playing;
		this.State=IDLE;
		this.maxHealth=100;
		this.currentHealth=maxHealth;
		this.walkSpeed= 1.0f*Game.SCALE;
		loadAnimation();
		initHitbox(20,28);
		initAttackBox();
	}
	
	public void setSpawn(Point spawn) {
		this.x=spawn.x;
		this.y=spawn.y;
		hitbox.x=x;
		hitbox.y=y;
	}
	
	private void initAttackBox() {
		attackBox=new Rectangle2D.Float(x,y,(int)(20*Game.SCALE),(int)(20*Game.SCALE));
		resetAttackBox();
	}


	public void update() {
		updateHealthBar();
		updatePowerBar();
		if(currentHealth<=0) {
			if(State!=DEAD) {
				State=DEAD;
				aniTick=0;
				aniIndex=0;
				playing.setPlayerDying(true);
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
			}else if(aniIndex==GetSpriteAmount(DEAD-1)&&aniTick>=ANI_SPEED-1) {
				playing.setGameOver(true);
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);
			}else
				updateAnimationTick();
			
			
			return;
		}
		
		
		updateattackBox();
		
		updatePos();
		if(moving) {
			checkPotionTouched();
			checkSpikesTouched();
			checkInsideWater();
			tileY=(int)(hitbox.y/Game.TILES_SIZE);
			if(powerAttackActive) {
				powerAttackTick++;
				if(powerAttackTick>=35) {
					powerAttackTick=0;
					powerAttackActive=false;
				}
			}
		}
		if(attacking||powerAttackActive)
			checkAttack();
		updateAnimationTick();
		setAnimation();
		
	}
	
	private void checkInsideWater() {
		if (IsEntityInWater(hitbox, playing.getLevelManager().getCurrentLevel().getLevelData()))
			currentHealth = 0;
	}
	
	private void checkSpikesTouched() {
		playing.checkSpikesTouched(this);
		
	}

	private void checkPotionTouched() {
		playing.checkPotionTouched(hitbox);
		
	}

	private void checkAttack() {
		if(attackChecked || aniIndex!=1)
			return;
		attackChecked=true;
		
		if(powerAttackActive) {
			attackChecked=false;
		}
		
		playing.checkEnemyHit(attackBox);
		playing.checkObjectHit(attackBox);
		playing.getGame().getAudioPlayer().playAttackSound();
	}


	private void updateattackBox() {
		if(right&&left) {
			if(flipW==1) {
				attackBox.x=hitbox.x+hitbox.width+(int)(Game.SCALE*10);
			}else {
				attackBox.x=hitbox.x-hitbox.width+(int)(Game.SCALE*10);
			}
		}
		if(right || powerAttackActive && flipW==1) {
			attackBox.x=hitbox.x+hitbox.width+(int)(Game.SCALE*10);
			
		}else if(left || powerAttackActive && flipW==-1) {
			attackBox.x=hitbox.x-hitbox.width-(int)(Game.SCALE*10);
		}
		attackBox.y=hitbox.y+(Game.SCALE*10);
	}


	private void updateHealthBar() {
		healthWidth=(int)((currentHealth/(float)maxHealth)*healthBarWidth);
		
		
	}
	
	private void updatePowerBar() {
		powerWidth=(int)((powerValue/(float)powerMaxValue)*powerBarWidth);
		
		powerGrowTick++;
		if(powerGrowTick>=powerGrowSpeed) {
			powerGrowTick=0;
			changePower(1);
		}
	}


	public void render(Graphics g,int lvlOffset) {
		g.drawImage(animations[State][aniIndex],
				(int)(hitbox.x - xDrawOffset)-lvlOffset+flipX
				,(int)(hitbox.y - yDrawOffset),width*flipW,height, null);
		//drawHitbox(g,lvlOffset);
		//drawAttackBox(g,lvlOffset);
		drawUI(g);
		

	}

	private void drawUI(Graphics g) {
		//background ui
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
		
		//Health ui
		g.setColor(Color.red);
		g.fillRect(healthBarXStart+statusBarX, healthBarYStart+statusBarY, healthWidth, healthBarHeight);
		
		//power Bar
		g.setColor(Color.yellow);
		g.fillRect(powerBarXStart+statusBarX, powerBarYStart+statusBarY, powerWidth, powerBarHeight);
	}


	private void updateAnimationTick() {
		
		aniTick++;
		if(aniTick>=ANI_SPEED) {
			aniTick=0;
			aniIndex++;
			if(aniIndex>=GetSpriteAmount(State)) {
				aniIndex=0;
				attacking = false;
				attackChecked=false;
			}
		}
	}
	
	private void updatePos() {
		
		moving = false;
		if(jump)
			jump();
		
		if(!inAir)
			if(!powerAttackActive)
				if((!left&&!right)||(right&&left))
					return;
		
		float xSpeed=0;
		
		if(left && !right) { 
			xSpeed-=walkSpeed;
			flipX=width;
			flipW=-1;
		}
		if(right && !left) {
			xSpeed+=walkSpeed;	
			flipX=0;
			flipW=1;
		}
		
		if(powerAttackActive) {
			if((!left &&!right)||(left && right)) 
			{
				if(flipW==-1)
					xSpeed=-walkSpeed;
				else
					xSpeed=walkSpeed;
			}
			xSpeed*=3;
		}
		
		if(!inAir)
			if(!IsEntityOnFloor(hitbox,lvlData))
				inAir=true;
		
		if(inAir && !powerAttackActive) {
			if(canMoveHere(hitbox.x,hitbox.y+airSpeed,hitbox.width,hitbox.height,lvlData)) {
				hitbox.y+=airSpeed;
				airSpeed+= GRAVITY;
				updateXPos(xSpeed);
			}else {
				hitbox.y= GetEntityYPosUnderRoofOraboveFloor(hitbox,airSpeed);
				if(airSpeed>0)
					resetInAir();
				else
					airSpeed =fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}
		}else 
			updateXPos(xSpeed);
		moving= true;
	}
	
	private void jump() {
		if(inAir)
			return;
		playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
		inAir =true;
		airSpeed = jumpSpeed;
		
	}


	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
		
	}


	private void updateXPos(float xSpeed) {
		if(canMoveHere(hitbox.x+xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
			hitbox.x +=xSpeed;
		else {
			hitbox.x=GetEntityxPosNextTotal(hitbox,xSpeed);
			if(powerAttackActive) {
				powerAttackActive=false;
				powerAttackTick=0;
			}
		}
		
	}
	
	public void changeHealth(int value) {
		currentHealth+=value;
		if(currentHealth<=0) {
			currentHealth=0;
		}else if(currentHealth>=maxHealth)
		{
			currentHealth=maxHealth;
		}
	}
	

	public void kill() {
		currentHealth=0;
		
	}
	
	public void changePower(int value) {
		powerValue+=value;
		if(powerValue>=powerMaxValue)
			powerValue=powerMaxValue;
		else if(powerValue<=0)
			powerValue=0;
	}
	

	private void resetAniTick() {
		aniTick=0;
		aniIndex=0;
	}
	
	private void setAnimation() {
		
		int startAni = State;
		
		if(moving) {
			State=RUNNING;
		}
		else
			State=IDLE;
		
		if(inAir) {
			if(airSpeed<0) 
				State =JUMP;
			else
				State =FALLING;
		}
		
		if(powerAttackActive) {
			State=ATTACK_1;
			aniIndex=1;
			aniTick=0;
			return;
		}
		
		if(attacking) {
			State = ATTACK_1;
			if(startAni!=ATTACK_1) {
				aniIndex=1;
				aniTick=0;
				return;
			}
				
		}
		if(startAni != State)
			resetAniTick();
		
	}
	
	private void loadAnimation() {
		
			BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
			
			animations = new BufferedImage[7][8];
			for(int j=0;j<animations.length;j++) {
				for(int i=0;i<animations[j].length;i++) {
					animations[j][i]=img.getSubimage(i*64, j*40, 64,40);
				}
			}	
			
			statusBarImg=LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
			
	}
	
	public void loadLvlDAta(int [][] lvlData) {
		this.lvlData= lvlData;
		if(!IsEntityOnFloor(hitbox,lvlData))
			inAir=true;
	}
	
	public void resetDirBooleans() {
		left=false;
		right=false;
	}

	public void setAttacking(boolean attacking) {
		this.attacking=attacking;
	}

	public boolean isLeft() {
		return left;
	}


	public void setLeft(boolean left) {
		this.left = left;
	}


	public boolean isRight() {
		return right;
	}


	public void setRight(boolean right) {
		this.right = right;
	}
	
	public void setJump(boolean jump) {
		this.jump=jump;
	}


	public void resetAll() {
		resetDirBooleans();
		inAir=false;
		attacking=false;
		moving=false;
		airSpeed=0f;
		State=IDLE;
		currentHealth=maxHealth;
		hitbox.x=x;
		hitbox.y=y;
		
		resetAttackBox();
		
		if(!IsEntityOnFloor(hitbox,lvlData))
			inAir=true;
	}
	
	private void resetAttackBox() {
		if(flipW==1) {
			attackBox.x=hitbox.x+hitbox.width+(int)(Game.SCALE*10);
		}else {
			attackBox.x=hitbox.x-hitbox.width+(int)(Game.SCALE*10);
		}
	}

	public int getTileY() {
		return tileY;
	}

	public void powerAttack(boolean b) {
		if(powerAttackActive)
			return;
		if(powerValue>=60) {
			powerAttackActive=true;
			changePower(-60);
		}
	}

}
