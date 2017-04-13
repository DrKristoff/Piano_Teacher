package com.sidegigapps.pianoteacher;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sidegigapps.pianoteacher.DemoActivity.MidiStatus;

/**
 * A placeholder fragment containing a simple view.
 */
public class DemoActivityFragment extends Fragment {

    TextView midiStatusTextView, midiNoteStatusTextView;

    public DemoActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_demo_activity, container, false);
        midiStatusTextView = (TextView)rootView.findViewById(R.id.textViewMidiStatus);
        midiNoteStatusTextView = (TextView) rootView.findViewById(R.id.textViewNoteStatus);

        return rootView;
    }

    public void updateMidiStatus(MidiStatus status){
        updateStatus(midiStatusTextView,status);
    }

    private void updateStatus(TextView view, MidiStatus status){
        if(status==MidiStatus.CONNECTED){
            view.setBackgroundColor(Color.GREEN);
        } else if (status==MidiStatus.DISCONNECTED){
            view.setBackgroundColor(Color.GRAY);
        }
    }
}
