import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2i;

public class Actor {
	Sprite thisSprite;
	Sprite target;
	public ArrayList<Projectile> projectiles = new ArrayList<>();
	int iterateNum = 0;

	Clock sfmlClock = new Clock();
	private Time timeHold = sfmlClock.getElapsedTime();
	private Time shootTime = sfmlClock.getElapsedTime();

	protected float timeLimit = 30.0f;
	protected float timeSinceLast;

	protected float shootCd = 5.0f;
	protected float sinceLastShoot;

	protected boolean alive = true;
	protected int[] targetLoc;
	protected float[] mvInc;

	int counter = 0;
	int lastShotCount = 0;
	int cooldown = 30;

	Texture texture1;
	Texture texture2;
	Texture texture3;
	String textureString;

	int xMax = 800;
	int xMin = 0;
	int yMax = 600;
	int yMin = 0;

	int HP = 100;

	Actor() {
		textureString = "Swanson.png";
		makeSprite(textureString);
		populateProjectiles();
		// "Yoself.png"

	}

	Actor(String name) {
		makeSprite(name);
		//populateProjectiles();
	}

	protected void makeSprite(String textString) {
		// Sprite thisSprite;
		texture1 = new Texture();
		texture2 = new Texture();
		texture3 = new Texture();

		// give texture ot sprite and make sprite not null
		try {
			texture1.loadFromFile(Paths.get(textString));
			thisSprite = new Sprite(texture1);

		} catch (IOException a) {
			System.out.println("IO exception");
		} catch (Exception all) {
			System.out.println("Other error");
			System.out.println();
		}

		// set center
		int[] centerPos = getCenter(texture1);
		thisSprite.setOrigin((float) centerPos[0], (float) centerPos[1]);
	}

	public Sprite getSprite() {
		return thisSprite;
	}

	public boolean hasSpr() {
		return thisSprite != null;
	}

	public void move(float x, float y) {
		thisSprite.move(x, y);
	}

	protected int[] randomPosition() {
		Random rand = new Random();
		/**
		 * int x = rand.nextInt(401) - 200 + (400); int y = rand.nextInt(301) -
		 * 150 + (300);
		 */
		int x = rand.nextInt(801);
		int y = rand.nextInt(600);

		// check bounds
		if (x > xMax || x < xMin) {
			x = -1 * x;
		}

		if (y > yMax || y < yMin) {
			y = -1 * y;
		}

		//System.out.println("x:" + x + " y:" + y);

		return new int[] { x, y };
	}

	/**
	 * Get positions for move.
	 * 
	 * @param player
	 * @param position
	 * @return returns x in [0] and y in [1]
	 */
	protected float[] getMoveIncrements(Sprite player, int[] position) {

		// Gets players xy
		float pX = player.getPosition().x;
		float pY = player.getPosition().y;

		// The place we're oging
		int[] thePosition = position;

		int positionX = thePosition[0];
		int positionY = thePosition[1];

		float relX = positionX - pX;
		float relY = positionY - pY;

		float hypo = (float) Math.sqrt(Math.pow(relX, 2) + Math.pow(relY, 2));

		float[] result = new float[3];
		result[0] = 5 * relX / (1 * hypo);
		result[1] = 5 * relY / (1 * hypo);
		result[2] = hypo;

		//System.out.println("movex: " + result[0] + " movey: " + result[1]);

		return result;
	}

	public boolean atPosition(Sprite test, int[] position) {
		float x = test.getPosition().x;
		float y = test.getPosition().y;

		float px = (float) position[0];
		float py = (float) position[1];

		float relx = Math.abs(x - px);
		float rely = Math.abs(y - py);

		// is x same, is y same?
		if (relx < 1 && rely < 1)
			return true;

		return false;
	}

	public int[] getCenter(Texture tex) {
		Vector2i pos = tex.getSize();
		int x = pos.x;
		int y = pos.y;

		int centX = x / 2;
		int centY = y / 2;

		return new int[] { centX, centY };

	}

	public void setTarget(Sprite target) {
		this.target = target;
	}

	public void updateTime() {
		timeHold = sfmlClock.getElapsedTime();
		updateLimit(timeHold, timeLimit, timeSinceLast);
	}

	public boolean timeUp() {
		if (timeHold.asSeconds() > timeSinceLast) {
			return true;
		}
		return false;
	}

	public boolean timeUp(Time timeHold, float timeLimit) {
		if (timeHold.asSeconds() > timeLimit) {
			return true;
		}
		return false;
	}

	public void updateTime(Time timeHolder, Clock clock, float limit, float comparison) {
		timeHolder = sfmlClock.getElapsedTime();
		updateLimit(timeHolder, limit, comparison);
	}

	public void updateLimit(Time timeStart, float limit, float finalComparison) {
		finalComparison = timeStart.asSeconds() + limit;
	}

	public void goToTargetLocation() {
		// Only do stuff if the actor is alive
		if (alive && targetLoc != null && mvInc != null) {
			thisSprite.move(mvInc[0], mvInc[1]);
		}
		if (timeUp()) {
			alive = false;
			thisSprite.setPosition(-9999f, -9999f);
		}
	}

	public void updateMvInc() {
		mvInc = getMoveIncrements(thisSprite, targetLoc);
	}

	public void updateTargetLoc(int[] newLoc) {
		targetLoc = newLoc;
	}

	public void setAlive(boolean value) {
		alive = value;
	}

	private void populateProjectiles() {
		for (int i = 0; i < 11; i++) {
			projectiles.add(new Projectile("pjt.png"));
		}
	}

	public void shoot(int[] target, float[] startPos) {
		if ((counter - lastShotCount) > cooldown) {
			if (iterateNum >= 10) {
				iterateNum = 0;
			} else {
				iterateNum++;
			}

			Projectile shooting = projectiles.get(iterateNum);
			// shooting.shoot(target);
			shooting.fireStart(target, startPos);
			lastShotCount = counter;
		}

	}

	public boolean isAlive() {
		return alive;
	}
	
	public void updateAlive(){
		if (HP < 0)
		{
			alive = false;			
		}
	}

	public int getHp() {
		// TODO Auto-generated method stub
		return HP;
	}

	public void drawProjectiles(RenderWindow window) {
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile proj = projectiles.get(i);
			if (proj.isAlive()) {
				window.draw(proj.getSprite());
			}
		}

	}

	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}

	public void changeHP(int x) {
		HP += x;
		checkHP();
		System.out.println("New HP" + HP);
	}

	public void checkHP() {
		if (HP < 0) {
			alive = false;
		}
	}

	public void testTime() {
		// if (timeHold.asSeconds() > 5.0f) {
		// System.out.println("Greater than 5 secs");
		// }

	}

	public void incCounter() {
		counter++;
	}

}
