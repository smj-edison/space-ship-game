package spaceshipgame.physics;

import spaceshipgame.Point;

public class TestOverlap {
	static float base = 1 / 1024 / 1024 / 1024;

	static float[] Rotate(int x, int y, int theta) {
		return new float[] { Math.round(x * Math.cos(theta) - y * Math.sin(theta)), Math.round(y * Math.cos(theta) + x * Math.sin(theta)) };
	}

	static int[][] coords2Points(int... arguments) {
		int i, j;
		int[][] points = new int[arguments.length / 2][2];
		
		for(i = j = 0; i < arguments.length; j++) {
			points[j] = new int[] { arguments[i++], arguments[i++] };
		}
		return points;
	}

	static float ROUND(float n) {
		return Math.round(n / base) * base;
	}

	static boolean isBetween(float c, float a, float b) {
		return (a - c) * (b - c) <= 0;
	}

	static boolean overlap(float a, float b, float c, float d) {
		return isBetween(c < d ? c : d, a, b) || isBetween(a < b ? a : b, c, d);
	}

	static boolean lineLineCollide(float ax1, float ay1, float ax2, float ay2, float bx1, float by1, float bx2, float by2) {
		float denom = (ax1 - ax2) * (by1 - by2) - (ay1 - ay2) * (bx1 - bx2);
		if(denom == 0) {
			if(ax1 == ax2) {
				return ax1 == bx1 && overlap(ay1, ay2, by1, by2);
			} else {
				float m = (ay1 - ay2) / (ax1 - ax2);
				float ka = ay1 - m * ax1;
				float kb = by1 - m * bx1;
				return ka == kb && overlap(ax1, ax2, bx1, bx2);
			}
		}
		float na = ax1 * ay2 - ay1 * ax2;
		float nb = bx1 * by2 - by1 * bx2;
		float x = ROUND((na * (bx1 - bx2) - (ax1 - ax2) * nb) / denom);
		float y = ROUND((na * (by1 - by2) - (ay1 - ay2) * nb) / denom);
		return isBetween(x, ax1, ax2) && isBetween(x, bx1, bx2) && isBetween(y, ay1, ay2) && isBetween(y, by1, by2);
	}

	static boolean isInPolygon(float x, float y, Point[] poly) {
		boolean isIn = false;
		for(int i = 0, j = poly.length - 1; i < poly.length; j = i++) {
			float xi = poly[i].x, yi = poly[i].y;
			float xj = poly[j].x, yj = poly[j].y;
			boolean intersect = yi > y != yj > y && x < (xj - xi) * (y - yi) / (yj - yi) + xi;
			if(intersect) {
				isIn = !isIn;
			}
		}
		return isIn;
	}

	static boolean isInCircle(float x, float y, float cx, float cy, float diam) {
		float dx = x - cx;
		float dy = y - cy;
		return dx * dx + dy * dy <= diam * diam / 4;
	}

	static boolean circleCircleCollide(float x1, float y1, float diam1, float x2, float y2, float diam2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dist2 = dx * dx + dy * dy;
		float sum = (diam1 + diam2) / 2;
		return dist2 <= sum * sum;
	}

	static boolean lineCircleCollide(float x1, float y1, float x2, float y2, float cx, float cy, float diam) {
		float m = (y2 - y1) / (x2 - x1);
		
		if(Math.abs(m) > 1024) {
			return lineCircleCollide(y1, x1, y2, x2, cy, cx, diam);
		}
		
		if(isInCircle(x2, y2, cx, cy, diam)) {
			return true;
		}
		x1 -= cx;
		x2 -= cx;
		y1 -= cy;
		y2 -= cy;
		float r = diam * diam / 4;
		float k = y1 - m * x1;
		float a = (1 + m * m) / r;
		float b = 2 * m * k / r;
		float c = k * k / r - 1;
		float discrim = b * b - 4 * a * c;
		if(discrim < 0) {
			return false;
		}
		
		discrim = (float) Math.sqrt(discrim);
		a *= 2;
		
		return isBetween((-b - discrim) / a, x1, x2) || isBetween((-b + discrim) / a, x1, x2);
	}
	
	public static boolean linePolygonCollide(float x1, float y1, float x2, float y2, Point[] poly) {
	    boolean collide = isInPolygon(x1, y1, poly);
	    
	    for (int j = poly.length - 1, i = 0; !collide && i < poly.length; j = i, i++) {
	        collide = lineLineCollide(x1, y1, x2, y2, poly[j].x, poly[j].y, poly[i].x, poly[i].y);
	    }
	    
	    return collide;
	}

	public static boolean polygonCircleRotateCollide(Point[] poly, float tx, float ty, float cx, float cy, float diam, float theta) {
		for(int i = 0; i < poly.length; i++) {
			poly[i].rotate(theta);
			poly[i].x += tx;
			poly[i].y += ty;
		}
		
		boolean collide = isInPolygon(cx, cy, poly);
		for(int i = 0, j = poly.length - 1; !collide && i < poly.length; j = i, i++) {
			collide = lineCircleCollide(poly[j].x, poly[j].y, poly[i].x, poly[i].y, cx, cy, diam);
		}
		return collide;
	}

	public static boolean linePolygonRotateCollide(Point[] poly, float tx, float ty, float x1, float y1, float x2, float y2, float theta) {
		for(int i = 0; i < poly.length; i++) {
			poly[i].rotate(theta);
			poly[i].x += tx;
			poly[i].y += ty;
		}
		
		boolean collide = isInPolygon(x1, y1, poly);
		for(int j = poly.length - 1, i = 0; !collide && i < poly.length; j = i, i++) {
			collide = lineLineCollide(x1, y1, x2, y2, poly[j].x, poly[j].y, poly[i].x, poly[i].y);
		}
		
		return collide;
	}
}