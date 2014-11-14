package flux_sound;

import arc_resource.Resource;

/**
 * SoundResources are resources that are used for playing sounds and music
 * 
 * @author Mikko Hilpinen
 * @since 16.9.2014
 */
public enum SoundResource implements Resource
{
	/**
	 * Wavs are sounds that are read from a wav file
	 */
	WAV,
	/**
	 * Midis are sounds that are read from a midi file
	 */
	MIDI,
	/**
	 * Wavtrack is a combination of wav sounds
	 */
	WAVTRACK,
	/**
	 * Miditrack is a combination of midi sounds
	 */
	MIDITRACK;
}
