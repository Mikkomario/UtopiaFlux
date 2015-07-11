package flux_midi;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

import flux_sound.Sound;
import flux_sound.SoundListener;


/**
 * midiSounds are musical objects which can be played. Only one midiSound should 
 * be played at a time.
 * 
 * @author Unto Solala & Mikko Hilpinen.
 * @since 10.7.2013
 */
public class MidiSound extends Sound implements MetaEventListener
{
	// ATTRIBUTES ---------------------------------------------------------

	private String fileName;
	private Sequence midiSequence;
	private Sequencer midiSequencer;
	private long pauseposition, nextLoopStart, nextLoopEnd;
	private double defaultTempo, defaultGain, nextTempo, nextGain;
	private int nextLoopCount;
	private boolean paused;

	
	// CONSTRUCTOR ---------------------------------------------------------

	/**
	 * Creates midiSound-object.
	 * 
	 * @param fileName Which midi file is used to play the music (data/ 
	 * automatically included).
	 * @param name The name of the midiSound (in the midiSoundbank)
	 */
	public MidiSound(String fileName, String name)
	{
		super(name);
		
		// Initializes attributes
		initialize(fileName, 1, 1);
	}
	
	/**
	 * Creates midiSound-object.
	 * 
	 * @param fileName Which midi file is used to play the music (data/ 
	 * automatically included).
	 * @param name The name of the midiSound (in the midiSoundbank)
	 * @param defaultGain The gain that always affects the sound
	 * @param defaultTempo The tempo factor that always affects the sound
	 */
	public MidiSound(String fileName, String name, double defaultGain, double defaultTempo)
	{
		super(name);
		
		// Initializes attributes
		initialize(fileName, defaultTempo, defaultGain);
	}
	
	
	// IMPLEMENTED METHODS	-------------------------------------------
	
	@Override
	protected void playSound()
	{
		// Plays the music once from the very beginning
		startMusic(0);
		setLoopCount(0);
		//setLoopStart(0);
		//setLoopEnd(-1);
	}

	@Override
	protected void loopSound()
	{
		// Loops the music continuously
		startMusic(0);
		setLoopCount(-1);
		//setLoopStart(0);
		//setLoopEnd(-1);
	}

	@Override
	protected void stopSound()
	{
		// Stops the music from playing and informs the listeners
		if (this.midiSequencer.isRunning())
		{
			// Doesn't listen to the sequencer anymore
			this.midiSequencer.removeMetaEventListener(this);
			this.midiSequencer.stop();
			this.midiSequencer.close();
		}
	}

	@Override
	public void pause()
	{
		if (this.midiSequencer.isRunning())
		{
			this.midiSequencer.stop();
			this.pauseposition = this.midiSequencer.getTickPosition();
			this.paused = true;
		}
	}

	@Override
	public void unpause()
	{
		if (!this.midiSequencer.isRunning() && isPlaying())
		{
			// Starts the music from the spot it was at
			startMusic(this.pauseposition);
		}
	}
	
	@Override
	public void meta(MetaMessage event)
	{
		// Checks if a midi ended and informs the listeners
		if (event.getType() == 47)
		{
			// Doesn't listen to the sequencer anymore
			this.midiSequencer.removeMetaEventListener(this);
			// Informs that the music stopped
			informSoundEnd();
		}
	}
	

	// OTHER METHODS ---------------------------------------------------
	
	/**
	 * @return Is the sound on a pause
	 */
	public boolean isPaused()
	{
		return this.paused;
	}
	
	/**
	 * Starts playing the music from the given position.
	 * 
	 * @param startPosition	 Playback's starting tick-position.
	 * @param specificlistener A listener that will be informed about the 
	 * events caused by this specific play (null if not needed)
	 */
	public void startMusic(long startPosition, SoundListener specificlistener)
	{
		// Stops old music if still playing
		if (isPlaying())
			stop();
		
		// Informs listeners and starts the music
		informSoundStart(specificlistener);
		startMusic(startPosition);
	}

	/**
	 * @return Returns the length of a Midi-sequence in ticks.
	 */
	public long getSequenceLength()
	{
		return this.midiSequence.getTickLength();
	}

	private void startMusic(long startPosition)
	{	
		// Adds the music as a listener to the sequencer
		this.midiSequencer.addMetaEventListener(this);
		
		//Now let's try to set our sequence
		try
		{		
			this.midiSequencer.setSequence(this.midiSequence);
		}
		catch (InvalidMidiDataException e)
		{
			System.err.println("Midi was invalid!");
			e.printStackTrace();
		}
		try
		{
			this.midiSequencer.open();
		}
		catch (MidiUnavailableException mue)
		{
			System.err.println("Midi" + getName() +  "was unavailable!");
			mue.printStackTrace();
		}
		
		// Changes the music stats according to previous changes
		if (!isPaused())
		{
			setLoopStart(this.nextLoopStart);
			setLoopEnd(this.nextLoopEnd);
			setTempoFactor(this.nextTempo);
			setLoopCount(this.nextLoopCount);
			setGain(this.nextGain);
			
			this.nextLoopStart = 0;
			this.nextLoopEnd = -1;
			this.nextTempo = 1;
			this.nextGain = 1;
			this.nextLoopCount = 0;
		}
		else
			this.paused = false;
		
		this.midiSequencer.setTickPosition(startPosition);
		this.midiSequencer.start();
	}
	
