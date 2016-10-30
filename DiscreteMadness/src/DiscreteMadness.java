
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

/**
 *
 * @author Matt Stone
 * @author Brenner Harris
 * @version 1.0
 *
 */

public class DiscreteMadness {

	// FIELDS
	boolean start = true;
	boolean winB = false;
	boolean loseB = false;
	boolean playFinishHim = true;

	SoundBuffer discreteBuffer;
	SoundBuffer finishHimBuffer;
	SoundBuffer musicBuffer;

	Sound discrete;
	Sound finishHim;
	Sound Music;

	public static void main(String[] args) throws IOException {
		DiscreteMadness game = new DiscreteMadness();
		game.play(args);
	}

	public void play(String[] args) throws IOException {
		// init sounds
		initSounds();

		// Create the main window
		float windowX = 800.0f;
		float windowY = 600.0f;
		RenderWindow window = new RenderWindow(new VideoMode((int) windowX, (int) windowY), "JSFML window");
		//window.setFramerateLimit(60);
		boolean atPosition = false;

		// Create the viewport
		View viewControlTarget;
		viewControlTarget = new View(new Vector2f(windowX / 2, windowY / 2), new Vector2f(windowX, windowY));

		// SPRITES
		Swanson swanson = new Swanson();
		Player player = new Player();
		Actor bg = new Actor("background.png");
		bg.move(400, 300);

		// Title Screens.
		Actor win = new Actor("youwin.png");
		Actor lose = new Actor("gameover.png");
		Actor title = new Actor("title.png");
		title.move(400.0f, 300.0f);
		win.move(400.0f, 300.0f);
		lose.move(400.0f, 300.0f);
		boolean winb = false;
		boolean loseb = false;

		// HealthBars
		Actor playerHp = new Actor("playerHp.png");
		playerHp.move(150, 550);
		Actor blackBar1 = new Actor("playerHp2.png");
		blackBar1.move(324, 550);

		Actor blackBar2 = new Actor("playerHp2.png");
		blackBar2.move(824, 550);
		Actor swansonHp = new Actor("swansonHp.png");
		swansonHp.move(650, 550);

		// SERVER AND CLIENT

		// PLAYER CONTROL

		// TEMPORARY

		// OTHER VARIABLES
		Event event;

		// Sounds
		SoundBuffer buffer = new SoundBuffer();
		buffer.loadFromFile(Paths.get("test.ogg"));

		Sound sound = new Sound();
		sound.setBuffer(buffer);
		// sound.play();

		// SETUP TARGETS/OTHER LOGIC
		swanson.setTarget(player.getSprite());

		// Start the game loop
		while (window.isOpen()) {

			// PROCESS EVENTS
			event = window.pollEvent();
			// Error in checking the provided "while" example -- below works
			if ((event != null) && (event.type == Event.Type.CLOSED))
				window.close();

			// INPUT/OUTPUT and related
			scanIO(player, window);

			// TESTING
			player.testTime();

			if (!start && !winB && !loseB) {
				// GAME LOGIC POST-CONTROL
				if (player != null) {
					player.logic();
					player.incCounter();
				}

				if (swanson != null) {
					swanson.aI();
					swanson.incCounter();
				}

				// collisoins
				collisionChecks(player, swanson);

				// HEALTH CHECKS
				swanson.updateAlive();
				player.updateAlive();
				if (!swanson.isAlive()) {
					// DEAD SWANSON
					System.out.println("SWANSON DEAD");
					winB = true;
				}
				if (!player.isAlive()) {
					// DEAD PLAYERS
					System.out.println("YOU DIED");
					loseB = true;
				}

				// HEALTH BARS
				float displace1 = getDisplace(player.getHp());
				blackBar1.getSprite().setPosition(324, 550);
				blackBar1.move(-displace1, 0);

				float displace2 = getDisplace(swanson.getHp());
				blackBar2.getSprite().setPosition(824, 550);
				blackBar2.move(-displace2, 0);
				
				//additional logic
				soundLogic(swanson);

			}
			// WINDOW
			// CLEAR WINDOW
			window.clear();
			window.setView(viewControlTarget);

			window.draw(bg.getSprite());
			window.draw(playerHp.getSprite());
			window.draw(blackBar1.getSprite());
			window.draw(swansonHp.getSprite());
			window.draw(blackBar2.getSprite());
			
			// Draw Swanson
			if (swanson != null && swanson.hasSpr()) {
				window.draw(swanson.getSprite());
			}
			//Draw trollface
			if(swanson != null && swanson.showTroll){
				window.draw(swanson.getTrollSprite());
			}
			
			// Draw Player
			if (player != null && player.hasSpr()) {
				window.draw(player.getSprite());
			}

			// Draw PlayerProjectiles
			player.drawProjectiles(window);
			swanson.drawProjectiles(window);
			swanson.drawDisplay(window);

			if (start) {
				window.draw(title.getSprite());
			}
			if (winB) {
				window.draw(win.getSprite());
			}
			if (loseB) {
				window.draw(lose.getSprite());
			}

			// UPDATE WINDOW
			window.display();

		}

		// END GAMELOOP
	}

