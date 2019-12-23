package spaceshipgame;

import java.util.ArrayList;

import com.sun.istack.internal.Nullable;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import spaceshipgame.physics.TestOverlap;
import spaceshipgame.util.MathHelpers;

class Spaceship implements UID {
	long uid;

	// position, velocity and acceleration
	PVector pos = new PVector(0, 0);
	PVector prevPos = pos.copy();
	PVector vel = new PVector(0, 0);
	PVector acc = new PVector(0, 0);

	// angular position, velocity, and acceleration
	float ang = 0;
	float avel = 0;
	float aacc = 0;
	float prevAng = ang;

	float mass; // how many hamburgers you ate

	// player controls
	ControllerConfig controls;

	boolean thrusting = false; // whether thrusters are engaged
	boolean shooting = false;

	float maxSpeed = Constants.MAX_TURN_SPEED; // so you don't spin to fast TODO: this doesn't do anything

	// characteristics
	String name; // spaceship name

	float maxLife = Constants.MAX_LIFE; // how many pieces you can be sliced into
	float life = maxLife;

	int color = 0x00FF0000; // spaceship color

	// weapons
	int bulletSize = 2; // in pixels
	int bulletSpeed = 480; // speed of bullets

	// engaged?
	boolean laser = false;
	boolean ghost = false;
	boolean homing = false;
	boolean shield = true;

	// upgrade slots
	Upgrade biggerUpgradeSlot;
	Upgrade otherUpgradeSlot;
	Upgrade fasterUpgradeSlot;

	ArrayList<Upgrade> bulletModifierUpgrades = new ArrayList<>();

	int bulletChanges = 0;

	int shootingTimer = 30000; // in FPS
	float reloadTime = Constants.DEFAULT_RELOAD_TIME; // in seconds

	// Visual
	float shieldTurn = 0;

	public Spaceship(long uid, Main ctx, int x, int y, ControllerConfig controls, int m, int c, String n) {
		this.uid = uid;

		pos = new PVector(x, y);
		prevPos = pos.copy();

		this.controls = controls;
		mass = m;
		color = c;
		name = n;
	}

	public Spaceship(long uid, Main g, int x, int y, boolean isPlayer, @Nullable ControllerConfig keysForPlayer, int m, int c) {
		this(uid, g, x, y, keysForPlayer, m, c, "bob");
	}

	public Spaceship(long uid, Main g, int x, int y, boolean isPlayer, @Nullable ControllerConfig keysForPlayer, int m) {
		this(uid, g, x, y, keysForPlayer, m, -1, "bob");
	}
	
	void drawSpaceship(PGraphics ctx, float x, float y, float angle, boolean thrusters) {
		// s = 2
		ctx.pushMatrix();
		ctx.translate(x, y);
		ctx.rotate(angle);

		Point[] sp = Constants.spaceshipPoints;

		ctx.triangle(sp[0].x, sp[0].y, sp[1].x, sp[1].y, sp[2].x, sp[2].y);
		ctx.rect(-8, 20, 16, 10);

		if(thrusters) {
			ctx.fill(255, 100, 0);
			ctx.noStroke();
			ctx.triangle(-8, 30, 1, 50, 8, 30);
			ctx.fill(255, 255, 0);
			ctx.triangle(-4, 30, 1, 46, 4, 30);
		}

		ctx.popMatrix();
	}

