/*
 Copyright (c) 2010, Sungjin Han <meinside@gmail.com>
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of meinside nor the names of its contributors may be
    used to endorse or promote products derived from this software without
    specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */

package org.andlib.ui;

import java.util.ArrayList;
import java.util.Calendar;

import org.andlib.helpers.Logger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * simple calendar view
 * 
 * @author meinside@gmail.com
 * @since 10.11.15.
 * 
 * last update 10.11.16.
 *
 */
public class SimpleCalendarView extends TableLayout
{
	public static final int NUM_ROWS = 6;
	public static final int NUM_COLUMNS = 7;
	
	protected Context context;

	private Calendar currentDate = null;
	private CalendarOnClickListener listener = null;
	
	private ArrayList<View> cells = null;
	
	private MonthDisplayHelper helper = null;

	/**
	 * @param context
	 * @param attrs
	 */
	public SimpleCalendarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	/**
	 * @param context
	 */
	public SimpleCalendarView(Context context)
	{
		super(context);
		initialize(context);
	}

	/**
	 * 
	 * @param context
	 */
	private void initialize(Context context)
	{
		this.context = context;
		currentDate = Calendar.getInstance();

		helper = new MonthDisplayHelper(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), Calendar.SUNDAY);

		TableRow.LayoutParams rowParams = new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rowParams.weight = 1f;
		rowParams.gravity = Gravity.CENTER_VERTICAL;
		rowParams.setMargins(0, 0, 0, 0);
		
		cells = new ArrayList<View>();

		for(int i=0; i<NUM_ROWS; i++)
		{
			TableRow row = new TableRow(context);

			for(int j=0; j<NUM_COLUMNS; j++)
			{
				View cell = createCalendarCell();
				cell.setTag(i * NUM_COLUMNS + j);	//save row+column index as tag

				cell.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View cell)
					{
						int row = (Integer)cell.getTag() / NUM_COLUMNS;
						int column = (Integer)cell.getTag() % NUM_COLUMNS;
						
						Logger.v("cell(" + column + "," + row + ") pressed");

						boolean isCurrentMonth = helper.isWithinCurrentMonth(row, column);
						int year = helper.getYear();
						int month;
						int day = helper.getDayAt(row, column);
						if(isCurrentMonth)
							month = helper.getMonth() + 1;
						else
							month = (day < 15 ? helper.getMonth() + 2 : helper.getMonth());

						goToYearMonthDay(year, month, day);
						if(listener != null)
						{
							listener.onClick(year, month, day);
							
							Logger.v("cell pressed - year: " + year + ", month: " + month + ", day: " + day);
						}
					}});

				//add calendar cells to the row view
				row.addView(cell);
				cells.add(cell);
			}

			//add row view to the calendar view
			addView(row, rowParams);
		}

		refresh();
	}

	/**
	 * override this function to change design of view
	 * 
	 * @param cell
	 * @param year
	 * @param month
	 * @param day
	 * @param isCurrentMonth
	 */
	protected void renderCell(View cell, int year, int month, int day, boolean isCurrentMonth)
	{
		TextView view = (TextView)cell;
		view.setText(String.format("%02d", day));

		if(isCurrentMonth && day == getCurrentDay())
		{
			view.setTextColor(0xFFFFFFFF);
			view.setBackgroundColor(Color.argb(128, 128, 128, 128));
		}
		else if(isCurrentMonth)
		{
			view.setTextColor(0xFFA0A0A0);
			view.setBackgroundColor(Color.argb(128, 0, 0, 0));
		}
		else
		{
			view.setTextColor(0xFF404040);
			view.setBackgroundColor(Color.argb(128, 0, 0, 0));
		}
	}

	/**
	 * override this function to change design of view
	 * 
	 * @return
	 */
	protected View createCalendarCell()
	{
		TextView view = new TextView(context);
		view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		view.setGravity(Gravity.RIGHT | Gravity.TOP);
		view.setPadding(3, 3, 3, 15);

		TableRow.LayoutParams cellParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		cellParams.weight = 1f;
		cellParams.gravity = Gravity.CENTER;
		cellParams.setMargins(1, 1, 1, 1);
		view.setLayoutParams(cellParams);

		return view;
	}

	/**
	 * 
	 */
	private void refresh()
	{
		helper = null;
		helper = new MonthDisplayHelper(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), Calendar.SUNDAY);

		int row, column;
		boolean isCurrentMonth;
		int year = helper.getYear(), month = helper.getMonth() + 1, day;

		for(View cell: cells)
		{
			row = (Integer)cell.getTag() / NUM_COLUMNS;
			column = (Integer)cell.getTag() % NUM_COLUMNS;

			day = helper.getDayAt(row, column);
			isCurrentMonth = helper.isWithinCurrentMonth(row, column);

			//render cell
			renderCell(cell, year, month, day, isCurrentMonth);
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getCurrentYear()
	{
		return currentDate.get(Calendar.YEAR);
	}

	/**
	 * 
	 * @return
	 */
	public int getCurrentMonth()
	{
		return currentDate.get(Calendar.MONTH) + 1;
	}

	/**
	 * 
	 * @return
	 */
	public int getCurrentDay()
	{
		return currentDate.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 
	 * @return
	 */
	public int getCurrentWeekday()
	{
		return currentDate.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 
	 * @return
	 */
	public Calendar getCurrentDate()
	{
		return currentDate;
	}

	/**
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public void goToYearMonthDay(int year, int month, int day)
	{
		currentDate.set(year, month - 1, day);
		
		refresh();
	}

	/**
	 * 
	 */
	public void goToPreviousYear()
	{
		currentDate.add(Calendar.YEAR, -1);
		
		refresh();
	}

	/**
	 * 
	 */
	public void goToNextYear()
	{
		currentDate.add(Calendar.YEAR, 1);
		
		refresh();
	}

	/**
	 * 
	 */
	public void goToPreviousMonth()
	{
		currentDate.add(Calendar.MONTH, -1);
		
		refresh();
	}

	/**
	 * 
	 */
	public void goToNextMonth()
	{
		currentDate.add(Calendar.MONTH, 1);
		
		refresh();
	}

	/**
	 * 
	 */
	public void goToPreviousDay()
	{
		currentDate.add(Calendar.DAY_OF_MONTH, -1);
		
		refresh();
	}

	/**
	 * 
	 */
	public void goToNextDay()
	{
		currentDate.add(Calendar.DAY_OF_MONTH, 1);
		
		refresh();
	}

	/**
	 * 
	 * @param listener
	 */
	public void setListener(CalendarOnClickListener listener)
	{
		this.listener = listener;
	}

	/**
	 * 
	 * @author meinside@gmail.com
	 *
	 */
	public interface CalendarOnClickListener
	{
		public void onClick(int year, int month, int day);
	}
}
