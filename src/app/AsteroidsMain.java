package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import effects.Effect;
import extras.Action;
import extras.AudioPlayer;
import gui.CircleButton;
import processing.core.PApplet;
import processing.core.PVector;

public class AsteroidsMain extends PApplet {
	static Player p;

	ArrayList<Bullet> bullets;
	ArrayList<Asteroid> asteroids;
	ArrayList<Effect> effects = new ArrayList<Effect>(); //TODO: explosion effects, shooting effects

	AudioPlayer shoot, explosion;

	CircleButton shootButton;

	long elapsedTime, startTime;
	boolean canShoot = true;

	public void settings() {
		size(displayWidth, displayHeight);

		orientation(LANDSCAPE); 
		//TODO: decrease resolution
	}

	public void setup() {
		try {
			shoot = new AudioPlayer("bin/shoot.wav");
			explosion = new AudioPlayer("bin/explosion.wav");
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Action action = (AsteroidsMain a) -> a.shoot();
		shootButton = new CircleButton(width * 0.2f, height * 0.8f, 100, action, this);

		bullets = new ArrayList<Bullet>();
		asteroids = new ArrayList<Asteroid>();

		for (int i = 0; i < 6; i++)
			asteroids.add(new Asteroid(random(width), random(height), this));

		p = new Player(width / 2, height / 2);

		frameRate(60);

		startTime = System.currentTimeMillis();
		elapsedTime = 0L;
	}

	public void draw() {
		elapsedTime = (new Date()).getTime() - startTime;

		background(0);

		stroke(255);
		noFill();

		p.update(this);
		p.show(this);

		for (int i = bullets.size() - 1; i >= 0; i--) {
			if (bullets.get(i).update(this)) {
				bullets.remove(i);
				continue;
			}

			int j = bullets.get(i).collide(asteroids, this);

			if (j != -1) {
				explosion.play();

				effects.add(new Effect(asteroids.get(j), this));

				asteroids.get(j).boom(bullets.get(i), this).forEach((a) -> {
					asteroids.add(a);
				});

				bullets.remove(i);

				asteroids.remove(j);

				continue;
			}

			bullets.get(i).show(this);
		}

		stroke(255);
		noFill();

		for (Asteroid a : asteroids) {
			a.update(this);
			a.show(this);
		}

		for (int i = effects.size() - 1; i >= 0; i--) {
			effects.get(i).update(this);

			if (effects.get(i).delete())
				effects.remove(i);
			else
				effects.get(i).show(this);
		}

		GUI();
	}

	public void keyPressed() {
		frameCount = -1;
	}

	PVector pMouse;

	public void mousePressed() {
		if (pMouse == null)
			pMouse = new PVector(mouseX, mouseY);
	}

	public void mouseReleased() {
		pMouse = null;
		canShoot = true;
	}

	PVector dragDir = new PVector(0, 0);

	public void mouseDragged() {
		dragDir = PVector.sub(new PVector(mouseX, mouseY), pMouse);
	}

	void GUI() {
		noStroke();
		fill(255, 100);
		shootButton.show(this);

		if (mousePressed) {
			if (!shootButton.update(mouseX, mouseY)) {
				float padD = (float) (height * 0.1); //Pad diameter
				noStroke();
				fill(255, 30);
				ellipse(pMouse.x, pMouse.y, padD, padD);

				float d = min(pMouse.dist(new PVector(mouseX, mouseY)), padD / 2);
				PVector dir = PVector.add(PVector.mult(dragDir.normalize(), d), pMouse);

				stroke(0);
				fill(255, 50);
				ellipse(dir.x, dir.y, padD / 2, padD / 2);

				p.addForce(PVector.mult(dragDir, 0.1f));
			}
		}
	}

	public void shoot() {
		if (elapsedTime > 300 && bullets.size() < 15 && canShoot) {
			bullets.add(p.shoot());
			shoot.play();
			canShoot = false;
			startTime = System.currentTimeMillis();
		}
	}

	public static void main(String[] args) {
		String[] processingArgs = { "Asteroids" };
		AsteroidsMain mySketch = new AsteroidsMain();
		PApplet.runSketch(processingArgs, mySketch);
	}
}
