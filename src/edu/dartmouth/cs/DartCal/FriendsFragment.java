package edu.dartmouth.cs.DartCal;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FriendsFragment extends Fragment {
	MenuItem friends;
	PersonalEventDbHelper database;
	ArrayList<String> selectedFriends;
	HashMap<String, String> nameMap;
	ArrayList<String> names;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.weekly_fragment, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		database = new PersonalEventDbHelper(getActivity());
		nameMap = new HashMap<String, String>();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		friends = menu.add("View Friends");
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		ArrayList<Event> values = database.fetchEntries();
		//this is the thing for updating the local database
		WeeksCalendar.AsyncTask;
		//CharSequence[] items;
		names = new ArrayList<String>();

		for (int i = 0; i < values.size(); i++){
			if (!nameMap.containsKey(values.get(i).getRegId())){
				nameMap.put(values.get(i).getOwnerName(), values.get(i).getRegId());
			}
		}
		Set<String> keySet = nameMap.keySet();

		for(String key : keySet){
			names.add(key);
		}
		CharSequence[] items = names.toArray(new CharSequence[names.size()]);
		//CharSequence[] items = {" Easy "," Medium "," Hard "," Very Hard "};
		// arraylist to keep the selected items

		final ArrayList<Integer> seletedItems=new ArrayList<Integer>();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Select Friends");
		builder.setMultiChoiceItems(items, null,
				new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int indexSelected,
					boolean isChecked) {
				if (isChecked) {
					// If the user checked the item, add it to the selected items
					seletedItems.add(indexSelected);
				} else if (seletedItems.contains(indexSelected)) {
					// Else, if the item is already in the array, remove it
					seletedItems.remove(Integer.valueOf(indexSelected));
				}
			}
		})
		// Set the action buttons
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				//  Your code when user clicked on OK
				//  You can write the code  to save the selected item here

				// System.out.println(seletedItems.size());

				for(int i = 0; i < seletedItems.size(); i++){
					selectedFriends.add(nameMap.get(names.get(seletedItems.get(i))));
				}

			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				//  Your code when user clicked on Cancel

			}
		});
		AlertDialog dialog;
		dialog = builder.create(); //create like this outside onClick
		dialog.show();

		return true;
	}

	public void displaySchedules() throws StreamCorruptedException, SQLException, ClassNotFoundException, IOException{

		ArrayList<Event> events = database.fetchEntries();
		
		ArrayList<Event> selectedEvents = new ArrayList<Event>();
		
		for(int j = 0; j < selectedFriends.size(); j++){
			for(int i = 0; i < events.size(); i++){
				if(events.get(i).getRegId() == selectedFriends.get(j)){
					selectedEvents.add(events.get(i));
				}
			}
			
		}

		selectedFriends.clear();
	}

}

