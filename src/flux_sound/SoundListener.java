package flux_sound;

import genesis_event.EventSelector;
import genesis_event.Handled;
import genesis_util.StateOperator;

/**
 * Soundlistener reacts to a start and/or end of a sound playing
 *
 * @author Mikko Hilpinen.
 * @since 19.8.2013.
 * @see flux_sound.Sound
 */
public interface SoundListener extends Handled
{
	/**
	 * This method is called when a listener receives an event it is interested in
	 * @param e A sound event
	 */
	public void onSoundEvent(SoundEvent e);
	
	/**
	 * @return The stateOperator that defines whether the listener is willing to receive any 
	 * sound events
	 */
	public StateOperator getListensToSoundEventsOperator();
	
	/**
	 * @return A selector that selects events that interest the listener
	 */
	public EventSelector<SoundEvent> getSoundEventSelector();
}
