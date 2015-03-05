package flux_sound;

import flux_midi.MidiSound;
import flux_wav.WavSound;
import arc_resource.ResourceType;

/**
 * These are the different types of sound resources introduced in this module
 * 
 * @author Mikko Hilpinen
 * @since 4.3.2015
 */
public enum SoundResourceType implements ResourceType
{
	/**
	 * Wav sounds are in wave audio format
	 * @see WavSound
	 */
	WAV,
	/**
	 * Midi sounds are in midi format
	 * @see MidiSound
	 */
	MIDI,
	/**
	 * SoundTracks are formed of multiple sounds
	 * @see SoundTrack
	 */
	SOUNDTRACK;
	
	
	// OTHER METHODS	---------------------------
	
	/**
	 * Finds a soundResource the given string represents
	 * @param s A string that represents a soundResource
	 * @return The soundResourceType represented by the string or null if no such type can be 
	 * found
	 */
	public static SoundResourceType parseFromString(String s)
	{
		for (SoundResourceType type : SoundResourceType.values())
		{
			if (type.toString().equalsIgnoreCase(s))
				return type;
		}
		
		return null;
	}
}
