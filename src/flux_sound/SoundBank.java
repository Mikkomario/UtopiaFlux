package flux_sound;

import arc_bank.AbstractBank;

/**
 * Soundbank is a bank that contains any types of sounds and can also provide 
 * any kinds of sound types.
 *
 * @author Mikko Hilpinen.
 *         Created 22.8.2013.
 * @see flux_sound.Sound
 */
public abstract class SoundBank extends AbstractBank
{
	// IMPLEMENTED METHODS	---------------------------------------------
	
	@Override
	protected Class<?> getSupportedClass()
	{
		// Soundbanks accept sound objects
		return Sound.class;
	}
	
	
	// OTHER METHODS	-------------------------------------------------
	
	/**
	 * Returns a sound from the bank
	 *
	 * @param soundname The name of the sound in the bank
	 * @return the sound from the bank (or null if no sound with such name 
	 * exists in the bank)
	 */
	public Sound getSound(String soundname)
	{
		return (Sound) getObject(soundname);
	}
	
	/**
	 * Stops all the sounds from the bank from playing
	 */
	public void stopAllSounds()
	{
		for (String name : getContentNames())
		{
			getSound(name).stop();
		}
	}
}
