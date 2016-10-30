import java.nio.file.Paths;
import java.util.ArrayList;

import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Mouse;

public class Player extends Actor {
	// public ArrayList<Projectile> projectiles = new ArrayList<>();

	SoundBuffer pewBuffer;
	Sound pew;
	
	public Player() {
		super();
		makeSprite("Yoself.png");
		thisSprite.move(400, 400);
		populateProjectilesPlayer();
		initSounds();

	}

	public void logic() {
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile proj = projectiles.get(i);
			if (proj != null && proj.isAlive()) {
				proj.fire();
			}

		}
	}



	public void smartShoot(int index, RenderWindow window) {
		Vector2i mousePosWin;
		Vector2f mousePos;
		mousePosWin = Mouse.getPosition(window);
		mousePos = window.mapPixelToCoords(mousePosWin);

		// Where to start the projectile
		float[] startPos = new float[2];
		startPos[0] = this.getSprite().getPosition().x;
		startPos[1] = this.getSprite().getPosition().y;

		// where to send the projectil
		int[] target = new int[2];
		target[0] = (int) mousePos.x;
		target[1] = (int) mousePos.y;

		this.shoot(target, startPos, index);
	}

	/**
	 * New shoot with index of shoot type.
	 * 
	 * @param target
	 * @param startPos
	 * @param index
	 */
	public void shoot(int[] target, float[] startPos, int index) {
		if ((counter - lastShotCount) > cooldown) {
			if (iterateNum >= 10) {
				iterateNum = 0;
			} else {
				iterateNum++;
			}
			pew.play();
			Projectile shooting = projectiles.get(index);
			shooting.fireStart(target, startPos);
			lastShotCount = counter;
		}

	}
	private void populateProjectilesPlayer() {
		for (int i = 0; i < 10; i++) {
			String str = "" + i + "" + i;
			projectiles.set(i, new Projectile("" + str + ".png"));
			projectiles.get(i).getSprite().setPosition(-100.0f, -100.0f);
		}
	}

	public int findProjectile(Projectile proj) {
		for (int i = 0; i < projectiles.size(); i++) {
			if (proj == projectiles.get(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Doesn't work
	 */
	public void updateMvInc() {
		mvInc = getMoveIncrements(thisSprite, targetLoc);
		mvInc[0] *= 20;
		mvInc[1] *= 20;
	}
	
	public void initSounds() {
		// SoundBuffer buffer = new SoundBuffer();
		try {
			pewBuffer = new SoundBuffer();
			pewBuffer.loadFromFile(Paths.get("playerSound.ogg"));

			//Sound sound = new Sound();
			pew = new Sound();
			pew.setBuffer(pewBuffer);
		} catch (Exception all) {
			;
		}
		// sound.play();
	}

}
