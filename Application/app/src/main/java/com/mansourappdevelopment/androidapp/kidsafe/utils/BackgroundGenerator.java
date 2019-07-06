package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.graphics.drawable.DrawableCompat;

import com.mansourappdevelopment.androidapp.kidsafe.R;

import java.util.Random;

public class BackgroundGenerator {
	public static Random random = new Random();
	
	public static Drawable getBackground(Context context) {
		Drawable drawable = DrawableCompat.wrap(context.getResources().getDrawable(R.drawable.app_background).mutate());
		DrawableCompat.setTint(drawable, getRandomColor());
		return drawable;
	}
	
	public static int getRandomColor() {
		int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
		return color;
	}
	
	public static String getFirstCharacters(String name) {
		StringBuilder firstCharacters = new StringBuilder();
		for (String word : name.split(" ")) {
			if (Character.isAlphabetic(word.charAt(0))) firstCharacters.append(word.charAt(0));
		}
		
		return firstCharacters.toString().toUpperCase();
	}
}