	/**
	 * Sets how many times the music loops.
	 * 
	 * @param loopCount	How many times the music loops. If loopCount is 
	 * negative, the music will loop continuously.
	 */
	public void setLoopCount(int loopCount)
	{
		if (isPlaying())
		{
			if (loopCount < 0)
			{
				System.out.println("Looping");
				this.midiSequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			}
			else
			{
				System.out.println("Not looping");
				this.midiSequencer.setLoopCount(loopCount);
			}
		}
		else
			this.nextLoopCount = loopCount;
	}

	/**
	 * Changes where the music's loop starts.
	 * 
	 * @param loopStartPoint The tick where music's loop starts.
	 */
	public void setLoopStart(long loopStartPoint)
	{
		if (isPlaying())
			this.midiSequencer.setLoopStartPoint(loopStartPoint);
		else
			this.nextLoopStart = loopStartPoint;
	}

	/**
	 * Changes where the music's loop ends.
	 * 
	 * @param loopEndPoint The tick where music's loop ends. (0 means no end point, 
	 * -1 means the end of the midi)
	 */
	public void setLoopEnd(long loopEndPoint)
	{
		if (isPlaying())
			this.midiSequencer.setLoopEndPoint(loopEndPoint);
		else
			this.nextLoopEnd = loopEndPoint;
	}
	
	/**
	 * Resets loop's start-point to 0 and end-point to the end of the sequence.
	 */
	public void setDefaultLoopPoints()
	{
		setLoopStart(0);
		setLoopEnd(-1);
	}
	
	/**
	 * Sets a new tempo for the midi. 1.0 is the default TempoFactor.
	 * 
	 * @param newTempoFactor New tempoFactor for the midi (0+) (1.0 by default)
	 */
	public void setTempoFactor (double newTempoFactor)
	{
		if (isPlaying())
			this.midiSequencer.setTempoFactor((float) (newTempoFactor * this.defaultTempo));
		else
			this.nextTempo = newTempoFactor;
	}
	
	/**
	 * Sets the TempoFactor to 1.0, which is the default.
	 */
	public void resetTempoFactor()
	{
		setTempoFactor(1);
	}
	
	/**
	 * Returns the current TempoFactor.
	 * 
	 * @return	Returns the current TempoFactor as a float.
	 */
	public double getTempoFactor()
	{
		return this.midiSequencer.getTempoFactor() / this.defaultTempo;
	}
	
	/**
	 * Changes the gain of the sound, which affects the volume. Unfortunately this feature 
	 * simply doesn't work. I've tried everything.
	 * @param newGain The new gain of the sound [0, 1]
	 */
	public void setGain(double newGain)
	{
		if (isPlaying())
		{
			Synthesizer synthesizer = null;
			
			// Volume control 
			if (this.midiSequencer instanceof Synthesizer)
				synthesizer =  (Synthesizer) this.midiSequencer;
			else
			{
				try
				{
					synthesizer = MidiSystem.getSynthesizer();
					synthesizer.open();
				}
				catch (MidiUnavailableException e)
				{
					System.err.println("Midi unavailable");
					e.printStackTrace();
				}
			}
			
			MidiChannel[] channels = synthesizer.getChannels();

			// gain is a value between 0 and 1 (loudest)
			for (int i = 0; i < channels.length; i++)
			{
				channels[i].controlChange(7, (int) (newGain * this.defaultGain * 127.0));
			}
		}
		else
			this.nextGain = newGain;
	}
	
	private void initialize(String fileName, double tempo, double gain)
	{
		// Initializes attributes
		this.fileName = "data/" + fileName;
		this.pauseposition = 0;
		this.defaultTempo = tempo;
		this.defaultGain = gain;
		this.nextLoopStart = 0;
		this.nextLoopEnd = -1;
		this.nextTempo = 1;
		this.nextGain = 1;
		this.paused = false;
		
		// tries to create the midisequence
		try
		{
			this.midiSequence = MidiSystem.getSequence(new File(this.fileName));
		}
		catch (InvalidMidiDataException e)
		{
			System.err.println("Couldn't find create a midisequence!");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println("IOException whilst creating midisequence!");
			e.printStackTrace();
		}
		// Now let's try and set-up our midiSequencer
		try
		{
			this.midiSequencer = MidiSystem.getSequencer();
		}
		catch (MidiUnavailableException e)
		{
			System.err.println("Problems whilst setting up sequencer!");
			e.printStackTrace();
		}
	}
}
