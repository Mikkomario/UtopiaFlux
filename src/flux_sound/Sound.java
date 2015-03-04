package flux_sound;

import flux_sound.SoundEvent.SoundEventType;
import genesis_event.Handled;
import genesis_util.StateOperator;
import genesis_util.StateOperatorListener;

/**
 * Sound is a sound or a music that can be played during the game. Each playable 
 * piece should extend this class. This class handles the listener informing and 
 * sets a standard for the subclasses.
 *
 * @author Mikko Hilpinen.
 * @since 19.8.2013.
 */
public abstract class Sound implements Handled, StateOperatorListener
{
	// ATTRIBUTES	------------------------------------------------------
	
	private SoundListener specificlistener;
	private SoundListenerHandler listenerhandler;
	private String name;
	private boolean playing;
	private StateOperator isDeadOperator;
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new sound with the given name
	 *
	 * @param name The name of the sound (in the bank)
	 */
	public Sound(String name)
	{
		// Initializes attributes
		this.name = name;
		this.listenerhandler = new SoundListenerHandler(false);
		this.specificlistener = null;
		this.playing = false;
		this.isDeadOperator = new StateOperator(false, true);
		
		this.isDeadOperator.getListenerHandler().add(this);
	}
	
	
	// ABSTRACT METHODS	--------------------------------------------------
	
	/**
	 * Plays the sound. This is meant for class-subclass interaction only and 
	 * the user should use the play() method instead.
	 * @see Sound#play(SoundListener)
	 */
	protected abstract void playSound();
	
	/**
	 * Loops the sound until it is stopped. This is meant for class-subclass 
	 * interaction only and the user should use the loop() method instead.
	 * @see Sound#loop(SoundListener)
	 */
	protected abstract void loopSound();
	
	/**
	 * Stops the sound from playing. This is meant for class-subclass 
	 * interaction only and the user should use the stop() method instead.
	 * @see Sound#stop()
	 */
	protected abstract void stopSound();
	
	/**
	 * Pauses the sound from playing
	 */
	public abstract void pause();
	
	/**
	 * Continues the sound from the spot it was paused at
	 */
	public abstract void unpause();
	
	
	// IMPLEMENTED METHODS	----------------------------------------------
	
	@Override
	public StateOperator getIsDeadStateOperator()
	{
		return this.isDeadOperator;
	}
	
	@Override
	public void onStateChange(StateOperator source, boolean newState)
	{
		// When the sound dies, it stops and empties the handler
		if (source == this.isDeadOperator && newState)
		{
			stop();
			this.specificlistener = null;
			getListenerHandler().removeAllHandleds();
			getListenerHandler().getIsDeadStateOperator().setState(true);
		}
	}
	
	
	// GETTERS & SETTERS	-----------------------
	
	/**
	 * @return The handler that informs the listners about the sound events created by this 
	 * sound
	 */
	public SoundListenerHandler getListenerHandler()
	{
		return this.listenerhandler;
	}
	
	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * Plays through the sound once. Informs the listener about the sound events 
	 * created by the sound.
	 *
	 * @param specificlistener A specific listener that will be informed about 
	 * the events caused by this play of the sound only (null if not needed)
	 */
	public void play(SoundListener specificlistener)
	{	
		// Only plays sounds if alive
		if (getIsDeadStateOperator().getState())
			return;
		
		// If the sound was already playing, stops the former one
		if (isPlaying())
			stop();
		
		// Informs the listeners about the event
		createSoundEvent(SoundEventType.START);
		
		this.playing = true;
		// Plays the sound
		playSound();
	}
	
	/**
	 * Loops the sound continuously until stopped
	 *
	 * @param specificlistener a listener that will be informed specifically 
	 * about the events caused by this play of the sound (null if not needed)
	 */
	public void loop(SoundListener specificlistener)
	{
		// Only plays sounds if alive
		if (getIsDeadStateOperator().getState())
			return;
		
		// If the sound was already playing, stops the former one
		if (isPlaying())
			stop();
		
		// Informs the listeners about the event
		createSoundEvent(SoundEventType.START);
		
		this.playing = true;
		// Plays the sound
		loopSound();
	}
	
	/**
	 * This method stops the sound from playing and informs the listeners about 
	 * the end of the sound
	 */
	public void stop()
	{
		// Only stops sounds if alive and playing
		if (getIsDeadStateOperator().getState() || !isPlaying())
			return;
		
		this.playing = false;
		// Stops the sound
		stopSound();
		// Informs the listeners about the event
		createSoundEvent(SoundEventType.END);
	}
	
	/**
	 * @return Is the sound currently playing or paused (true) or stopped (false)
	 */
	public boolean isPlaying()
	{
		return this.playing;
	}
	
	/**
	 * @return The name of the sound to differentiate it from other sounds
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Subclasses should call this method when a sound ends naturally but not 
	 * when stopSound method is called.
	 */
	protected void informSoundEnd()
	{
		// Updates the status
		this.playing = false;
		
		// Informs the listeners
		createSoundEvent(SoundEventType.END);
	}
	
	/**
	 * Subclasses should call this method when a sound starts outside the 
	 * playsound method
	 * @param specificlistener A listener that will be informed about events 
	 * during this one sound
	 */
	protected void informSoundStart(SoundListener specificlistener)
	{
		// Updates the status
		this.playing = true;
		this.specificlistener = specificlistener;
		
		// Informs the listeners
		createSoundEvent(SoundEventType.START);
	}
	
	private void createSoundEvent(SoundEventType eventType)
	{
		SoundEvent e = new SoundEvent(this, eventType);
		informListenerAboutEvent(this.specificlistener, e);
		informListenerAboutEvent(getListenerHandler(), e);
	}
	
	private static void informListenerAboutEvent(SoundListener l, SoundEvent e)
	{
		if (l != null && l.getListensToSoundEventsOperator().getState() && 
				l.getSoundEventSelector().selects(e))
			l.onSoundEvent(e);
	}
}
