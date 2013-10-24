package com.academyx.android.criminalintent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {
	
	private static final String TAG = "CrimeFragment";
	
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate() called");
		
		super.onCreate(savedInstanceState);
		mCrime = new Crime();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView() called");
		
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mCrime.setTitle(s.toString());
			}
		});
		
		mDateButton = (Button) v.findViewById(R.id.crime_date);
		
		String date = DateUtils.formatDateTime(this.getActivity(), mCrime.getDate().getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
		mDateButton.setText(date);
		mDateButton.setEnabled(false);
		
		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// Set the crime's solved state
				mCrime.setSolved(true);
			}
			
		});
		
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach() called");
		super.onAttach(activity);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy() called");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "onDetach() called");
		super.onDetach();
	}

	@Override
	public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
		Log.d(TAG, "onInflate() called");
		super.onInflate(activity, attrs, savedInstanceState);
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause() called");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume() called");
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.d(TAG, "onStart() called");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.d(TAG, "onStop() called");
		super.onStop();
	}

}
