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
	MIDI;
}