	public void equipUpgrade(Upgrade u) {
		switch(u.cad) {
			case BIGGER:
				if(biggerUpgradeSlot != null) {
					biggerUpgradeSlot.unequip(this);
				}

				if(otherUpgradeSlot != null) {
					otherUpgradeSlot.unequip(this);
				}

				u.equip(this);
				otherUpgradeSlot = u;
				break;
			case FASTER:
				// unequip other upgrades
				if(fasterUpgradeSlot != null) {
					fasterUpgradeSlot.unequip(this);
				}
				if(otherUpgradeSlot != null) {
					otherUpgradeSlot.unequip(this);
				}

				u.equip(this);
				fasterUpgradeSlot = u;
				break;
			case BULLET_CHANGE:
				// unequip other slot
				if(otherUpgradeSlot != null) {
					otherUpgradeSlot.unequip(this);
				}

				u.equip(this);
				bulletModifierUpgrades.add(u);
				break;
			case OTHER:
				if(otherUpgradeSlot != null) {
					otherUpgradeSlot.unequip(this);
				}

				otherUpgradeSlot = u;
				u.equip(this);
				break;
			case AUTO_DIE:
				u.equip(this);
				break;
		}
	}

	void draw(PGraphics ctx) {
		if(shield) { 
			ctx.noStroke();
			ctx.fill(255);
			ctx.pushMatrix();
			ctx.translate(pos.x, pos.y);
			ctx.noFill(); 
			ctx.rotate(shieldTurn / Constants.FPS); 
			
			for(int i = 0; i < 360 / 10; i++) {
				ctx.rotate((float) (Math.PI / 8));
				ctx.fill(255);
				ctx.triangle(-15, -Constants.SHIELD_DIAMETER / 2, 0, -Constants.SHIELD_DIAMETER / 2 - 30, 15, -Constants.SHIELD_DIAMETER / 2);
			}
			
			ctx.noStroke();
			ctx.fill(150, 0, 125);
			ctx.ellipse(0, 0, Constants.SHIELD_DIAMETER, Constants.SHIELD_DIAMETER);
			ctx.strokeWeight(10);
			ctx.stroke(255);
			ctx.noFill();
			ctx.ellipse(0, 0, Constants.SHIELD_DIAMETER + 5, Constants.SHIELD_DIAMETER + 5);
			ctx.popMatrix();
			shieldTurn++;
		}

		ctx.fill(greyifyColor(ctx.red(color)), greyifyColor(ctx.green(color)), greyifyColor(ctx.blue(color)));
		ctx.stroke(greyifyColor(ctx.red(color)), greyifyColor(ctx.green(color)), greyifyColor(ctx.blue(color)));
		
		drawSpaceship(ctx, ((prevPos.x - pos.x) / 2) + pos.x, ((prevPos.y - pos.y) / 2) + pos.y, prevAng, false);

		if(ghost) {
			ctx.fill(ctx.red(color), ctx.green(color), ctx.blue(color), 200);
			ctx.stroke(ctx.red(color), ctx.green(color), ctx.blue(color), 200);
		} else {
			ctx.fill(color);
			ctx.stroke(color);
		}

		drawSpaceship(ctx, pos.x, pos.y, ang, thrusting);
		drawLifeAndName(ctx);
	}

	int greyifyColor(float color) {
		if(color > 128) {
			return (int) color - 100;
		} else {
			return (int) color + 100;
		}
	}

	void drawLifeAndName(PGraphics ctx) { // draws the life bar
		ctx.noStroke();
		ctx.fill(255);
		ctx.rect(pos.x - 16, pos.y - 43, 32, 12);
		ctx.colorMode(PConstants.HSB);
		ctx.fill(MathHelpers.map(life, 0, maxLife, 0, 90), 255, 255);
		ctx.rect(pos.x - 16, pos.y - 43, MathHelpers.map(life - 1, 0, maxLife, 0, 32), 12);
		ctx.colorMode(PConstants.RGB);
		ctx.fill(0);
		ctx.textSize(8);
		ctx.text(name, pos.x - 15, pos.y - 34);
	}

