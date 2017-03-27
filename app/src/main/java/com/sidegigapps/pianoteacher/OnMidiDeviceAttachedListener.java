package com.sidegigapps.pianoteacher;

import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;

/**
 * Listener for MIDI attached events
 *
 * @author K.Shoji
 */
public interface OnMidiDeviceAttachedListener {

    /**
     * device has been attached
     *
     * @param usbDevice the attached UsbDevice
     */
    @Deprecated
    void onDeviceAttached(@NonNull UsbDevice usbDevice);

    /**
     * MIDI input device has been attached
     *
     * @param midiInputDevice attached MIDI Input device
     */
    void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice);

}