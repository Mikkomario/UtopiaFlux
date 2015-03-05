package flux_wav;

import arc_bank.Bank;
import arc_bank.BankBank;
import arc_bank.BankBankInitializer;
import arc_bank.BankObjectConstructor;
import arc_bank.MultiMediaHolder;
import arc_bank.ResourceInitializationException;
import flux_wav.WavSound;
import flux_sound.SoundResourceType;

/**
 * This is a static collection of wavSounds and wavSoundBanks
 * @author Mikko Hilpinen
 * @since 5.3.2015
 */
public class WavSoundBank
{
	// CONSTRUCTOR	-------------------------
	
	private WavSoundBank()
	{
		// The interface is static
	}

	
	// OTHER METHODS	--------------------
	
	/**
	 * Initializes the wavSound resources. This should be called before the gamePhases have 
	 * been initialized.
	 * @param fileName The name of the file that contains wavSound data ("data/" automatically 
	 * included). The file should have the following format:<br>
	 * &bankName1<br>
	 * soundName1#fileName#volumeAdjustment (optional, default = 0)#
	 * panAdjustment (optional, default = 0)<br>
	 * soundName2#...<br>
	 * ...<br>
	 * &bankName2<br>
	 * ...<br>
	 */
	public static void initializeWavSoundResources(String fileName)
	{
		MultiMediaHolder.initializeResourceDatabase(createWavSoundBankBank(fileName));
	}
	
	/**
	 * Creates a new bank system that handles all the wavSounds introduced in the given file
	 * @param fileName The name of the file that contains wavSound data ("data/" automatically 
	 * included). The file should have the following format:<br>
	 * &bankName1<br>
	 * soundName1#fileName#volumeAdjustment (optional, default = 0)#
	 * panAdjustment (optional, default = 0)<br>
	 * soundName2#...<br>
	 * ...<br>
	 * &bankName2<br>
	 * ...<br>
	 * 
	 * @return A new Bank system containing all the introduced banks
	 */
	public static BankBank<WavSound> createWavSoundBankBank(String fileName)
	{
		return new BankBank<>(new BankBankInitializer<>(fileName, 
				new WavSoundBankConstructor(), new WavSoundConstructor()), 
				SoundResourceType.WAV);
	}
	
	/**
	 * Finds and returns a wavBank with the given name. The bank must be active in order 
	 * for this to work.
	 * @param bankName The name of the wavBank
	 * @return A wavBank with the given name
	 */
	@SuppressWarnings("unchecked")
	public static Bank<WavSound> getWavSoundBank(String bankName)
	{
		return (Bank<WavSound>) MultiMediaHolder.getBank(SoundResourceType.WAV, bankName);
	}
	
	/**
	 * Finds and returns a wavSound from the given wavBank. The bank must be active.
	 * @param bankName The name of the bank that contains the sound
	 * @param soundName The name of the sound in the bank
	 * @return A sound with the given name from the given bank
	 */
	public static WavSound getSound(String bankName, String soundName)
	{
		return getWavSoundBank(bankName).get(soundName);
	}
	
	
	// SUBCLASSES	------------------------
	
	private static class WavSoundBankConstructor implements BankObjectConstructor<Bank<WavSound>>
	{
		@Override
		public Bank<WavSound> construct(String line, Bank<Bank<WavSound>> bank)
		{
			// The line contains the name of the bank
			Bank<WavSound> newBank = new Bank<>();
			bank.put(line, newBank);
			return newBank;
		}
	}
	
	private static class WavSoundConstructor implements BankObjectConstructor<WavSound>
	{
		@Override
		public WavSound construct(String line, Bank<WavSound> bank)
		{
			// The line has the following format: soundName#fileName#
			// volumeAdjustment (optional)#pan (optional)
			String[] arguments = line.split("#");
			
			if (arguments.length < 2)
				throw new ResourceInitializationException("Can't construct a wavSound from " + 
						line);
			
			float volume = 0, pan = 0;
			if (arguments.length > 2)
			{
				try
				{
					volume = Float.parseFloat(arguments[2]);
					if (arguments.length > 3)
						pan = Float.parseFloat(arguments[3]);
				}
				catch (NumberFormatException e)
				{
					throw new ResourceInitializationException("Can't parse line " + line);
				}
			}
			
			WavSound newSound = new WavSound(arguments[1], arguments[0], volume, pan);
			bank.put(arguments[0], newSound);
			
			return newSound;
		}	
	}
}
