package com.anvy.projects.dssviewer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.anvy.projects.dssviewer.R;

public class RotatedFrameLayout extends FrameLayout {

	public enum Rotation {
		Degrees_0, Degrees_90, Degrees_180, Degrees_270
	}

	private Rotation rotation = Rotation.Degrees_0;

	public RotatedFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotatedFrameLayout,
        		defStyle, 0);
        int rotation = a.getInt(R.styleable.RotatedFrameLayout_rotation,
        		0);
        a.recycle();
		init(rotation);
	}

	public RotatedFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RotatedFrameLayout(Context context) {
		super(context);
		init(0);
	}

	@SuppressLint("NewApi")
	private void init(int rotation) {
		switch (rotation) {
		case 0:
			this.rotation = Rotation.Degrees_0;
			break;
		case 1:
			this.rotation = Rotation.Degrees_90;
			break;
		case 2:
			this.rotation = Rotation.Degrees_180;
			break;
		case 3:
			this.rotation = Rotation.Degrees_270;
			break;
		}
		setPivotX(0);
		setPivotY(0);
	}

	public void rotate(Rotation rotation) {
		setPivotX(0);
		setPivotY(0);
		this.rotation = rotation;
		requestLayout();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		switch (rotation) {
		case Degrees_0:
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			setRotation0();
			break;
		case Degrees_90:
			super.onMeasure(heightMeasureSpec, widthMeasureSpec);
			setRotation90();
			break;
		case Degrees_180:
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			setRotation180();
			break;
		case Degrees_270:
			super.onMeasure(heightMeasureSpec, widthMeasureSpec);
			setRotation270();
			break;
		}
	}

	private void setRotation0() {
		setRotation(0);
		setTranslationX(0);
		setTranslationY(0);
	}

	private void setRotation90() {
		setRotation(90);
		setTranslationX(getMeasuredHeight());
		setTranslationY(0);
	}

	private void setRotation180() {
		setRotation(180);
		setTranslationX(getMeasuredWidth());
		setTranslationY(getMeasuredHeight());
	}

	private void setRotation270() {
		setRotation(270);
		setTranslationX(0);
		setTranslationY(getMeasuredWidth());
	}

}
