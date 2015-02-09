/**
 * Título: Android4Education Acelerometro App
 * Licencia Pública General de GNU (GPL) versión 3 
 * Código Fuente: https://github.com/pebosch/a4e-acelerometroapp
 * Autor: Pedro Fernández Bosch
 * Fecha de la última modificación: 09/01/2015
 */

package com.a4e.acelerometroapp;

import com.a4e.aceletometroapp.R;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.Toast;

/**
 * Declaración de variables
 */
public class MainActivity extends Activity {

	ImageButton imgButton;
	
	Accelerometer accelerometer;
    Float accelX, accelY, accelZ;
    long lastUpdate, lastMov;
    int mostrarEjes = 0;
    
    int sdk = android.os.Build.VERSION.SDK_INT;
    final Handler myHandler = new Handler();
    
    int deltaTime = 40;
    
    boolean accelerometerInitiated;
    private Button playButton;
    private TextView pasoactual;
    private ImageView estado;
    private LinearLayout value_container;
    private int stateApp = 1;
    
    int cont = 0, paso = 0;
    
    /**
	 * Definición de los diferentes tipos de movimientos
	 */	
    public enum TipoMovimiento{
    	NINGUNO, ARRIBA, ABAJO, DERECHA, IZQUIERDA, ARRIBAPROFUNDO,ABAJOPROFUNDO
    }
    
    private TipoMovimiento tipoMov = TipoMovimiento.NINGUNO;

