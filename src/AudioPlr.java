import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AudioPlr implements Runnable {
    private Clip backgroundClip; // Background music clip
    private Map<String, Clip> soundEffects; // Store sound effect clips
    private boolean musicOn; // To track whether music is currently playing
    private boolean isLooping; // To control music loop in a thread
    private Thread musicThread; // Thread to manage background music playback

    // Constructor to initialize the background music
    public AudioPlr(String backgroundPath, boolean shouldLoop) {
        soundEffects = new HashMap<>();
        setupBackgroundAudio(backgroundPath, shouldLoop); // Load and configure the background music
        this.isLooping = shouldLoop;
    }

    // Setup method to load and configure the background audio file
    private void setupBackgroundAudio(String filePath, boolean shouldLoop) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            backgroundClip = (Clip) AudioSystem.getLine(info);
            backgroundClip.open(audioStream);
            this.isLooping = shouldLoop;

            // Optionally, set the volume level for background music
            FloatControl volumeControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-30.0f); // Reduce volume by 10 decibels (adjust as necessary)

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to load a sound effect from a file
    public void loadSoundEffect(String name, String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path for sound effect cannot be null or empty.");
        }

        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                throw new IOException("Sound file not found: " + filePath);
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip soundClip = (Clip) AudioSystem.getLine(info);
            soundClip.open(audioStream);
            FloatControl volumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-30.0f); // Reduce volume by 10 decibels (adjust as necessary)
            soundEffects.put(name, soundClip); // Store the sound effect clip in the map

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format: " + e.getMessage()); // Specific error handling for
                                                                               // unsupported format
        } catch (IOException e) {
            System.err.println("I/O error while loading sound: " + e.getMessage()); // Handling file-related issues
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + e.getMessage()); // Handle audio line issues
        }
    }

    // Method to play a specific sound effect by name
    public void playSoundEffect(String name) {
        Clip soundClip = soundEffects.get(name);
        if (soundClip != null) {
            soundClip.setFramePosition(0); // Rewind to the beginning
            soundClip.start(); // Play the sound effect
        }
    }

    // Method to start or resume background audio
    public void playAudio() {
        musicOn = true;
        if (backgroundClip != null) {
            musicThread = new Thread(this); // Start the music thread
            musicThread.start();
        }
    }

    // Method to stop the background audio
    public void endAudio() {
        musicOn = false;
        if (backgroundClip != null) {
            backgroundClip.stop();
        }

        // Interrupt the music thread if it's running
        if (musicThread != null && musicThread.isAlive()) {
            musicThread.interrupt();
        }
    }

    // Runnable method to handle background music playback in a separate thread
    @Override
    public void run() {
        while (musicOn) {
            backgroundClip.setFramePosition(0); // Rewind to the beginning
            backgroundClip.start();

            try {
                Thread.sleep(backgroundClip.getMicrosecondLength() / 1000); // Sleep until the clip finishes
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Exit if interrupted
            }
        }
    }

    // Method to check if the music is currently playing
    public boolean isMusicOn() {
        return musicOn;
    }

    // Method to stop and clear all sound effects (useful when ending the game)
    public void clearSoundEffects() {
        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                clip.stop();
                clip.close();
            }
        }
        soundEffects.clear(); // Clear the map
    }
}
