import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;

public class Swanson extends Actor {
	float totalWalked = 0;
	int[] targetPosition;
	int[] spritePostion;
	int[] shootAt;
	int[] binary = Calc.getBin(0);
	int dec = -1;
	float[] moveInc;
	SoundBuffer buffer = new SoundBuffer();
	Sound trol;
	
	Actor trollface;
	boolean showTroll = false;
	

	public ArrayList<Actor> displayRegister = new ArrayList<>();
	public Actor[] display = new Actor[4];

	int zeroIndex = 0;
	int oneIndex = 1;
	boolean atkPhase = false;
	boolean transition = false;
	boolean movePhase = true;

	int attackCounter = 0;

	public Swanson() {
		targetPosition = new int[2];
		spritePostion = new int[2];
		moveInc = new float[3];
		cooldown = 5;
		populateProjectilesSwan();
		populateDisplayRegister();
		updateDisplay();
		thisSprite.setPosition(300.0f, 20.0f);
		trollface = new Actor("Troll Face.png");
		trollface.getSprite().setPosition(300.0f, 20.0f);
		initSounds();
		
	}

	public void aI() {
		// Get a new walking position if needed

		// Handle movements of projectiles [that are active]
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile proj = projectiles.get(i);
			if (proj != null && proj.isAlive()) {
				proj.fire();
			}

		}

		// MOVE PHASE
		if (movePhase) {
			targetPosition = randomPosition();
			movePhase = false;

			// setupt walking
			transition = true;
			moveInc = getMoveIncrements(thisSprite, targetPosition);

		}

		// TRANSITION PHASE
		if (transition) {
			if (atPosition(thisSprite, targetPosition)) {
				transition = false;
				totalWalked += moveInc[2];
				// check if needs to attack
				if (totalWalked >= 800) {
					atkPhase = true;
					transition = false;
				} else {
					movePhase = true;
				}

			}
			if (outOfBounds(moveInc[0], moveInc[1])) {
				transition = false;
				movePhase = true;
				totalWalked += 400;

				if (totalWalked >= 800) {
					atkPhase = true;
					if(getRandOneToTen() == 2){
						showTroll = true;
						trol.play();
					}
					transition = false;
				} else {
				}
			}
			move(moveInc[0], moveInc[1]);
		}
		// ATTACK PHASE
		if (atkPhase) {
			if (attackCounter == 0) {
				dec = Calc.getRandom();
				System.out.println("shooting: " + dec);
				binary = Calc.getBin(dec);
				updateDisplay();
			}

			transition = false;
			movePhase = false;
			boolean flag = attack();
			if (flag) {
				atkPhase = false;
				movePhase = true;
				showTroll = false;
			}
		}

	}

	protected boolean attack() {
		// get target position

		if (counter - lastShotCount <= cooldown) {
			return false;
		}
		shootAt = new int[2];
		shootAt[0] = (int) target.getPosition().x;
		shootAt[1] = (int) target.getPosition().y;

		// use swansons positoin as origin
		float[] ori = new float[2];
		ori[0] = thisSprite.getPosition().x;
		ori[1] = thisSprite.getPosition().y;

		// shoot project towards target
		// System.out.println("attack hit in swanson");
		shootSwan(shootAt, ori);

		attackCounter++;
		// System.out.println("atk counter:" + attackCounter);
		if (attackCounter >= 4) {
			attackCounter = 0;
			return true;
		}
		return false;

	}

	public void shootSwan(int[] target, float[] startPos) {
		if ((counter - lastShotCount) > cooldown) {
			if (iterateNum >= 10) {
				iterateNum = 0;
			} else {
				iterateNum++;
			}

			// 0 1 2 3<- index
			// 1 0 0 0 <- binary
			int index = -1;
			if (binary[attackCounter] == 1) {
				index = getOne();
			} else {
				index = getZero();
			}

			Projectile shooting = projectiles.get(index);
			// shooting.shoot(target);
			shooting.fireStart(target, startPos);
			lastShotCount = counter;
		}

	}

	protected boolean outOfBounds(float x, float y) {
		float px = thisSprite.getPosition().x;
		float py = thisSprite.getPosition().y;

		float newX = px + x;
		float newY = py + y;

		if (newX > xMax || newX < xMin) {
			return true;
		}
		if (newY > yMax || newY < yMin) {
			return true;
		}
		return false;

	}

	public void applyDamageMaybe(int num) {
		if (num == dec) {
			HP -= 20;
			System.out.println("YOU HURT HIM");
		}
	}

	private void populateProjectilesSwan() {
		projectiles = new ArrayList<>(100);
		for (int i = 0; i < 50; i++) {
			projectiles.add(new Projectile("" + i % 2 + ".png"));
			projectiles.get(i).getSprite().setPosition(-100.0f, -100.0f);
		}
	}

	private int getOne() {
		int temp = oneIndex;
		oneIndex += 2;
		if (oneIndex > 31) {
			oneIndex = 1;
		}
		return temp;
	}

	private int getZero() {
		int temp = zeroIndex;
		zeroIndex += 2;
		if (zeroIndex > 32) {
			zeroIndex = 0;
		}
		return temp;
	}

	private void populateDisplayRegister() {
		displayRegister = new ArrayList<>(100);
		for (int i = 0; i < 10; i++) {
			displayRegister.add(new Projectile("" + i % 2 + ".png"));
		}
	}

	private void updateDisplay() {
		int oneIndex = 1;
		int zeroIndex = 0;
		float xOffset = 350f;
		float yOffset = 50f;
		for (int i = 0; i < 4; i++) {
			if (binary[i] == 1) {
				display[i] = displayRegister.get(oneIndex);
				oneIndex += 2;
			} else {
				display[i] = displayRegister.get(zeroIndex);
				zeroIndex += 2;
			}
			display[i].getSprite().setPosition(xOffset, yOffset);
			xOffset += 40;
		}

	}

	public void drawDisplay(RenderWindow window) {
		for (int i = 0; i < 4; i++) {
			window.draw(display[i].getSprite());
		}
	}

	public void initSounds() {
		// SoundBuffer buffer = new SoundBuffer();
		try {
			buffer = new SoundBuffer();
			buffer.loadFromFile(Paths.get("swansonHit.ogg"));

			//Sound sound = new Sound();
			trol = new Sound();
			trol.setBuffer(buffer);
		} catch (Exception all) {
			;
		}
		// sound.play();
	}

	public int getRandOneToTen() {
		Random rand = new Random();
		int result = rand.nextInt(3);

		return result;
	}
	
	public void move(float x, float y) {
		thisSprite.move(x, y);
		trollface.move(x, y);
	}
	
	public Sprite getTrollSprite(){
		return trollface.getSprite();
	}
	
	public boolean showTroll(){
		return showTroll;		
	}
}