	private float getDisplace(int hp) {
		int maxHp = 100;
		float MaxMinusCurrent = (float) maxHp - hp;
		MaxMinusCurrent /= 100;
		MaxMinusCurrent *= 174;
		return MaxMinusCurrent;
	}

	public void scanIO(Player playerZ, RenderWindow window) {

		Sprite player = playerZ.getSprite();

		if (Keyboard.isKeyPressed(Keyboard.Key.A)) {
			player.move(-10, 0);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.W)) {

			player.move(0, -10);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.D)) {

			player.move(10, 0);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.S)) {
			player.move(0, 10);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.NUM1) || Keyboard.isKeyPressed(Keyboard.Key.NUMPAD1)) {
			playerZ.smartShoot(1, window);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.NUM2) || Keyboard.isKeyPressed(Keyboard.Key.NUMPAD2)) {
			playerZ.smartShoot(2, window);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.NUM3) || Keyboard.isKeyPressed(Keyboard.Key.NUMPAD3)) {
			playerZ.smartShoot(3, window);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.NUM4) || Keyboard.isKeyPressed(Keyboard.Key.NUMPAD4)) {
			playerZ.smartShoot(4, window);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.NUM5) || Keyboard.isKeyPressed(Keyboard.Key.NUMPAD5)) {
			playerZ.smartShoot(5, window);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.NUM6) || Keyboard.isKeyPressed(Keyboard.Key.NUMPAD6)) {
			playerZ.smartShoot(6, window);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.NUM7) || Keyboard.isKeyPressed(Keyboard.Key.NUMPAD7)) {
			playerZ.smartShoot(7, window);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.NUM8) || Keyboard.isKeyPressed(Keyboard.Key.NUMPAD8)) {
			playerZ.smartShoot(8, window);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.NUM9) || Keyboard.isKeyPressed(Keyboard.Key.NUMPAD9)) {
			playerZ.smartShoot(9, window);
		}
		if (Keyboard.isKeyPressed(Keyboard.Key.NUM0) || Keyboard.isKeyPressed(Keyboard.Key.TILDE)
				|| Keyboard.isKeyPressed(Keyboard.Key.NUMPAD0)) {
			playerZ.smartShoot(0, window);
		}

		if (Mouse.isButtonPressed(Mouse.Button.LEFT)) {

			if (start) {
				discrete.play();
			}
			start = false;

		}
		if (Mouse.isButtonPressed(Mouse.Button.LEFT)) {
			Vector2i mousePosWin;
			Vector2f mousePos;
			mousePosWin = Mouse.getPosition(window);
			mousePos = window.mapPixelToCoords(mousePosWin);

			// Where to start the projectile
			float[] startPos = new float[2];
			startPos[0] = player.getPosition().x;
			startPos[1] = player.getPosition().y;

			// where to send the projectil
			int[] target = new int[2];
			target[0] = (int) mousePos.x;
			target[1] = (int) mousePos.y;

			playerZ.shoot(target, startPos);
		}

	}

	public static void collisionChecks(Player player, Swanson swanson) {
		// check player projectiles hit swanson
		FloatRect comp1;
		FloatRect comp2;

		ArrayList<Projectile> pp = player.getProjectiles();
		for (int i = 0; i < pp.size(); i++) {
			Projectile proj = pp.get(i);
			comp1 = proj.getSprite().getGlobalBounds();
			comp2 = swanson.getSprite().getGlobalBounds();

			if (checkCollision(comp1, comp2) && proj.isAlive()) {
				System.out.println("HIT HIM");
				proj.setAlive(false);
				int num = player.findProjectile(proj);
				swanson.applyDamageMaybe(num);
			}
		}

		// check swansons projects hit player

		ArrayList<Projectile> sp = swanson.getProjectiles();
		for (int i = 0; i < sp.size(); i++) {
			Projectile proj = sp.get(i);
			comp1 = proj.getSprite().getGlobalBounds();
			comp2 = player.getSprite().getGlobalBounds();

			if (checkCollision(comp1, comp2) && proj.isAlive()) {
				System.out.println("HIT ME");
				proj.setAlive(false);
				player.changeHP(-20);
			}
		}

		// check if swanson touches player

	}

	public static boolean checkCollision(FloatRect x, FloatRect y) {
		boolean collision = false;
		if (x != null && y != null) {
			FloatRect z = x.intersection(y);
			if (z != null) {
				collision = true;
			}
		}
		return collision;
	}

	public void initSounds() {
		// SoundBuffer buffer = new SoundBuffer();
		try {
			finishHimBuffer = new SoundBuffer();
			discreteBuffer = new SoundBuffer();

			finishHimBuffer.loadFromFile(Paths.get("finishHim.ogg"));
			discreteBuffer.loadFromFile(Paths.get("discreteMadness.ogg"));

			// Sound sound = new Sound();
			discrete = new Sound();
			discrete.setBuffer(discreteBuffer);

			finishHim = new Sound();
			finishHim.setBuffer(finishHimBuffer);
		} catch (Exception all) {
			System.out.println("error in sound init");
		}
		// sound.play();
	}
	
	
	public void soundLogic(Actor act){
		if(playFinishHim && act.getHp() < 10){
			
			finishHim.play();
			playFinishHim = false;
		}
	}

}

