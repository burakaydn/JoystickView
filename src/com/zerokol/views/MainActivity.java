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
    TextView txtAzimuth, txtPitch, txtRoll;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        txtAngle = (TextView) findViewById(R.id.txtAngle);
        txtPower = (TextView) findViewById(R.id.txtPower);
        txtDirection = (TextView) findViewById(R.id.txtDirection);
        
        txtAzimuth = (TextView) findViewById(R.id.txtAzimuth);
        txtPitch = (TextView) findViewById(R.id.txtPitch);
        txtRoll = (TextView) findViewById(R.id.txtRoll);
        
        joystick = (JoystickView) findViewById(R.id.joystickView);
        mSensorManager = (SensorManager)getApplicationContext().getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
         
        joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction, float[] orientation) {
            	txtAngle.setText("angle: " + String.valueOf(angle) + "°");
            	txtPower.setText("power: " + String.valueOf(power) + "%");
            	txtAzimuth.setText("azimuth: " + String.valueOf(orientation[0]) + "°");
            	txtPitch.setText("pitch: " + String.valueOf(orientation[1]) + "°");
            	txtRoll.setText("roll: " + String.valueOf(orientation[2]) + "°");
            	switch (direction) {
                case JoystickView.FRONT:
                    txtDirection.setText("front");
                    break;
                case JoystickView.FRONT_RIGHT:
                    txtDirection.setText("front_right");
                    break;
                case JoystickView.RIGHT:
                    txtDirection.setText("right");
                    break;
                case JoystickView.RIGHT_BOTTOM:
                    txtDirection.setText("right_bottom");
                    break;
                case JoystickView.BOTTOM:
                    txtDirection.setText("bottom");
                    break;
                case JoystickView.BOTTOM_LEFT:
                    txtDirection.setText("bottom_left");
                    break;
                case JoystickView.LEFT:
                    txtDirection.setText("left");
                    break;
                case JoystickView.LEFT_FRONT:
                    txtDirection.setText("left_front");
                    break;
                default:
                    txtDirection.setText("center");
                }
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
