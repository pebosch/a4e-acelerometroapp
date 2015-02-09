package com.a4e.acelerometroapp;

import android.content.Context;

public class Accelerometer implements AccelerometerInterface{
	 AccelerometerHandler accelHandler;
	 Float accelX=0f, accelY=0f, accelZ=0f, prevX=0f, prevY=0f, prevZ=0f;
	 float minMovSide = 1E-9f; // Min value to considerate like a movement
     float mov=0, movX=0, movY=0, movZ=0;

	    public Accelerometer(Context context){
	        accelHandler = new AccelerometerHandler(context);
	    }

	    @Override
	    public float getAccelX() {
	    	accelX = accelHandler.getAccelX();
	    	return accelX;
	    }

	    @Override
	    public float getAccelY() {
	    	accelY =  accelHandler.getAccelY();
	    	return accelY;
	    }

	    @Override
	    public float getAccelZ() {
	    	accelZ = accelHandler.getAccelZ();
	    	return accelZ;
	    }

	    @Override
	    public long getAtTime(){
	    	return accelHandler.getAtTime();
	    }
	    
	    @Override
	    public float getPower(){
	    	return accelHandler.getPower();
	    }
	    
	    @Override
	    public void actPrevAxisValues(){
	    	prevX = accelX;
            prevY = accelY;
            prevZ = accelZ;
	    }
	    
	    @Override
	    public void actAxisMov(long timeDiff){
	    	 mov = Math.abs((accelX + accelY + accelZ) - (prevX - prevY - prevZ)) / timeDiff;
	    	 movX = (accelX - prevX) / timeDiff;
             movY = (accelY - prevY) / timeDiff;
             movZ = (accelZ - prevZ) / timeDiff;
	    }
	    
	    @Override
	    public boolean isPositiveMovX(){
	    	boolean movDone = false;
	    	
	    	if(movX > minMovSide)
	    		movDone = true;
	    	
	    	return movDone;
	    }
	    
	    @Override
	    public boolean isNegativeMovX(){
	    	boolean movDone = false;
	    	
	    	
	    	if(movX < -minMovSide)
	    		movDone = true;
	    	
	    	return movDone;
	    }
	    
	    @Override
	    public boolean isPositiveMovY(){
	    	boolean movDone = false;
	    	
	    	if(movY > minMovSide)
	    		movDone = true;
	    	
	    	return movDone;			
	    }
	    
	    @Override
	    public boolean isNegativeMovY(){
	    	boolean movDone = false;
	    	
	    
        	if(movY < -minMovSide)
        		movDone = true;

	    	return movDone;
	    }
	    
	    @Override
	    public boolean isPositiveMovZ(){
	    	boolean movDone = false;
	    	
	    	
        	if(movZ > minMovSide)
        		movDone = true;
        	
	    	return movDone;
	    }
	    
	    @Override
	    public boolean isNegativeMovZ(){
	    	boolean movDone = false;
	    	
        	if(movZ < -minMovSide)
        		movDone = true;
        	
	    	return movDone;
	    }
	    
	    @Override
	    public float getTotalMov(){
	    	return mov;
	    }
	    
	    @Override
	    public float getMovXValue(){
	    	return movX;
	    }
	    
	    @Override
	    public float getMovYValue(){
	    	return movY;
	    }
	    
	    @Override
	    public float getMovZValue(){
	    	return movZ;
	    }
}