    /**
	 * Definición de hebras
	 */	
    protected void sensorThread() {
        Thread t = new Thread() {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(deltaTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(stateApp == 1)
                    	myHandler.post(ejecutarAccion);
                }
            }
        };
        t.start();
    }

    /**
	 * OnCreate Method Override 
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Remove title bar
        /*this.requestWindowFeature(Window.FEATURE_NO_TITLE);*/

        // Remove notification bar
        /*this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        accelerometer = new Accelerometer(this);
        accelX = accelY = accelZ =  0f;
        accelerometerInitiated = false;

        pasoactual = (TextView) findViewById(R.id.pasoactual);
        estado = (ImageView) findViewById(R.id.estado);
        value_container = (LinearLayout) findViewById(R.id.value_container);
        
        // Inicialización del TextView
        ((TextView) findViewById(R.id.valueAccXYZ)).setText( "Acc X: 0\nAcc Y: 0\nAcc Z: 0");
        
        setPlayButton(); 
        addButtonListener();
        
        sensorThread(); //Thread simultaneo para manejo del acelerometro (sensorEventListener)
    }
    
    /**
	 * Main Función Acelerómetro
	 */
    final Runnable ejecutarAccion = new Runnable(){
        public void run(){
            synchronized (this) {
                long currentTime = accelerometer.getAtTime();
                int limit = 35000000; //0,035 segundos
                float minMov = 1E-6f;
                float mov, movX, movY, movZ;
                long timeDiff;

                accelX = accelerometer.getAccelX();
                accelY = accelerometer.getAccelY();
                accelZ = accelerometer.getAccelZ();

                ((TextView) findViewById(R.id.valueXYZ)).setText( "Eje X: " + accelX.toString() + "\nEje Y: " + accelY.toString() + "\nEje Z: " + accelZ.toString());

                if (!accelerometerInitiated) {
                    lastUpdate = currentTime;
                    lastMov = currentTime;
                    accelerometer.actPrevAxisValues();
                    accelerometerInitiated = true;
                }

                timeDiff = currentTime - lastUpdate;
               
                if (timeDiff > 0) {
                	
                    if (currentTime - lastMov >= limit) { // Intervalo en el cual realizar las comprobaciones
                    	
                    	accelerometer.actAxisMov(timeDiff);
                    	mov = accelerometer.getTotalMov();
                    	movX = accelerometer.getMovXValue();
                    	movY = accelerometer.getMovYValue();
                    	movZ = accelerometer.getMovZValue();
                    	 
                    	if (mov > minMov) {
                    		((TextView) findViewById(R.id.valueAccXYZ)).setText( "Acc X: " + movX + "\nAcc Y: " + movY + "\nAcc Z: " + movZ);
                            
                            switch(tipoMov){
	                            case ARRIBA:
	                            	if(accelerometer.isPositiveMovY()){
	                        			cont++;
		                            	if(paso == 0){
	                        				paso = 1;
	                	        			estado.setImageResource(R.drawable.estado2);
	                	        			((TextView) findViewById(R.id.feedbackText)).setText("Realiza un movimiento hacia abajo");
	                        			}
	                            	}
	                            	if(accelerometer.isNegativeMovY()){
	                        			if(paso == 1){
	                        				paso = 2;
	                        				estado.setImageResource(R.drawable.estado3);
	                        				((TextView) findViewById(R.id.feedbackText)).setText("Realiza un movimiento hacia la derecha");
	                        			}
	                            	}
	                            	if(accelerometer.isPositiveMovX())
	                            		if(paso == 2){
	                            			paso = 3;
	                            			estado.setImageResource(R.drawable.estado4);
	                            			((TextView) findViewById(R.id.feedbackText)).setText("¡Objetivo conseguido!");
	                            			// Reproducción sonora (res/raw/)
	                            			MediaPlayer reproSonido = MediaPlayer.create(MainActivity.this, R.raw.fire); 
	                            			reproSonido.start(); 
	                        			}
	                            	break;
	                            default:
	                            	break;
                            }
                        }
                        lastMov = currentTime;
                    }
                    
                	TextView pasoactual = (TextView)findViewById(R.id.pasoactual);
                	pasoactual.setText(String.valueOf(paso));
                    
        	        accelerometer.actPrevAxisValues();
                    lastUpdate = currentTime;
                }
            }
        }
    };
   

	 /**
	 * Comienza la partida cuando se pulsa el botón play
	 */
	private void setPlayButton() {
		playButton = (Button) findViewById(R.id.play); // Reference the speak button

		// Set up click listener
		playButton.setOnClickListener(new OnClickListener() {              
	       
			@Override 
	        public void onClick(View v) {  
				playButton.setVisibility(View.GONE); 	// Ocultar boton de inicio
				pasoactual.setVisibility(View.VISIBLE); // Mostrar contador de pasos
				((TextView) findViewById(R.id.feedbackText)).setText("Realiza un movimiento hacia arriba");
				tipoMov = TipoMovimiento.ARRIBA;
				cont = 0;
	        }  
	    });

	}
	
	/**
	* Botón de reload
	*/
	public void addButtonListener() {
		imgButton = (ImageButton) findViewById(R.id.imageButton);
		imgButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				playButton.setVisibility(View.VISIBLE); 	// Mostrar boton de inicio
				pasoactual.setVisibility(View.GONE); // Ocultar contador de pasos
				((TextView) findViewById(R.id.feedbackText)).setText("Realiza un movimiento hacia arriba");
				estado.setImageResource(R.drawable.estado1);
				tipoMov = TipoMovimiento.NINGUNO;
				cont = 0;
				paso = 0;
				
				Toast.makeText(MainActivity.this,"Comando reiniciado", Toast.LENGTH_SHORT).show(); //Mensaje de feedback
			}
		});
	}
	
	
	/**
	 * Definición del menu de opciones de la aplicación
	 */	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Funcionalidad de los ítems del menu
	 */	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.optionEjes:
			if(mostrarEjes == 0) {
				value_container.setVisibility(View.VISIBLE);	// Mostrar los valores de XYZ
				mostrarEjes = 1;
			}
			else {
				value_container.setVisibility(View.GONE);		// Ocultar los valores de XYZ
				mostrarEjes = 0;
			}
			break;
		case R.id.optionLicencia:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.gnu.org/licenses/gpl.html")));
			break;
		case R.id.optionCodigo:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pebosch/a4e-acelerometroapp")));
			break;
		case R.id.optionAyuda:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.a4e.acelerometroapp")));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
