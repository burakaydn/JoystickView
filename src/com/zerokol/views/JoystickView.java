package com.zerokol.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.view.View;

public class JoystickView extends View implements Runnable, SensorEventListener {
	// Constants
	private final double RAD = 57.2957795;
	public final static long DEFAULT_LOOP_INTERVAL = 100;
	public final static int FRONT = 3;
	public final static int FRONT_RIGHT = 2;
	public final static int LEFT_FRONT = 4;
	public final static int LEFT = 5;
	public final static int RIGHT = 1;
	public final static int RIGHT_BOTTOM = 8;
	public final static int BOTTOM_LEFT = 6;
	public final static int BOTTOM = 7;
	// Variables
	private OnJoystickMoveListener onJoystickMoveListener; // Listener
	private Thread thread = new Thread(this);
	private long loopInterval = DEFAULT_LOOP_INTERVAL;
	private float xPosition = 0; // Touch x position
	private float yPosition = 0; // Touch y position
	private double centerX = 0; // Center view x position
	private double centerY = 0; // Center view y position
	private Paint mainCircle;
	private Paint secondaryCircle;
	private Paint button;
	private Paint horizontalLine;
	private Paint verticalLine;
	private int joystickRadius;
	private int buttonRadius;
	private int lastAngle = 0;
	private int lastPower = 0;
	private float factor = 1f;
	private  float[] orientation = new float[3];

	public JoystickView(Context context) {
		super(context);
	}

	public JoystickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initJoystickView();
	}

	public JoystickView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		initJoystickView();
	}

	protected void initJoystickView() {
		mainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainCircle.setColor(Color.WHITE);
		mainCircle.setStyle(Paint.Style.FILL_AND_STROKE);

		secondaryCircle = new Paint();
		secondaryCircle.setColor(Color.GREEN);
		secondaryCircle.setStyle(Paint.Style.STROKE);

		verticalLine = new Paint();
		verticalLine.setStrokeWidth(2);
		verticalLine.setColor(Color.BLACK);

		horizontalLine = new Paint();
		horizontalLine.setStrokeWidth(2);
		horizontalLine.setColor(Color.BLACK);

		button = new Paint(Paint.ANTI_ALIAS_FLAG);
		button.setColor(Color.RED);
		button.setStyle(Paint.Style.FILL);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));

		setMeasuredDimension(d, d);

		buttonRadius = (int) (d / 2 * 0.25);
		joystickRadius = (int) (d / 2);
		factor = d / (2f * 90f); // for making equal button origin to joystickRadius when pitch equals to 90 or -90 
	}

	private int measure(int measureSpec) {
		int result = 0;

		// Decode the measurement specifications.
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Return a default size of 200 if no bounds are specified.
			result = 200;
		} else {
			// As you want to fill the available space
			// always return the full available bounds.
			result = specSize;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		centerX = (getWidth()) / 2;
		centerY = (getHeight()) / 2;

		// painting the main circle
		canvas.drawCircle((int) centerX, (int) centerY, joystickRadius,
				mainCircle);
		// painting the secondary circle
		canvas.drawCircle((int) centerX, (int) centerY, joystickRadius / 2,
				secondaryCircle);
		// paint lines
		canvas.drawLine((float) centerX, (float) centerY, (float) centerX,
				(float) (centerY - joystickRadius), verticalLine);
		canvas.drawLine((float) (centerX - joystickRadius), (float) centerY,
				(float) (centerX + joystickRadius), (float) centerY,
				horizontalLine);
		canvas.drawLine((float) centerX, (float) (centerY + joystickRadius),
				(float) centerX, (float) centerY, horizontalLine);

		// painting the move button
		canvas.drawCircle((int) xPosition, (int) yPosition, buttonRadius,
				button);
	}

	private int getAngle() {
		if (xPosition > centerX) {
			if (yPosition < centerY || yPosition > centerY) {
				return lastAngle = (int) (Math.atan((yPosition - centerY)
						/ (xPosition - centerX))
						* RAD + 90);
			} else {
				return lastAngle = 90;
			}
		} else if (xPosition < centerX) {
			if (yPosition < centerY || yPosition > centerY) {
				return lastAngle = (int) (Math.atan((yPosition - centerY)
						/ (xPosition - centerX)) * RAD) - 90;
			} else {
				return lastAngle = -90;
			}
		} else {
			if (yPosition <= centerY) {
				return lastAngle = 0;
			} else {
				if (lastAngle < 0) {
					return lastAngle = -180;
				} else {
					return lastAngle = 180;
				}
			}
		}
	}

	private int getPower() {
		return (int) (100 * Math.sqrt((xPosition - centerX)
				* (xPosition - centerX) + (yPosition - centerY)
				* (yPosition - centerY)) / joystickRadius);
	}

	private int getDirection() {
		if (lastPower == 0 && lastAngle == 0) {
			return 0;
		}
		int a = 0;
		if (lastAngle <= 0) {
			a = (lastAngle * -1) + 90;
		} else if (lastAngle > 0) {
			if (lastAngle <= 90) {
				a = 90 - lastAngle;
			} else {
				a = 360 - (lastAngle - 90);
			}
		}

		int direction = (int) (((a + 22) / 45) + 1);

		if (direction > 8) {
			direction = 1;
		}
		return direction;
	}

	public void setOnJoystickMoveListener(OnJoystickMoveListener listener,
			long repeatInterval) {
		this.onJoystickMoveListener = listener;
		this.loopInterval = repeatInterval;
	}

	public static interface OnJoystickMoveListener {
		public void onValueChanged(int angle, int power, int direction, float[] orientation);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		orientation = event.values;
		yPosition = yPosition < 0 ? Math.max(-90, event.values[1]) : Math.min(
				90, event.values[1]);
		yPosition = (int) ((yPosition * factor) + centerY);

		xPosition = xPosition < 0 ? Math.max(-90, event.values[2]) : Math.min(
				90, event.values[2]);
		xPosition = (int) ((xPosition * factor) + centerX);

		System.out.println("x: " + (xPosition - centerX) + " y: "
				+ (yPosition - centerY));
		double abs = Math.sqrt((xPosition - centerX) * (xPosition - centerX)
				+ (yPosition - centerY) * (yPosition - centerY));
		if (abs > joystickRadius) {
			xPosition = (int) ((xPosition - centerX) * joystickRadius / abs + centerX);
			yPosition = (int) ((yPosition - centerY) * joystickRadius / abs + centerY);
		}

		if (onJoystickMoveListener != null) {
			if (thread != null && thread.isAlive()) {
				thread.interrupt();
			}
			thread = new Thread(this);
			thread.start();
		}
		invalidate();
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			post(new Runnable() {
				public void run() {
					onJoystickMoveListener.onValueChanged(getAngle(),
							getPower(), getDirection(), orientation);
				}
			});
			try {
				Thread.sleep(loopInterval);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}