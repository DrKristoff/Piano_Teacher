package com.sidegigapps.pianoteacher;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;

public class DemoActivity extends AbstractMidiActivity {

    MidiManager midiManager;
    MidiDevice midiDevice;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_acitivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showToast("DemoActivity OnCreate");
        UsbManager manager = (UsbManager)getSystemService(Context.USB_SERVICE);

        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        try {
            // Perform the operation associated with our pendingIntent
            mPermissionIntent.send();
            showToast("permission intent sent");
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void setupMidiDevice(){

        midiManager = (MidiManager)getSystemService(Context.MIDI_SERVICE);
        showToast("midiManager null? " + String.valueOf(midiManager==null));
        MidiDeviceInfo[] deviceInfo = midiManager.getDevices();

        int numDevicesConnected = deviceInfo.length;

        showToast("DEVICES CONNECTED: " + String.valueOf(numDevicesConnected));

        if(numDevicesConnected==0) return;

        //TODO: detect if multiple devices are plugged in.  For now, just grab the first one in the array.  If multiple are plugged in, the 0th may not be correct
        MidiDeviceInfo info = deviceInfo[0];
        int numInputs = info.getInputPortCount();
        int numOutputs = info.getOutputPortCount();

        Bundle properties = info.getProperties();
        String manufacturer = properties
                .getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);

        midiManager.openDevice(info, new MidiManager.OnDeviceOpenedListener() {
            @Override
            public void onDeviceOpened(MidiDevice device) {
                if (device == null) {
                    Log.e("RCD", "could not open device ");
                    showToast("could not open device");
                } else {
                    midiDevice = device;
                    MidiOutputPort outputPort = device.openOutputPort(0);  //TODO:  controlling port selection
                    outputPort.connect(new MyReceiver());
                    showToast("device Opened!");
                }
            }}, new Handler(Looper.getMainLooper())
        );

    }

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {

                showToast("receiver captured broadcast  ");
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(accessory != null){
                            //call method to set up accessory communication
                            setupMidiDevice();
                        }
                    }
                    else {
                        showToast("permission denied for accessory " + accessory);
                    }
                }
            }
        }
    };

    class MyReceiver extends MidiReceiver {
        public void onSend(byte[] data, int offset,
                           int count, long timestamp) throws IOException {
            Toast.makeText(getApplicationContext(),data.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void showToast(final String message){
        DemoActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
        });

    }

    // User interface
    final Handler midiInputEventHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (midiInputEventAdapter != null) {
                midiInputEventAdapter.add((String)msg.obj);
            }
            // message handled successfully
            return true;
        }
    });

    ArrayAdapter<String> midiInputEventAdapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDeviceAttached(@NonNull UsbDevice usbDevice) {
        showToast("onDeviceAttached");
    }

    @Override
    public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
        showToast("onMidiInputDeviceAttached");

    }

    @Override
    public void onDeviceDetached(@NonNull UsbDevice usbDevice) {
        showToast("onDeviceDetached");
    }

    @Override
    public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
        showToast("onMidiInputDeviceDetached");
    }

    @Override
    public void onMidiMiscellaneousFunctionCodes(@NonNull MidiInputDevice sender, int cable, int byte1, int byte2, int byte3) {
        showToast("onMidiMiscellaneousFunctionCodes");
    }

    @Override
    public void onMidiCableEvents(@NonNull MidiInputDevice sender, int cable, int byte1, int byte2, int byte3) {

        showToast("onMidiCableEvents");
    }

    @Override
    public void onMidiSystemCommonMessage(@NonNull MidiInputDevice sender, int cable, byte[] bytes) {

        showToast("onMidiSystemCommonMessage");
    }

    @Override
    public void onMidiSystemExclusive(@NonNull MidiInputDevice sender, int cable, byte[] systemExclusive) {

        showToast("onMidiSystemExclusive");
    }

    @Override
    public void onMidiNoteOff(@NonNull MidiInputDevice sender, int cable, int channel, int note, int velocity) {

        showToast("onMidiNoteOff");
    }

    @Override
    public void onMidiNoteOn(@NonNull MidiInputDevice sender, int cable, int channel, int note, int velocity) {
        showToast("onMidiNoteOn");

    }

    @Override
    public void onMidiPolyphonicAftertouch(@NonNull MidiInputDevice sender, int cable, int channel, int note, int pressure) {
        showToast("onMidiPolyphonicAftertouch");

    }

    @Override
    public void onMidiControlChange(@NonNull MidiInputDevice sender, int cable, int channel, int function, int value) {
        showToast("onMidiControlChange");

    }

    @Override
    public void onMidiProgramChange(@NonNull MidiInputDevice sender, int cable, int channel, int program) {
        showToast("onMidiProgramChange");

    }

    @Override
    public void onMidiChannelAftertouch(@NonNull MidiInputDevice sender, int cable, int channel, int pressure) {
        showToast("onMidiChannelAftertouch");

    }

    @Override
    public void onMidiPitchWheel(@NonNull MidiInputDevice sender, int cable, int channel, int amount) {
        showToast("onMidiPitchWheel");

    }

    @Override
    public void onMidiSingleByte(@NonNull MidiInputDevice sender, int cable, int byte1) {
        showToast("onMidiSingleByte");

    }

    @Override
    public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice sender, int cable, int timing) {
        showToast("onMidiTimeCodeQuarterFrame");
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "TimeCodeQuarterFrame from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", timing: " + timing));
    }

    @Override
    public void onMidiSongSelect(@NonNull MidiInputDevice sender, int cable, int song) {
        showToast("onMidiSongSelect");
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SongSelect from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", song: " + song));
    }

    @Override
    public void onMidiSongPositionPointer(@NonNull MidiInputDevice sender, int cable, int position) {
        showToast("onMidiSongPositionPointer");
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SongPositionPointer from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", position: " + position));
    }

    @Override
    public void onMidiTuneRequest(@NonNull MidiInputDevice sender, int cable) {
        showToast("onMidiTuneRequest");
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "TuneRequest from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
    }

    @Override
    public void onMidiTimingClock(@NonNull MidiInputDevice sender, int cable) {
        showToast("onMidiTimingClock");
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "TimingClock from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
    }

    @Override
    public void onMidiStart(@NonNull MidiInputDevice sender, int cable) {
        showToast("onMidiStart");
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Start from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
    }

    @Override
    public void onMidiContinue(@NonNull MidiInputDevice sender, int cable) {
        showToast("onMidiContinue");
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Continue from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
    }

    @Override
    public void onMidiStop(@NonNull MidiInputDevice sender, int cable) {
        showToast("onMidiStop");
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Stop from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
    }

    @Override
    public void onMidiActiveSensing(@NonNull MidiInputDevice sender, int cable) {
        showToast("onMidiActiveSensing");
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ActiveSensing from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
    }

    @Override
    public void onMidiReset(@NonNull MidiInputDevice sender, int cable) {
        showToast("onMidiReset");
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Reset from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
    }

}