/*
 * }
 *
 * }
 */

// Reference
// Create a graphical text to display
/*
 * Font font = new Font(); font.loadFromFile(Paths.get("atom.png"));
 */
// Text text = new Text("Hello JSFML", font, 50);

// Load a music to play
/*
 * Music music = new Music(); music.openFromFile(Paths.get("nice_music.ogg"));
 */

// Play the music
/*
 * music.play();
 */

/*
 * while (event != null) { //Close window : exit if (event.type ==
 * Event.Type.CLOSED) window.close(); }
 */
// if (Keyboard.isKeyPressed(Keyboard.Key.Left))

// Draw the string
// window.draw(text);
// Update the window

/*
 * BEFORE SMART IMPORT APPLIED -- USE AS REFERENCE import java.util.Scanner;
 * import org.jsfml.audio.*; import org.jsfml.graphics.*; import
 * org.jsfml.window.*; import java.io.*; import java.nio.file.Paths; import
 * org.jsfml.window.VideoMode; import org.jsfml.window.event.Event; import
 * org.jsfml.audio.Music;
 */// import org.jsfml.graphics.*;
	// import org.jsfml.window.VideoMode;
	// import org.jsfml.window.event.Event;
	// import org.jsfml.window.Keyboard;
	// import org.jfsml.Keyboard.*;
