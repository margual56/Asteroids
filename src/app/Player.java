package app;

import extras.Movable;
import processing.core.PGraphics;
import processing.core.PVector;

class Player extends Movable{
	  private float scale = 20;
	  private PVector vertices[];
	  
	  private float angle = 0;
	  
	  public Player(float x, float y){
	    this.pos = new PVector(x, y);
	    this.velocity = new PVector(0, 0);
	    
	    float tmp = AsteroidsMain.sqrt(2);
	    vertices = new PVector[]{new PVector(0	 , 1   ).setMag(scale), 
								 new PVector(tmp , -tmp).setMag(scale), 
								 new PVector(-tmp, -tmp).setMag(scale)};
	  }
	  
	  public void update(AsteroidsMain a){
	    move(a);
	  }
	  
	  public void addForce(PVector f){
	    if(PVector.add(velocity, f).mag() < 5){
	      velocity.add(f);
	      
	      angle = AsteroidsMain.atan2(f.y, f.x);
	    }
	  }
	  
	  public Bullet shoot(){
		return new Bullet(PVector.add(this.pos, PVector.fromAngle(angle).mult(scale)), PVector.fromAngle(angle).setMag(6));
	  }
	  
	  public void show(AsteroidsMain a){
	    a.pushMatrix();
	    a.translate(pos.x, pos.y);
	    a.rotate(angle - AsteroidsMain.PI/2);
	    a.beginShape();
	    for(PVector p: vertices)
	    	a.vertex(p.x, p.y);
	    a.endShape(AsteroidsMain.CLOSE);
	    a.popMatrix();
	  }
	  
	  public void show(PGraphics g){
	    g.pushMatrix();
	    g.translate(pos.x, pos.y);
	    g.rotate(angle - AsteroidsMain.PI/2);
	    g.beginShape();
	    for(PVector p: vertices)
	      g.vertex(p.x, p.y);
	    g.endShape(AsteroidsMain.CLOSE);
	    g.popMatrix();
	  }
	}