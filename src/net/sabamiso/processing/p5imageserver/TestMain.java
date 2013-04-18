package net.sabamiso.processing.p5imageserver;

import processing.core.PApplet;
import processing.core.PVector;

@SuppressWarnings("serial")
public class TestMain extends PApplet {

	class SimpleAnalogClock {
		public PVector center = new PVector();
		public int radius = 0;

		void draw_clock_line(float s, float e, float angle) {
			float vx = sin(angle * 2 * PI);
			float vy = -cos(angle * 2 * PI);
			float sp_x = center.x + s * radius * vx;
			float sp_y = center.y + s * radius * vy;
			float ep_x = center.x + e * radius * vx;
			float ep_y = center.y + e * radius * vy;

			line(sp_x, sp_y, ep_x, ep_y);
		}

		void draw() {
			stroke(255);
			noFill();

			strokeWeight(10);
			ellipse(center.x, center.y, radius * 2, radius * 2);

			strokeWeight(5);
			for (int i = 0; i < 12; ++i) {
				draw_clock_line(0.9f, 1.0f, i / 12.0f);
			}

			int h = hour();
			int m = minute();
			int s = second();

			float angle_h = h / 12.0f + m / 12.0f / 60.0f;
			strokeWeight(15);
			draw_clock_line(0.0f, 0.5f, angle_h);

			float angle_m = m / 60.0f + s / 60.0f / 60.0f;
			strokeWeight(10);
			draw_clock_line(0.0f, 0.7f, angle_m);

			float angle_s = s / 60.0f;
			strokeWeight(5);
			draw_clock_line(0.0f, 0.80f, angle_s);
		}
	}

	SimpleAnalogClock clock;
	P5ImageServer image_server;
	
	public void setup() {
		size(640, 480);
		frameRate(5);

		clock = new SimpleAnalogClock();
		clock.center.x = width / 2;
		clock.center.y = height / 2;
		clock.radius = 160;
		
		image_server = new P5ImageServer("http://a.example.com/image_post", "camera0");
	}

	public void draw() {
		background(0, 0, 64);
		smooth();
		clock.draw();
		image_server.send(g);
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "net.sabamiso.processing.p5imageserver.TestMain" });
	}
}
