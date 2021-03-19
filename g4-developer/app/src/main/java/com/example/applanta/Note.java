package com.example.applanta;

public class Note {
    private float pitch;
    private long delay;
    private int voice;

    public Note(int voice, float pitch, long delay) {
        this.voice = voice;
        this.pitch = pitch;
        this.delay = delay;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
