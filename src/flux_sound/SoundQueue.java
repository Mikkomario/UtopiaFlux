package flux_sound;

import flux_sound.SoundEvent.SoundEventType;
import genesis_event.EventSelector;
import genesis_util.LatchStateOperator;
import genesis_util.StateOperator;
import genesis_util.StateOperatorListener;

import java.util.LinkedList;
import java.util.List;

/**
 * SoundQueues play a number of sounds in a succession, starting the 
 * next sound when the last one stops playing. the Sounds can be added to the 
 * queue easily even during playing.
 *
 * @author Mikko Hilpinen.
 * @since 6.9.2013.
 */
public abstract class SoundQueue implements SoundListener, StateOperatorListener
{
	// ATTRIBUTES	------------------------------------------------------
	
	private List<Sound> sounds;
	private boolean diesatend, playing;
	private StateOperator isDeadOperator, listensToSoundsOperator;
	private EventSelector<SoundEvent> eventSelector;
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new empty soundqueue ready to play sounds
	 * 
	 * @param autodeath Should the queue die when it has played all the 
	 * sounds in it
	 */
	public SoundQueue(boolean autodeath)
	{
		// Initializes attributes
		this.isDeadOperator = new LatchStateOperator(false);
		this.listensToSoundsOperator = new StateOperator(true, false);
		this.eventSelector = SoundEvent.createEventTypeSelector(SoundEventType.END);
		
		this.sounds = new LinkedList<>();
		this.diesatend = autodeath;
		this.playing = false;
		
		getIsDeadStateOperator().getListenerHandler().add(this);
	}
	
	
	// ABSTRACT METHODS	-------------------------------------------------
	
	/**
	 * here the subclass is supposed to play the given sound using the settings 
	 * it deems necessary. The SoundQueue should be added as the specific 
	 * listener to the sound
	 *
	 * @param sound The sound that needs to be played
	 */
	protected abstract void playSound(Sound sound);
	
	
	// IMPLEMENTED METHODS	---------------------------------------------

	@Override
	public void onStateChange(StateOperator source, boolean newState)
	{
		if (source == getIsDeadStateOperator() && newState)
		{
			// Clears the sound list before dying
			this.playing = false;
			this.sounds.clear();
		}
	}
	
	@Override
	public StateOperator getIsDeadStateOperator()
	{
		return this.isDeadOperator;
	}

	@Override
	public void onSoundEvent(SoundEvent e)
	{
		// Removes the old sound from the queue
		if (!this.sounds.isEmpty())
			this.sounds.remove(0);
		
		// If there aren't more sounds to play
		if (this.sounds.isEmpty())
		{
			// Dies if autodeath is on
			if (this.diesatend)
				getIsDeadStateOperator().setState(true);
			
			this.playing = false;
		}
		// Plays the next sound (if still able)
		else if (isPlaying())
			playSound(this.sounds.get(0));
	}

	@Override
	public StateOperator getListensToSoundEventsOperator()
	{
		return this.listensToSoundsOperator;
	}

	@Override
	public EventSelector<SoundEvent> getSoundEventSelector()
	{
		return this.eventSelector;
	}
	
	
	// GETTERS & SETTERS	---------------------
	
	/**
	 * @return Whether the que is currently playing a sound
	 */
	public boolean isPlaying()
	{
		return this.playing;
	}
	
	
	// OTHER METHODS	-------------------------------------------------
	
	/**
	 * Plays through the sounds once, removing them after playing them
	 */
	public void play()
	{
		// Plays through the sounds (if there are any and if not already playing)
		if (getIsDeadStateOperator().getState() || isPlaying() || this.sounds.isEmpty())
			return;
		this.playing = true;
		playSound(this.sounds.get(0));
	}
	
	/**
	 * Stops the current sound from playing. The queue can be restarted with 
	 * play method. If a permanent stop is needed, it is adviced to use the 
	 * isDeadStateOperator method insted.
	 * 
	 * @see #play()
	 */
	public void stop()
	{
		this.playing = false;
		this.sounds.get(0).stop();
	}
	
	/**
	 * Empties the queue without stopping any sounds.
	 */
	public void empty()
	{
		this.sounds.clear();
	}
	
	/**
	 * Adds a sound to the queue. May also start playing the sound if needed.
	 *
	 * @param sound The sound added to the queue of played sounds
	 * @param playiffree Should the sound be played if there isn't a sound 
	 * playing yet.
	 */
	protected void addSound(Sound sound, boolean playiffree)
	{
		// Checks the argument
		if (sound == null)
			return;
		
		// Adds the sound to the list
		this.sounds.add(sound);
		
		// Plays the sound if needed & not playing another sound
		if (playiffree && !isPlaying())
		{
			playSound(this.sounds.get(0));
			this.playing = true;
		}
	}
}
