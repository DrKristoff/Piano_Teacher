package com.sidegigapps.pianoteacher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class AbstractMidiActivity extends AppCompatActivity implements OnMidiDeviceDetachedListener, OnMidiDeviceAttachedListener, OnMidiInputEventListener{
    final class OnMidiDeviceAttachedListenerImpl implements OnMidiDeviceAttachedListener {

        @Override
        public void onDeviceAttached(@NonNull UsbDevice usbDevice) {
            // deprecated method.
            // do nothing
        }

        @Override
        public void onMidiInputDeviceAttached(@NonNull final MidiInputDevice midiInputDevice) {
            if (AbstractMidiActivity.this.midiInputDevice != null) {
                return;
            }
            midiInputDevice.setMidiEventListener(AbstractMidiActivity.this);
            AbstractMidiActivity.this.midiInputDevice = midiInputDevice;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AbstractMidiActivity.this.onMidiInputDeviceAttached(midiInputDevice);
                }
            });
        }

    }

    /**
     * Implementation for single device connections.
     *
     * @author K.Shoji
     */
    final class OnMidiDeviceDetachedListenerImpl implements OnMidiDeviceDetachedListener {

        @Override
        public void onDeviceDetached(@NonNull UsbDevice usbDevice) {
            // deprecated method.
            // do nothing
        }

        @Override
        public void onMidiInputDeviceDetached(@NonNull final MidiInputDevice midiInputDevice) {
            if (AbstractMidiActivity.this.midiInputDevice != null && AbstractMidiActivity.this.midiInputDevice == midiInputDevice) {
                AbstractMidiActivity.this.midiInputDevice = null;
            }
            midiInputDevice.setMidiEventListener(null);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AbstractMidiActivity.this.onMidiInputDeviceDetached(midiInputDevice);
                }
            });
        }

    }

    MidiInputDevice midiInputDevice = null;
    OnMidiDeviceAttachedListener deviceAttachedListener = null;
    OnMidiDeviceDetachedListener deviceDetachedListener = null;
    private MidiDeviceConnectionWatcher deviceConnectionWatcher = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UsbManager usbManager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
        deviceAttachedListener = new OnMidiDeviceAttachedListenerImpl();
        deviceDetachedListener = new OnMidiDeviceDetachedListenerImpl();

        deviceConnectionWatcher = new MidiDeviceConnectionWatcher(getApplicationContext(), usbManager, deviceAttachedListener, deviceDetachedListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (deviceConnectionWatcher != null) {
            deviceConnectionWatcher.stop();
        }
        deviceConnectionWatcher = null;

        midiInputDevice = null;
    }


    /**
     * Suspends receiving/transmitting MIDI messages.
     * All events will be discarded until the devices being resumed.
     */
    protected final void suspendMidiDevices() {
        if (midiInputDevice != null) {
            midiInputDevice.suspend();
        }

    }

    /**
     * Resumes from {@link #suspendMidiDevices()}
     */
    protected final void resumeMidiDevices() {
        if (midiInputDevice != null) {
            midiInputDevice.resume();
        }
    }

    @Override
    public void onMidiRPNReceived(@NonNull MidiInputDevice sender, int cable, int channel, int function, int valueMSB, int valueLSB) {
        // do nothing in this implementation
    }

    @Override
    public void onMidiNRPNReceived(@NonNull MidiInputDevice sender, int cable, int channel, int function, int valueMSB, int valueLSB) {
        // do nothing in this implementation
    }

    @Override
    public void onMidiRPNReceived(@NonNull MidiInputDevice sender, int cable, int channel, int function, int value) {
        // do nothing in this implementation
    }

    @Override
    public void onMidiNRPNReceived(@NonNull MidiInputDevice sender, int cable, int channel, int function, int value) {
        // do nothing in this implementation
    }
}
