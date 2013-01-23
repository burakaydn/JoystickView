package com.zerokol.views;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.zerokol.views.JoystickView.OnJoystickMoveListener;


public class MainActivity extends Activity {
	private JoystickView joystick;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    TextView txtAngle, txtPower, txtDirection;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        txtAngle = (TextView) findViewById(R.id.txtAngle);
        txtPower = (TextView) findViewById(R.id.txtPower);
        txtDirection = (TextView) findViewById(R.id.txtDirection);
        joystick = (JoystickView) findViewById(R.id.joystickView);
        mSensorManager = (SensorManager)getApplicationContext().getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
         
        joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction) {
            	txtAngle.setText("angle: " + String.valueOf(angle) + "°");
            	txtPower.setText("power: " + String.valueOf(power) + "%");
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }
    
    @Override
    protected void onResume() {
    	mSensorManager.registerListener(joystick, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	mSensorManager.unregisterListener(joystick);
    	super.onPause();
    }
}
