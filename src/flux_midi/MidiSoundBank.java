package flux_midi;

import java.io.FileNotFoundException;

import flux_sound.SoundBank;

/**
 * midiSoundbank contains a group of midiSounds and provides them for the 
 * objects that need them
 * 
 * @author Unto Solala & Mikko Hilpinen
 * @since 10.7.2013
 */
public abstract class MidiSoundBank extends SoundBank
{
	// ABSTRACT METHODS -----------------------------------------------------
	
	/**
	 * Creates Midis with the createMidi()-method.
	 * 
	 * @throws FileNotFoundException if all of the midis couldn't be loaded.
	 */
	public abstract void createMidis() throws FileNotFoundException;

	
	// IMPLEMENTED METHODS ---------------------------------------------------

	@Override
	protected void initialize()
	{
		// Creates the midis
		try
		{
			createMidis();
		}
		catch (FileNotFoundException fnfe)
		{
			// TODO: For some reason this doesn't seem to catch all the 
			// filenotfound exceptions
			
			System.err.println("Could not load all of the Midis!");
			fnfe.printStackTrace();
		}
	}
	
	@Override
	protected Class<?> getSupportedClass()
	{
		return MidiSound.class;
	}
	
	
	// OTHER METHODS	--------------------------------------------------

	/**
	 * Creates a midi and stores it in the bank
	 * 
	 * @param fileName	File's name and location (data/ is added by default)
	 * @param midiName	Name of the new midi in the bank.
	 */
	protected void createmidiSound(String fileName, String midiName)
	{
		MidiSound newMidi = new MidiSound(fileName, midiName);
		addObject(newMidi, midiName);
	}

	/**
	 * Returns a midi from the midiSoundBank.
	 * 
	 * @param midiName	Name of the wanted midi
	 * @return Returns the wanted midi if it is in the database, otherwise 
	 * returns null.
	 */
	@Override
	public MidiSound getSound(String midiName)
	{
		return (MidiSound) getObject(midiName);
	}
}
