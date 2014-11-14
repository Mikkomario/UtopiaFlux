package flux_midi;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import flux_sound.SoundResource;
import arc_bank.MultiMediaHolder;
import arc_bank.OpenBank;

/**
 * OpenmidiSoundBank holds a number of midis that are initialized using a 
 * set of commands.
 * 
 * @author Mikko Hilpinen. 
 * @since 10.2.2014
 */
public class OpenMidiSoundBank extends MidiSoundBank implements OpenBank
{
	// ATTRIBUTES	------------------------------------------------------
	
	private ArrayList<String> commands;
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new midiSoundBank by providing it a set of commands used to 
	 * initialize the bank.
	 * 
	 * @param creationcommands The creation commands that show which midis 
	 * should be loaded and from where.<br>
	 * Each of the commands should have the following syntax:<br>
	 * MusicName#filename (data/ automatically included)
	 */
	public OpenMidiSoundBank(ArrayList<String> creationcommands)
	{
		// Initializes attributes
		this.commands = creationcommands;
	}

	
	// IMPLEMENTED METHODS	---------------------------------------------
	
	@Override
	public void createMidis() throws FileNotFoundException
	{
		// TODO Consider finding a way to avoid copy-paste between the different 
		// openBanks

		// Goes through all the commands
		for (int i = 0; i < this.commands.size(); i++)
		{
			String commandline = this.commands.get(i);
			String[] commands = commandline.split("#");
			
			// Check that there's enough information
			if (commands.length < 2)
			{
				System.err.println("Command " + commandline + " doesn't " +
						"have enough arguments for creating a midiSound!");
				continue;
			}
			
			// Creates the new wavsound and adds it to the bank
			createmidiSound(commands[1], commands[0]);
		}
	}
	
	
	// OTHER METHODS	---------------------------------------------
	
	/**
	 * Returns an MidiSoundBank if it has been initialized
	 *
	 * @param bankname The name of the needed bank
	 * @return The MidiSoundBank with the given name or null if no such bank exists 
	 * or if the bank is not active
	 */
	public static MidiSoundBank getMidiSoundBank(String bankname)
	{
		OpenBank maybeMidibank = MultiMediaHolder.getBank(SoundResource.MIDI, bankname);
		
		if (maybeMidibank instanceof MidiSoundBank)
			return (MidiSoundBank) maybeMidibank;
		else
			return null;
	}
	
	/**
	 * Returns a MidiSound from any MidiSoundBank that is currently active
	 * 
	 * @param bankName The name of the bank that holds the sound
	 * @param soundName The name of the sound in the bank
	 * @return A MidiSound with the given name in the given bank
	 */
	public static MidiSound getMidiSound(String bankName, String soundName)
	{
		MidiSoundBank bank = getMidiSoundBank(bankName);
		
		if (bank == null)
			return null;
		
		return bank.getSound(soundName);
	}
}
