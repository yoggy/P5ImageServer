import net.sabamiso.processing.p5imageserver.*;

SimpleAnalogClock clock;
P5ImageServer image_server;

void setup() {
  size(640, 480);
  frameRate(5);

  clock = new SimpleAnalogClock();
  clock.center.x = width / 2;
  clock.center.y = height / 2;
  clock.radius = 160;
  
  image_server = new P5ImageServer("http://a.example.com/image_post", "camera0");
}

void draw() {
  background(0, 0, 64);
  smooth();
  clock.draw();
  image_server.send(g);
}

class SimpleAnalogClock {
  public PVector center = new PVector();
  public int radius = 0;

  void draw_clock_line(float s, float e, float angle) {
    float vx = sin(angle * 2 * PI);
    float vy =  -cos(angle * 2 * PI);
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
    ellipse(center.x , center.y, radius * 2, radius * 2); 
  
    strokeWeight(5);
    for (int i = 0; i < 12; ++i) {
      draw_clock_line(0.9, 1.0, i / 12.0);
    }
  
    int h = hour();
    int m = minute();
    int s = second();
    
    float angle_h = h / 12.0 + m / 12.0 /60.0;
    strokeWeight(15);
    draw_clock_line(0.0, 0.5, angle_h);
  
    float angle_m = m / 60.0 + s / 60.0 / 60.0;
    strokeWeight(10);
    draw_clock_line(0.0, 0.7, angle_m);
  
    float angle_s = s / 60.0;
    strokeWeight(5);
    draw_clock_line(0.0, 0.80, angle_s);
  }
}

