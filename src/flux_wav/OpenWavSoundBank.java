package flux_wav;

import java.util.ArrayList;

import flux_sound.SoundResource;
import arc_bank.MultiMediaHolder;
import arc_bank.OpenBank;

/**
 * OpenWavSoundBank initializes the sounds using a list of commands in string 
 * format. The bank then provides these sounds for the other classes to use
 *
 * @author Mikko Hilpinen.
 * @since 7.9.2013.
 */
public class OpenWavSoundBank extends WavSoundBank implements OpenBank
{
	// ATTRIBUTES	-----------------------------------------------------
	
	private ArrayList<String> creationcommands;
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new OpenWavSoundBank that will be initialized using the 
	 * given commands. Each command instance creates a single wavsound to the 
	 * bank
	 *
	 * @param creationcommands The creationcommands contain the necessary 
	 * information for creating a wavsound. The commands should have the 
	 * following syntax:<br>
	 * <i>soundname#filename (data/ automatically included)
	 * #volumeadjustment#panning</i><br>
	 * Command can be for example such line as "car#sounds/car.wav#3.0#-0.4"
	 */
	public OpenWavSoundBank(ArrayList<String> creationcommands)
	{
		// Initializes attributes
		this.creationcommands = creationcommands;
	}
	
	
	// IMPLEMENTED METHODS	---------------------------------------------
	
	@Override
	protected void initialize()
	{
		// Creates the wavsounds using the commands
		// Goes through all the commands
		for (int i = 0; i < this.creationcommands.size(); i++)
		{
			String commandline = this.creationcommands.get(i);
			String[] commands = commandline.split("#");
			
			// Check that there's enough information
			if (commands.length < 4)
			{
				System.err.println("Command " + commandline + " doesn't " +
						"have enough arguments for creating a wavsound!");
				continue;
			}
			
			// Tries to change the volume and pan from strings to floats
			float volume = 0;
			float pan = 0;
			
			try
			{
				volume = Float.parseFloat(commands[2]);
				pan = Float.parseFloat(commands[3]);
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("Command " + commandline + " contains " +
						"invalid information! Volume and pan need to be in " +
						"float format.");
				nfe.printStackTrace();
			}
			
			// Creates the new wavsound and adds it to the bank
			createSound(commands[1], commands[0], volume, pan);
		}
	}
	
	/**
	 * Returns an WavSoundBank if it has been initialized
	 *
	 * @param bankname The name of the needed bank
	 * @return The WavSoundBank with the given name or null if no such bank exists 
	 * or if the bank is not active
	 */
	public static WavSoundBank getWavSoundBank(String bankname)
	{
		OpenBank maybewavbank = MultiMediaHolder.getBank(SoundResource.WAV, bankname);
		
		if (maybewavbank instanceof WavSoundBank)
			return (WavSoundBank) maybewavbank;
		else
			return null;
	}
	
	/**
	 * Returns a wavSound from any wavSoundBank that is currently active
	 * 
	 * @param bankName The name of the bank that holds the sound
	 * @param soundName The name of the sound in the bank
	 * @return A wavSound with the given name in the given bank
	 */
	public static WavSound getWavSound(String bankName, String soundName)
	{
		WavSoundBank bank = getWavSoundBank(bankName);
		
		if (bank == null)
			return null;
		
		return bank.getSound(soundName);
	}
}
