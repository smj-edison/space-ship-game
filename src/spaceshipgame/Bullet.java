package spaceshipgame;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import spaceshipgame.physics.TestOverlap;

class Bullet implements UID {
	long uid;

	PVector pos = new PVector(0, 0);
	PVector vel = new PVector(0, 0);

	int damage;
	float sourceId;
	boolean homing = false;
	float maxSpeed;

	Spaceship target;

	public Bullet(long uid, long sourceId, float x, float y, int damage, boolean homing) {
		this.uid = sourceId;
		this.sourceId = sourceId;
		this.pos = new PVector(x, y);
		this.damage = damage;
		this.homing = homing;
	}

	public void draw(PGraphics ctx) {
		float bsm = Constants.BULLET_SIZE_MULTIPLIER;
		
		if(homing) {
			ctx.pushMatrix();
			ctx.translate(pos.x, pos.y);
			// point in the direction heading
			ctx.rotate((float) (PApplet.atan2(vel.y, vel.x) + Math.PI / 2));
			ctx.triangle(-damage * 4, damage * bsm, 0, -damage * bsm, damage * bsm, damage * bsm);
			ctx.popMatrix();

		} else {
			ctx.ellipse(pos.x, pos.y, damage * bsm, damage * bsm);
		}
	}

	public void update(Game game) {
		if(homing) {
			float minDist = Float.MAX_VALUE;
			Spaceship nearest = null;

			// search for the nearest ship
			for(Spaceship ship : game.ships) {
				if(ship.uid != this.sourceId && !ship.ghost /* && !ship.shield */
						&& PApplet.dist(this.pos.x, this.pos.y, ship.pos.x, ship.pos.y) < minDist) {
					nearest = ship;
				}
			}

			if(nearest != null) {
				float ang = PApplet.atan2(nearest.pos.y - this.pos.y, nearest.pos.x - this.pos.x);
				
				vel.x = (float) (Math.cos(ang) * Constants.HOMING_SPEED);
				vel.y = (float) (Math.sin(ang) * Constants.HOMING_SPEED);
				
				if(maxSpeed != 0) {
					vel.limit(maxSpeed);
				}
				
			}
		}

		pos.add(vel);
	}

	public void aim(float ang, float speed, PVector shipVelocity) {
		vel.set((float) Math.cos(ang - PConstants.HALF_PI) * speed / Constants.FPS,
				(float) Math.sin(ang - PConstants.HALF_PI) * speed / Constants.FPS);
		
		vel.add(shipVelocity);
		
		maxSpeed = speed;
	}

	public void aim(float ang, float speed) {
		aim(ang, speed, new PVector(0, 0));
	}

	public boolean isColliding(Spaceship s) {
		if(!s.ghost && s.uid != sourceId) {

			if(!s.shield) {
				return TestOverlap.polygonCircleRotateCollide(Constants.getNewSpaceshipPoints(), s.pos.x, s.pos.y,
							pos.x, pos.y, damage * Constants.BULLET_SIZE_MULTIPLIER, s.ang) ||
				   TestOverlap.linePolygonRotateCollide(Constants.getNewSpaceshipPoints(), s.pos.x, s.pos.y, //TODO: work on collisions
							pos.x, pos.y, pos.x + vel.x, pos.y + vel.y, s.ang);
			} else {
				return TestOverlap.circleCircleCollide(pos.x, pos.y, damage * 4,
													   s.pos.x, s.pos.y, Constants.SHIELD_DIAMETER);
			}
		}
		
		return false;
	}

	public long getUID() {
		return uid;
	}
}