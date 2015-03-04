package flux_midi;

import flux_sound.SoundResourceType;
import arc_bank.Bank;
import arc_bank.BankBank;
import arc_bank.BankBankInitializer;
import arc_bank.BankObjectConstructor;
import arc_bank.MultiMediaHolder;
import arc_bank.ResourceInitializationException;

/**
 * MidiSoundBank is a static collection of midiSounds and midiSound banks
 * @author Mikko Hilpinen
 * @since 4.3.2015
 */
public class MidiSoundBank
{
	// CONSTRUCTOR	-----------------------
	
	private MidiSoundBank()
	{
		// The interface is static
	}

	
	// OTHER METHODS	--------------------
	
	/**
	 * Initializes the midiSound resources. This should be called before the gamePhases have 
	 * been initialized.
	 * @param fileName The name of the file that contains midiSound data ("data/" automatically 
	 * included). The file should have the following format:<br>
	 * &bankName1<br>
	 * soundName1#fileName<br>
	 * soundName2#...<br>
	 * ...<br>
	 * &bankName2<br>
	 * ...<br>
	 */
	public static void initializeMidiSoundResources(String fileName)
	{
		MultiMediaHolder.initializeResourceDatabase(createMidiSoundBankBank(fileName));
	}
	
	/**
	 * Creates a new bank system that handles all the midiSounds introduced in the given file
	 * @param fileName The name of the file that contains midiSound data ("data/" automatically 
	 * included). The file should have the following format:<br>
	 * &bankName1<br>
	 * soundName1#fileName<br>
	 * soundName2#...<br>
	 * ...<br>
	 * &bankName2<br>
	 * ...<br>
	 * 
	 * @return A new Bank system containing all the introduced banks
	 */
	public static BankBank<MidiSound> createMidiSoundBankBank(String fileName)
	{
		return new BankBank<>(new BankBankInitializer<>(fileName, 
				new MidiSoundBankConstructor(), new MidiSoundConstructor()), 
				SoundResourceType.MIDI);
	}
	
	/**
	 * Finds and returns a midiBank with the given name. The bank must be active in order 
	 * for this to work.
	 * @param bankName The name of the midiBank
	 * @return A midiBank with the given name
	 */
	@SuppressWarnings("unchecked")
	public static Bank<MidiSound> getMidiSoundBank(String bankName)
	{
		return (Bank<MidiSound>) MultiMediaHolder.getBank(SoundResourceType.MIDI, bankName);
	}
	
	/**
	 * Finds and returns a midiSound from the given midiBank. The bank must be active.
	 * @param bankName The name of the bank that contains the sound
	 * @param soundName The name of the sound in the bank
	 * @return A sound with the given name from the given bank
	 */
	public static MidiSound getSound(String bankName, String soundName)
	{
		return getMidiSoundBank(bankName).get(soundName);
	}
	
	
	// SUBCLASSES	------------------------
	
	private static class MidiSoundBankConstructor implements BankObjectConstructor<Bank<MidiSound>>
	{
		@Override
		public Bank<MidiSound> construct(String line, Bank<Bank<MidiSound>> bank)
		{
			// The line contains the name of the bank
			Bank<MidiSound> newBank = new Bank<>();
			bank.put(line, newBank);
			return newBank;
		}
	}
	
	private static class MidiSoundConstructor implements BankObjectConstructor<MidiSound>
	{
		@Override
		public MidiSound construct(String line, Bank<MidiSound> bank)
		{
			// The line has the following format: soundName#fileName
			String[] arguments = line.split("#");
			
			if (arguments.length < 2)
				throw new ResourceInitializationException("Can't construct a midiSound from " + 
						line);
			
			MidiSound newSound = new MidiSound(arguments[1], arguments[0]);
			bank.put(arguments[0], newSound);
			
			return newSound;
		}	
	}
}