	void update(Game game) { // makes you move
		prevPos = pos.copy();
		prevAng = ang;
		
		if(shooting) {
			shoot(game);
		}
		
		vel.add(acc);
		vel.limit(Constants.MAX_SPEED / Constants.FPS);
		pos.add(vel);

		if(pos.mag() + Constants.SHIP_RADIUS > Constants.PLAY_SIZE) {
			// g.freeze();
			float ang = pos.heading();

			float multiplier = Constants.PLAY_SIZE - Constants.SHIP_RADIUS;
			pos.x = (float) Math.cos(ang) * multiplier;
			pos.y = (float) Math.sin(ang) * multiplier;
			vel.x *= -0.5;
			vel.y *= -0.5;
		}

		acc.mult(0);
		avel += aacc;
		//avel = Math.max(-Constants.MAX_TURN_SPEED, Math.min(Constants.MAX_TURN_SPEED, avel));
		
		if(Math.abs(avel) > Constants.MAX_TURN_SPEED) {
			avel += -avel * 0.4;
		}
		
		ang += avel;
		aacc = 0;

		shootingTimer++;
		// shieldTurn++;
	}

	void shoot(Game game) {
		float HALF_PI = PConstants.HALF_PI;
		
		System.out.println(shootingTimer);
		if(((float) shootingTimer / Constants.FPS) >= reloadTime && !laser) {
			game.bullets.add(new Bullet(game.getNewUID(), uid,            // VV start the bullet inside of the ship
					(float) (pos.x + Math.cos(ang - HALF_PI) * (Constants.SHIP_RADIUS - (bulletSize * 4))),
					(float) (pos.y + Math.sin(ang - HALF_PI) * (Constants.SHIP_RADIUS - (bulletSize * 4))),
					bulletSize, this.homing));
			
			game.bullets.get(game.bullets.size() - 1).aim(ang, bulletSpeed, this.vel);
			shootingTimer = 0;
		} /*else if(laser) { //draw the laser
			ctx.stroke(255, 0, 0); //TODO: DO NOT DRAW THE LASER HERE!!!
			ctx.strokeWeight(2);
			ctx.noFill();
			ctx.line(pos.x + vel.x, pos.y + vel.y, 
					(float) Math.cos(ang - HALF_PI + avel) * Constants.PLAY_SIZE * 2 + pos.x + vel.x,
					(float) Math.sin(ang - HALF_PI + avel) * Constants.PLAY_SIZE * 2 + pos.y + vel.y);
			
			for(Spaceship s : game.ships) {
				// TODO: redo rotation, calculate sin and cos once
				if(s.uid != this.uid && !s.ghost
						&& TestOverlap.linePolygonRotateCollide(Constants.getNewSpaceshipPoints(),
								s.pos.x, s.pos.y, pos.x + vel.x, pos.y + vel.y,
								(float) Math.cos(ang - HALF_PI + avel) * Constants.PLAY_SIZE * 2 + pos.x + vel.x,
								(float) Math.sin(ang - HALF_PI + avel) * Constants.PLAY_SIZE * 2 + pos.y + vel.y, s.ang)) {
					
					s.life -= Constants.LASER_DAMAGE_PER_SECOND / Constants.FPS;
				}
			}
			
			ctx.noStroke();
		}*/
	}

	void updateKeys(boolean[] keys) { // tells you to move
		if(controls != null) {
			if(keys[this.controls.turnLeftKey]) {
				aacc -= Constants.TURN_ACCELERATION / Constants.FPS;
			}
			
			if(keys[this.controls.turnRightKey]) {
				aacc += Constants.TURN_ACCELERATION / Constants.FPS;
			}
			
			if(keys[this.controls.accelerateKey]) {
				applyForce(new PVector((float) Math.cos(ang - PConstants.HALF_PI) * Constants.MOVE_ACCELERATION / Constants.FPS, 
									   (float) Math.sin(ang - PConstants.HALF_PI) * Constants.MOVE_ACCELERATION / Constants.FPS));
				thrusting = true;
			} else {
				thrusting = false;
			}
			
			shooting = keys[this.controls.shootKey];
		}
	}
	
	void applyForce(PVector v) {
		acc.add(PVector.div(v, (mass * Constants.FPS)));
	}

	public long getUID() {
		return uid;
	}
}