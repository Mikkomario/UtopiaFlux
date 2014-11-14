package flux_midi;

import java.util.ArrayList;

import arc_bank.OpenBank;
import arc_resource.Resource;
import flux_sound.OpenSoundBankHolder;
import flux_sound.SoundResource;

/**
 * This holder holds multiple midiSoundBanks and provides them for objects that 
 * need them. The banks are initialized using a specific file.
 * 
 * @author Mikko Hilpinen. 
 * @since 10.2.2014
 */
public class OpenMidiSoundBankHolder extends OpenSoundBankHolder
{
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates and initializes new OpenmidiSoundBankHolder. The content is loaded 
	 * using the given file.
	 *
	 * @param filename A file that shows information about what banks to create 
	 * (data/ automatically included). 
	 * The file should be written as follows:<p>
	 * 
	 * &bankname<br>
	 * midiname#filename(data/ automatically included)<br>
	 * anothermidiname#anotherfilename<br>
	 * ...<br>
	 * &anotherbankname<br>
	 * ...<br>
	 * * this is a comment
	 */
	public OpenMidiSoundBankHolder(String filename)
	{
		super(filename, true);
	}
	
	
	// IMPLEMENTED METHODS	---------------------------------------------

	@Override
	protected OpenBank createBank(ArrayList<String> commands)
	{
		return new OpenMidiSoundBank(commands);
	}
	
	@Override
	public Resource getHeldResourceType()
	{
		return SoundResource.MIDI;
	}
	
	
	// OTHER METHODS	-------------------------------------------------
	
	/**
	 * Looks for a OpenmidiSound matching the given name and if it is found,
	 * returns it. If not found, returns null.
	 * 
	 * @param bankname	The name of the required bank
	 * @return The bank with the given name or null if no such bank exists
	 */
	public OpenMidiSoundBank getmidiSoundBank(String bankname)
	{
		OpenBank maybeOpenMidiBank = getBank(bankname);
		
		if(maybeOpenMidiBank instanceof OpenMidiSoundBank)
			return (OpenMidiSoundBank) maybeOpenMidiBank;
		else
			return null;
	}
}
