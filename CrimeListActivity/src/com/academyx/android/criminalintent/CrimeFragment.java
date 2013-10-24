package com.academyx.android.criminalintent;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID = "criminalintent.CRIME_ID";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_DIAL = 3;

    Crime mCrime;
    EditText mTitleField;
    Button mDateButton;
    CheckBox mSolvedCheckBox;
    ImageButton mPhotoButton;
    ImageView mPhotoView;
    Button mSuspectButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        setHasOptionsMenu(true);
    }

    public void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    @Override
    @TargetApi(11)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });
        
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                    .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });
        
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        });

        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // launch the camera activity
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });
        
        // if camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mPhotoButton.setEnabled(false);
        }

        mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p == null) 
                    return;

                FragmentManager fm = getActivity()
                    .getSupportFragmentManager();
                String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.createInstance(path)
                    .show(fm, DIALOG_IMAGE);
            }
        });
        
        Button reportButton = (Button) v.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                
                PackageManager pm = getActivity().getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
                if (activities.size() > 0) {
                	i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                    i = Intent.createChooser(i, getString(R.string.send_report));
                    startActivity(i);
                }
            }
        });
        
        Button callButton = (Button) v.findViewById(R.id.crime_callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            	PackageManager pm = getActivity().getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
                if (activities.size() > 0) {
                	i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                	startActivityForResult(i, REQUEST_DIAL);
                }
            }
        });
        
        mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            	PackageManager pm = getActivity().getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
                if (activities.size() > 0) {
                	startActivityForResult(i, REQUEST_CONTACT);
                }
            }
        });
        
        if (mCrime.getSuspect() != null) {
        	mSuspectButton.setText(mCrime.getSuspect());
        }
        
        return v; 
    }
    
    private String getCrimeReport() {
    	String solvedString = null;
    	if (mCrime.isSolved()) {
    		solvedString = getString(R.string.crime_report_solved);
    	} else {
    		solvedString = getString(R.string.crime_report_unsolved);
    	}
    	
    	String dateFormat = "EEE, MMM dd";
    	String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
    	
    	String suspect = mCrime.getSuspect();
    	if (suspect == null) {
    		suspect = getString(R.string.crime_report_no_suspect);
    	} else {
    		suspect = getString(R.string.crime_report_suspect, suspect);
    	}
    	
    	String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
    	
    	return report;
    }
    
    private void showPhoto() {
        // (re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity()
                .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            // create a new Photo object and attach it to the crime
            String filename = data
                .getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                Photo p = new Photo(filename);
                mCrime.setPhoto(p);
                showPhoto();
            }
        } else if (requestCode == REQUEST_CONTACT) {
        	Uri contactData = data.getData();
        	
        	// Specify which fields you want your query to return values for
        	String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
        	
        	// Perform your query - the contactUri is like a "where" clause here
        	Cursor c = getActivity().getContentResolver().query(contactData, queryFields, null, null, null);
        	
        	// Check if there are results
        	if (c.getCount() == 0) {
        		c.close();
        		return;
        	}
        	
        	// Pull out the first column of the first row of data, that is your suspect's name
        	c.moveToFirst();
        	String suspect = c.getString(0);
        	mCrime.setSuspect(suspect);
        	mSuspectButton.setText(suspect);
        	c.close();
        } else if (requestCode == REQUEST_DIAL) {
        	Uri contactData = data.getData();
        	Log.d("Contact", "Contact Data: " + contactData.toString());
        	
        	// Specify which fields you want your query to return values for
        	String[] queryFields = new String[]{Phone.NUMBER};
        	
        	// Perform your query - the contactUri is like a "where" clause here
        	Cursor c = getActivity().getContentResolver().query(contactData, queryFields, null, null, null);
        	
        	// Check if there are results
        	if (c.getCount() == 0) {
        		c.close();
        		return;
        	}
        	
        	// Pull out the first column of the first row of data, that is your suspect's name
        	c.moveToFirst();
        	String number = c.getString(0);
            Log.d("Contact", "Selected number: " + number);
            c.close();
        	
        	Uri numberUri = Uri.parse("tel:" + number);
    		startActivity(new Intent(Intent.ACTION_DIAL, numberUri));
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } 
    }
}
