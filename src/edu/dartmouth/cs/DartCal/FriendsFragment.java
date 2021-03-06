package edu.dartmouth.cs.DartCal;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class FriendsFragment extends Fragment {
	MenuItem friends;
	MenuItem friendsX;
	PersonalEventDbHelper database;
	EventDbHelper db;
	HashMap<String, String> nameMap;
	// ArrayList<String> names;
	ArrayList<Integer> prevSelection;
	WeeksCalendar cal;
	HashSet<String> names;
	private static DrawViewFriends drawView;
	boolean[] checkedItems;
	View rootView;
	private boolean showFriendXhours = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		rootView = inflater
				.inflate(R.layout.friends_fragment, container, false);
		drawView = (DrawViewFriends) rootView
				.findViewById(R.id.drawViewFriends);
		drawView.postInvalidate();

		return rootView;
	}

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// // Inflate the layout for this fragment
	// return inflater.inflate(R.layout.weekly_fragment, container, false);
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		database = new PersonalEventDbHelper(getActivity());
		nameMap = new HashMap<String, String>();
		db = new EventDbHelper(getActivity());
		// selectedFriends = new ArrayList<String>();
		names = new HashSet<String>();
		prevSelection = new ArrayList<Integer>();
		checkedItems = new boolean[500];

		ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
		query.findInBackground(new FindCallback<Event>() {
			@Override
			public void done(List<Event> events, ParseException error) {
				// TODO Auto-generated method stub
				// System.out.println(events.size());
				// System.out.println(events.get(0).getOwnerName());

				for (int i = 0; i < events.size(); i++) {
					names.add(events.get(i).getOwnerName());
				}
				names.remove(Globals.USER);
			}
		});

		// cal = new WeeksCalendar();
		// cal.AsyncTask();

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		friends = menu.add(0, 0, 0, "View Friends");
		friendsX = menu.add(0, 1, 1, "Toggle Friends' X-Hours");
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// ArrayList<Event> values = database.fetchEntries();
		// this is the thing for updating the local database
		// CharSequence[] items;
		// names = new ArrayList<String>();
		final CharSequence[] items = names.toArray(new CharSequence[names
				.size()]);
		// CharSequence[] items = {" Easy "," Medium "," Hard "," Very Hard "};
		// arraylist to keep the selected items

		final ArrayList<Integer> seletedItems = new ArrayList<Integer>();

		int itemId = item.getItemId();
		System.out.println("itemid in friends " + itemId);

		switch (itemId) {

		case 0:

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Select Friend(s) to Display Schedule");
			builder.setMultiChoiceItems(items, checkedItems,
					new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int indexSelected, boolean isChecked) {
							if (isChecked) {
								checkedItems[indexSelected] = true;
								// If the user checked the item, add it to the
								// selected items
								seletedItems.add(indexSelected);
							} else if (seletedItems.contains(indexSelected)) {
								checkedItems[indexSelected] = false;
								// Else, if the item is already in the array,
								// remove
								// it
								seletedItems.remove(Integer
										.valueOf(indexSelected));
							}
						}
					})
					// Set the action buttons
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// Your code when user clicked on OK
									// You can write the code to save the
									// selected item here

									// System.out.println(seletedItems.size());
									for (int i = 0; i < checkedItems.length; i++) {
										if (checkedItems[i] == true) {
											if (!seletedItems.contains(i))
												seletedItems.add(i);
										}

									}

									// clear the list and checked items if you
									// reset your profile
									prevSelection = seletedItems;
									for (int i = 0; i < seletedItems.size(); i++) {
										Globals.selectedFriends
												.add((String) items[seletedItems
														.get(i)]);
									}

									try {
										displaySchedules();
									} catch (StreamCorruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							})

					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// Your code when user clicked on Cancel

								}
							});
			AlertDialog dialog;
			dialog = builder.create(); // create like this outside onClick
			dialog.show();

			return true;

			// toggle x hours of selected friends
		case 1:

			if (!showFriendXhours) {
				showFriendXhours = true;
				Globals.friendXhoursOn = true;

				Toast.makeText(getActivity(), "X-Hours on", Toast.LENGTH_SHORT)
						.show();

				drawView.invalidate();
				break;
			}

			if (showFriendXhours) {
				showFriendXhours = false;
				Globals.friendXhoursOn = false;
				Toast.makeText(getActivity(), "X-Hours off", Toast.LENGTH_SHORT)
						.show();
				drawView.invalidate();
				break;
			}

			return true;
		}

		return false;
	}

	public static void displaySchedules() throws StreamCorruptedException,
			SQLException, ClassNotFoundException, IOException {
		System.out.println("clearing the drawing matrix!!!");
		int size = Globals.drawingMatrix.size();
		for (int i = 0; i < size; i++) {
			Globals.drawingMatrix.get(i).clear();
		}
		Globals.drawingMatrix.clear();

		for (int i = 0; i < Globals.selectedFriends.size(); i++) {

			ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
			query.whereEqualTo("ownerName", Globals.selectedFriends.get(i));
			try {
				List<Event> event = query.find();
				System.out.println(event.get(0).getOwnerName());
				System.out.println(event.get(0).getEventName());
				System.out.println(event.get(0).getClassPeriod());
				Globals.drawingMatrix.add((ArrayList<Event>) event);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("setting boolean to true");
		Globals.selectedFriends.clear();
		Globals.drawFriends = true;
		drawView.invalidate();
	}

	public void check() {
		ArrayList<Friend> list = null;
		try {
			list = db.fetchEntries();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(list.get(0).getSchedule().get(0).getEventName());
		System.out.println(list.get(0).getSchedule().get(1).getEventName());

		System.out.println(db.removeEntries());
	}

}
