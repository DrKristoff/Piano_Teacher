package com.sidegigapps.pianoteacher;
import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;

/**
 * Listener for MIDI detached events
 *
 * @author K.Shoji
 */
public interface OnMidiDeviceDetachedListener {

    /**
     * device has been detached
     *
     * @param usbDevice the detached UsbDevice
     */
    @Deprecated
    void onDeviceDetached(@NonNull UsbDevice usbDevice);

    /**
     * MIDI input device has been detached
     *
     * @param midiInputDevice detached MIDI Input device
     */
    void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice);
}