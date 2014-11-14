package flux_sound;

import genesis_logic.LogicalHandled;

/**
 * Soundlistener reacts to a start and/or end of a sound playing
 *
 * @author Mikko Hilpinen.
 *         Created 19.8.2013.
 * @see flux_sound.Sound
 */
public interface SoundListener extends LogicalHandled
{
	/**
	 * This method is called when a sound the listener listens to is played
	 * @param source the sound that just started
	 */
	public void onSoundStart(Sound source);
	
	/**
	 * This method is called when a sound the listener listens to ends
	 * @param source The sound that just ended
	 */
	public void onSoundEnd(Sound source);
}
