package com.example.ct.stupidtest;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.*;

/**
 * Created by CT on 11/24/15.
 */
public class PcmMonoAudioOutputStream extends OutputStream implements Closeable {

    final PcmAudioFormat format;
    final DataOutputStream dos;

    public PcmMonoAudioOutputStream(PcmAudioFormat format, DataOutputStream dos) {
        this.format = format;
        this.dos = dos;
    }

    public PcmMonoAudioOutputStream(PcmAudioFormat format, File file) throws IOException {
        this.format = format;
        this.dos = new DataOutputStream(new FileOutputStream(file));
    }

    public void write(int b) throws IOException {
        dos.write(b);
    }

    public void write(short[] shorts) throws IOException {
        dos.write(Bytes.toByteArray(shorts, shorts.length, format.isBigEndian()));
    }

    public void write(int[] ints) throws IOException {
        dos.write(Bytes.toByteArray(ints, ints.length, format.getBytePerSample(), format.isBigEndian()));
    }

    public void close() {
        IOs.closeSilently(dos);
    }

}
