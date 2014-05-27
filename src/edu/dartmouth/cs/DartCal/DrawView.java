/**
 * DartCal
 * File: DrawView.java
 * Author: Lisa Luo
 * Modified: 5/21/14
 * Description: Controls the drawing of schedules in the
 * 	Weekly and Friends fragments.
 */

package edu.dartmouth.cs.DartCal;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

// class to help draw things
public class DrawView extends View {
	private Paint paint = new Paint();

	private boolean isRotated = MainActivity.isRotated;
	private Context context = WeeklyFragment.mContext;
	private Paint mPaint = new Paint();
	private Canvas mCanvas;

	private EventDbHelper dbHelper = new EventDbHelper(context);
	// private PersonalEventDbHelper

	int rColor = generateRandomColor();

	public DrawView(Context context) {
		super(context);
	}

	// allows for insertion into xml
	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		setMeasuredDimension(measureWidth(widthMeasureSpec),
//				measureHeight(heightMeasureSpec));
		setMeasuredDimension(measureWidth(widthMeasureSpec), Globals.SCROLL_VIEW_HEIGHT);
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		// This is because of background image in relativeLayout, which is
		// 1000*1000px
		measureSpec = 1001;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.UNSPECIFIED) {
			// We were told how big to be
			result = specSize;
		}
		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		// This is because of background image in relativeLayout, which is
		// 1000*1000px
		measureSpec = 1001;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Here we say how high to be
			result = specSize;
		}
		
		System.out.println("SCREEN HEIGHT: " + result);
		return result;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// debugging hour blocks
		 paint.setStrokeWidth(0);
		 int dark = getResources().getColor(R.color.dark_green);
		 paint.setColor(dark);
		
		 canvas.drawRect(Globals.SATURDAY_LEFT, Globals.TIME_915AM,
		 Globals.SATURDAY_RIGHT, Globals.TIME_530PM, paint);

		try {

			// retrieve user profile information
			Friend userData = dbHelper.fetchEntryByIndex(1);

			// check for first time use
			if (userData != null) {

				ArrayList<Event> courseBlocks = userData.getSchedule();

				if (courseBlocks.size() > 0) {

					// fetch course information from the database
					int course1Time = courseBlocks.get(0).getClassPeriod();
					int course2Time = courseBlocks.get(1).getClassPeriod();
					int course3Time = courseBlocks.get(2).getClassPeriod();
					int course4Time = courseBlocks.get(3).getClassPeriod();

					// set color (defined in methods)
					paint.setStrokeWidth(0);

					// need to distinguish how long to draw the time blocks
					// also distinguish rotation

					if (Globals.callOnDraw) {

						int mint = getResources().getColor(R.color.mint_green);
						paint.setColor(mint);

						drawCourse(course1Time, canvas);
						drawCourse(course2Time, canvas);
						drawCourse(course3Time, canvas);
						drawCourse(course4Time, canvas);
						canvas.save();
					}

					// turn the xhours on; for use in the WEEKLY fragment
					if (Globals.xHoursOn) {
						int green = getResources().getColor(R.color.dark_green);
						paint.setColor(green);

						canvas.save();
						drawXhour(course1Time, canvas);
						drawXhour(course2Time, canvas);
						drawXhour(course3Time, canvas);
						drawXhour(course4Time, canvas);

						canvas.restore();
						invalidate();
					}
				}

				// Draw friends data; for use in the FRIENDS fragment
				if (Globals.drawFriends) {

					int color = generateRandomColor();
					paint.setColor(color);

					ArrayList<Friend> friendData = dbHelper.fetchEntries();

					if (friendData != null) {

						for (int i = 0; i < friendData.size(); i++) {

							ArrayList<Event> friendCourses = friendData.get(i)
									.getSchedule();

							int course1 = friendCourses.get(0).getClassPeriod();
							int course2 = friendCourses.get(1).getClassPeriod();
							int course3 = friendCourses.get(2).getClassPeriod();
							int course4 = friendCourses.get(3).getClassPeriod();

							drawCourse(course1, canvas);
							drawCourse(course2, canvas);
							drawCourse(course3, canvas);
							drawCourse(course4, canvas);

							if (Globals.friendXhoursOn) {
								drawXhour(course1, canvas);
								drawXhour(course2, canvas);
								drawXhour(course3, canvas);
								drawXhour(course4, canvas);
							}
						}
					}
				}

				// DATABASE CHANGE: change for every 15 minute
				// increment.......!!!!!!....spinner....
				// Draw events; for use in the TERM fragment
				if (Globals.drawEventsOn) {

					// GET THE EVENTS FROM THE DATABASE

					long startTime = 0;
					long endTime = 0;
					long date = 0;

					// drawCustomEvent(startTime, endTime, date);

				}

				invalidate();
			}

		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// debugging random color generator
		paint.setStrokeWidth(0);
		paint.setColor(rColor);
		drawCourse(Globals.PERIOD_10, canvas);
	}

	public boolean onTouch(View v, MotionEvent event) {
		invalidate();
		return true;
	}

	/**
	 * Draws specified course block.
	 * 
	 * @param period
	 * @param canvas
	 */
	public void drawCourse(int period, Canvas canvas) {

		switch (period) {

		case Globals.EARLY_DRILL:

			break;

		case Globals.PERIOD_8:
			canvas.drawRect(Globals.MONDAY_LEFT, Globals.TIME_8_TOP,
					Globals.MONDAY_RIGHT, Globals.TIME_8_BOTTOM, paint);
			canvas.drawRect(Globals.TUESDAY_LEFT, Globals.TIME_8_TOP,
					Globals.TUESDAY_RIGHT, Globals.TIME_8_BOTTOM, paint);
			canvas.drawRect(Globals.THURSDAY_LEFT, Globals.TIME_8_TOP,
					Globals.THURSDAY_RIGHT, Globals.TIME_8_BOTTOM, paint);
			canvas.drawRect(Globals.FRIDAY_LEFT, Globals.TIME_8_TOP,
					Globals.FRIDAY_RIGHT, Globals.TIME_8_BOTTOM, paint);
			break;

		case Globals.PERIOD_9L:
			canvas.drawRect(Globals.MONDAY_LEFT, Globals.TIME_9L_TOP,
					Globals.MONDAY_RIGHT, Globals.TIME_9L_BOTTOM, paint);
			canvas.drawRect(Globals.WEDNESDAY_LEFT, Globals.TIME_9L_TOP,
					Globals.WEDNESDAY_RIGHT, Globals.TIME_9L_BOTTOM, paint);
			canvas.drawRect(Globals.FRIDAY_LEFT, Globals.TIME_9L_TOP,
					Globals.FRIDAY_RIGHT, Globals.TIME_9L_BOTTOM, paint);
			break;

		case Globals.PERIOD_9S:
			canvas.drawRect(Globals.MONDAY_LEFT, Globals.TIME_9AM,
					Globals.MONDAY_RIGHT, Globals.TIME_9L_BOTTOM, paint);
			canvas.drawRect(Globals.TUESDAY_LEFT, Globals.TIME_9AM,
					Globals.TUESDAY_RIGHT, Globals.TIME_9L_BOTTOM, paint);
			canvas.drawRect(Globals.THURSDAY_LEFT, Globals.TIME_9AM,
					Globals.THURSDAY_RIGHT, Globals.TIME_9L_BOTTOM, paint);
			canvas.drawRect(Globals.FRIDAY_LEFT, Globals.TIME_9AM,
					Globals.FRIDAY_RIGHT, Globals.TIME_9L_BOTTOM, paint);
			break;

		case Globals.PERIOD_10:
			canvas.drawRect(Globals.MONDAY_LEFT, Globals.TIME_10AM,
					Globals.MONDAY_RIGHT, Globals.TIME_10_BOTTOM, paint);
			canvas.drawRect(Globals.WEDNESDAY_LEFT, Globals.TIME_10AM,
					Globals.WEDNESDAY_RIGHT, Globals.TIME_10_BOTTOM, paint);
			canvas.drawRect(Globals.FRIDAY_LEFT, Globals.TIME_10AM,
					Globals.FRIDAY_RIGHT, Globals.TIME_10_BOTTOM, paint);
			break;

		case Globals.PERIOD_10A:
			canvas.drawRect(Globals.TUESDAY_LEFT, Globals.TIME_10AM,
					Globals.TUESDAY_RIGHT, Globals.TIME_10A_BOTTOM, paint);
			canvas.drawRect(Globals.THURSDAY_LEFT, Globals.TIME_10AM,
					Globals.THURSDAY_RIGHT, Globals.TIME_10A_BOTTOM, paint);
			break;

		case Globals.PERIOD_11:
			canvas.drawRect(Globals.MONDAY_LEFT, Globals.TIME_11_TOP,
					Globals.MONDAY_RIGHT, Globals.TIME_11_BOTTOM, paint);
			canvas.drawRect(Globals.WEDNESDAY_LEFT, Globals.TIME_11_TOP,
					Globals.WEDNESDAY_RIGHT, Globals.TIME_11_BOTTOM, paint);
			canvas.drawRect(Globals.FRIDAY_LEFT, Globals.TIME_11_TOP,
					Globals.FRIDAY_RIGHT, Globals.TIME_11_BOTTOM, paint);
			break;

		case Globals.PERIOD_12:
			canvas.drawRect(Globals.MONDAY_LEFT, Globals.TIME_12_TOP,
					Globals.MONDAY_RIGHT, Globals.TIME_12_BOTTOM, paint);
			canvas.drawRect(Globals.WEDNESDAY_LEFT, Globals.TIME_12_TOP,
					Globals.WEDNESDAY_RIGHT, Globals.TIME_12_BOTTOM, paint);
			canvas.drawRect(Globals.FRIDAY_LEFT, Globals.TIME_12_TOP,
					Globals.FRIDAY_RIGHT, Globals.TIME_12_BOTTOM, paint);
			break;

		case Globals.PERIOD_2:
			canvas.drawRect(Globals.MONDAY_LEFT, Globals.TIME_2_TOP,
					Globals.MONDAY_RIGHT, Globals.TIME_2_BOTTOM, paint);
			canvas.drawRect(Globals.WEDNESDAY_LEFT, Globals.TIME_2_TOP,
					Globals.WEDNESDAY_RIGHT, Globals.TIME_2_BOTTOM, paint);
			canvas.drawRect(Globals.FRIDAY_LEFT, Globals.TIME_2_TOP,
					Globals.FRIDAY_RIGHT, Globals.TIME_2_BOTTOM, paint);
			break;

		case Globals.PERIOD_2A:
			canvas.drawRect(Globals.TUESDAY_LEFT, Globals.TIME_2PM,
					Globals.TUESDAY_RIGHT, Globals.TIME_2A_BOTTOM, paint);
			canvas.drawRect(Globals.THURSDAY_LEFT, Globals.TIME_2PM,
					Globals.THURSDAY_RIGHT, Globals.TIME_2A_BOTTOM, paint);
			break;

		case Globals.PERIOD_3A:
			canvas.drawRect(Globals.MONDAY_LEFT, Globals.TIME_3PM,
					Globals.MONDAY_RIGHT, Globals.TIME_3A_BOTTOM, paint);
			canvas.drawRect(Globals.THURSDAY_LEFT, Globals.TIME_4PM,
					Globals.THURSDAY_RIGHT, Globals.TIME_3B_BOTTOM, paint);
			break;

		case Globals.PERIOD_3B:
			canvas.drawRect(Globals.TUESDAY_LEFT, Globals.TIME_4PM,
					Globals.TUESDAY_RIGHT, Globals.TIME_3B_BOTTOM, paint);
			canvas.drawRect(Globals.THURSDAY_LEFT, Globals.TIME_4PM,
					Globals.THURSDAY_RIGHT, Globals.TIME_3B_BOTTOM, paint);
			break;

		case Globals.AFTERNOON_DRILL:
			break;

		case Globals.EVENING_DRILL:
			break;

		default:
			break;
		}

		invalidate();
	}

	/**
	 * Draws the x-hour associated with the specified course period.
	 * 
	 * @param period
	 * @param canvas
	 */
	public void drawXhour(int period, Canvas canvas) {

		switch (period) {

		// 8
		case Globals.PERIOD_8:
			canvas.drawRect(Globals.WEDNESDAY_LEFT, Globals.TIME_8_TOP,
					Globals.WEDNESDAY_RIGHT, Globals.TIME_8_BOTTOM, paint);
			break;

		// 9L
		case Globals.PERIOD_9L:
			canvas.drawRect(Globals.THURSDAY_LEFT, Globals.TIME_9AM,
					Globals.THURSDAY_RIGHT, Globals.TIME_9L_BOTTOM, paint);
			break;

		// 9S
		case Globals.PERIOD_9S:
			canvas.drawRect(Globals.WEDNESDAY_LEFT, Globals.TIME_9AM,
					Globals.WEDNESDAY_RIGHT, Globals.TIME_9L_BOTTOM, paint);
			break;

		// 10
		case Globals.PERIOD_10:
			canvas.drawRect(Globals.THURSDAY_LEFT, Globals.TIME_12PM,
					Globals.THURSDAY_RIGHT, Globals.TIME_1250PM, paint);
			break;

		// 10A
		case Globals.PERIOD_10A:
			canvas.drawRect(Globals.WEDNESDAY_LEFT, Globals.TIME_3PM,
					Globals.WEDNESDAY_RIGHT, Globals.TIME_2A_BOTTOM, paint);
			break;

		// 11
		case Globals.PERIOD_11:
			canvas.drawRect(Globals.TUESDAY_LEFT, Globals.TIME_12PM,
					Globals.TUESDAY_RIGHT, Globals.TIME_1250PM, paint);
			break;

		// 12
		case Globals.PERIOD_12:
			canvas.drawRect(Globals.TUESDAY_LEFT, Globals.TIME_1PM,
					Globals.TUESDAY_RIGHT, Globals.TIME_150PM, paint);
			break;

		// 2
		case 8:
			canvas.drawRect(Globals.THURSDAY_LEFT, Globals.TIME_1PM,
					Globals.THURSDAY_RIGHT, Globals.TIME_150PM, paint);
			break;

		// 2A
		case Globals.PERIOD_2A:
			canvas.drawRect(Globals.WEDNESDAY_LEFT, Globals.TIME_415PM,
					Globals.WEDNESDAY_RIGHT, Globals.TIME_505PM, paint);
			break;

		// 3A
		case Globals.PERIOD_3A:
			canvas.drawRect(Globals.MONDAY_LEFT, Globals.TIME_5PM,
					Globals.MONDAY_RIGHT, Globals.TIME_550PM, paint);
			break;

		// 3B
		case Globals.PERIOD_3B:
			canvas.drawRect(Globals.MONDAY_LEFT, Globals.TIME_5PM,
					Globals.MONDAY_RIGHT, Globals.TIME_550PM, paint);
			break;
		}

		invalidate();

	}

	/**
	 * For weeks activity in TERM fragment. Take the long time and translate to
	 * TOP variable for drawing.
	 * 
	 * @param startTime
	 * @return
	 */
	public float setStartTime(String startTime) {

		// parse the start time??

		float top = findTime(startTime);

		// take the long time and translate to TOP variable for drawing
		return top;
	}

	/**
	 * Takes the long time and translates to BOTTOM variable for drawing.
	 * 
	 * @param endTime
	 * @return
	 */
	public float setEndTime(long endTime) {

		int bottom;

		return bottom = 0;
	}

	/**
	 * Takes in an epoch time that contains start and end time, which includes
	 * the date.
	 * 
	 * @param startTime
	 * @param endTime
	 * @param canvas
	 */
	public void drawCustomEvent(long startTime, long endTime, Canvas canvas) {

		String start = parseTime(startTime);
		String end = parseTime(endTime);
		//
		// int top = setStartTime(startTime);
		// int bottom = setEndTime(endTime);

		// canvas.drawRect(left, right, top, bottom, paint);

	}

	/**
	 * Helper function to translate long time into pixel location to draw block.
	 * 
	 * @param time
	 * @return where to draw said time
	 */
	public float findTime(String time) {

		if (time == "7:00:00")
			return Globals.TIME_7AM;
//		if (time == "7:15:00")
//			return "715AM";
		// if (time == 89400)
		// return "730AM";
		if (time == "8:00:00")
			return Globals.TIME_8AM;
		if (time == "9:00:00")
			return Globals.TIME_9AM;
		if (time == "10:00:00")
			return Globals.TIME_10AM;
		if (time == "11:00:00")
			return Globals.TIME_11AM;
		if (time == "12:00:00")
			return Globals.TIME_12PM;
		if (time == "13:00:00")
			return Globals.TIME_1PM;
		if (time == "14:00:00")
			return Globals.TIME_2PM;
		if (time == "15:00:00")
			return Globals.TIME_3PM;
		if (time == "16:00:00")
			return Globals.TIME_4PM;
		if (time == "17:00:00")
			return Globals.TIME_5PM;
		if (time == "18:00:00")
			return Globals.TIME_6PM;
		if (time == "19:00:00")
			return Globals.TIME_7PM;
		if (time == "20:00:00")
			return Globals.TIME_8PM;
		if (time == "21:00:00")
			return Globals.TIME_9PM;
		if (time == "22:00:00")
			return Globals.TIME_10PM;
		if (time == "23:00:00")
			return Globals.TIME_11PM;
		if (time == "24:00:00")
			return Globals.TIME_12AM;
		if (time == "1:00:00")
			return Globals.TIME_1AM;
		if (time == "2:00:00")
			return Globals.TIME_2AM;
		if (time == "3:00:00")
			return Globals.TIME_3AM;
		if (time == "4:00:00")
			return Globals.TIME_4AM;
		if (time == "5:00:00")
			return Globals.TIME_5AM;
		if (time == "6:00:00")
			return Globals.TIME_6AM;

		// error code
		return -1;
	}

	/**
	 * Helper function to return a random color for assignment to friend.
	 * 
	 * @return
	 */
	public int generateRandomColor() {

		Random rand = new Random();
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);

		int randomColor = Color.argb(100, r, g, b);

		return randomColor;
	}

	/**
	 * From 1970 epoch time in seconds to a formatted time string
	 * 
	 * @param timeInMs
	 * @param context
	 * @return time in a formatted string
	 */
	public static String parseTime(long timeInMs) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInMs);
		SimpleDateFormat dateFormat;
		dateFormat = new SimpleDateFormat(Globals.TIME_FORMAT, Locale.US);
		System.out.println("parsetime function" + calendar.getTime());

		int unroundedMinutes = calendar.get(Calendar.MINUTE);
		int mod = unroundedMinutes % 15;
		calendar.add(Calendar.MINUTE, mod < 8 ? -mod : (15 - mod));
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return dateFormat.format(calendar.getTime());
	}

	/**
	 * From a formatted time string to a long (just time!)
	 * 
	 * @param timeString
	 * @return time in long
	 * @throws ParseException
	 */
	public static long timeBackToLong(String timeString) throws ParseException {

		SimpleDateFormat dateFormat;
		dateFormat = new SimpleDateFormat(Globals.TIME_FORMAT, Locale.US);

		System.out.println("df " + dateFormat.parse(timeString));
		System.out.println("df " + dateFormat.parse(timeString).getTime());

		// Time t = Time.valueOf(timeString);
		// System.out.println("t " + t);
		// long l = t.getTime();
		// System.out.println("l " + l);
		// return l;
		return dateFormat.parse(timeString).getTime();

	}

}
