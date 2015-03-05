package flux_sound;

import flux_midi.MidiSoundBank;
import flux_wav.WavSoundBank;
import arc_bank.Bank;
import arc_bank.BankBank;
import arc_bank.BankBankInitializer;
import arc_bank.BankObjectConstructor;
import arc_bank.MultiMediaHolder;
import arc_bank.ResourceInitializationException;

/**
 * SoundTrackBank is a static collection of soundTracks and soundTrackBanks
 * @author Mikko Hilpinen
 * @since 5.3.2015
 */
public class SoundTrackBank
{
	// CONSTRUCTOR	-------------------------
	
	private SoundTrackBank()
	{
		// The interface is static
	}
	

	// OTHER METHODS	--------------------
	
	/**
	 * Initializes the soundTrack resources. This should be called before the gamePhases have 
	 * been initialized.
	 * @param fileName The name of the file that contains soundTrack data ("data/" automatically 
	 * included). The file should have the following format:<br>
	 * &bankName1<br>
	 * trackName1#soundResourceType#soundBankName#soundName1,soundName2,...#
	 * loopCount1,loopCount2,... (optional, all loopcounts are 0 by default)<br>
	 * trackName2#...<br>
	 * ...<br>
	 * &bankName2<br>
	 * ...<br>
	 */
	public static void initializeSoundTrackResources(String fileName)
	{
		MultiMediaHolder.initializeResourceDatabase(createSoundTrackBankBank(fileName));
	}
	
	/**
	 * Creates a new bank system that handles all the soundTracks introduced in the given file
	 * @param fileName The name of the file that contains soundTrack data ("data/" automatically 
	 * included). The file should have the following format:<br>
	 * &bankName1<br>
	 * trackName1#soundResourceType#soundBankName#soundName1,soundName2,...#
	 * loopCount1,loopCount2,... (optional, all loopcounts are 0 by default)<br>
	 * trackName2#...<br>
	 * ...<br>
	 * &bankName2<br>
	 * ...<br>
	 * 
	 * @return A new Bank system containing all the introduced banks
	 */
	public static BankBank<SoundTrack> createSoundTrackBankBank(String fileName)
	{
		return new BankBank<>(new BankBankInitializer<>(fileName, 
				new SoundTrackBankConstructor(), new SoundTrackConstructor()), 
				SoundResourceType.SOUNDTRACK);
	}
	
	/**
	 * Finds and returns a soundTrackBank with the given name. The bank must be active in order 
	 * for this to work.
	 * @param bankName The name of the bank
	 * @return A soundTrackBank with the given name
	 */
	@SuppressWarnings("unchecked")
	public static Bank<SoundTrack> getSoundTrackBank(String bankName)
	{
		return (Bank<SoundTrack>) MultiMediaHolder.getBank(SoundResourceType.SOUNDTRACK, 
				bankName);
	}
	
	/**
	 * Finds and returns a soundTrack from the given wavBank. The bank must be active.
	 * @param bankName The name of the bank that contains the track
	 * @param soundName The name of the track in the bank
	 * @return A track with the given name from the given bank
	 */
	public static SoundTrack getSound(String bankName, String soundName)
	{
		return getSoundTrackBank(bankName).get(soundName);
	}
	
	
	// SUBCLASSES	------------------------
	
	private static class SoundTrackBankConstructor implements BankObjectConstructor<Bank<SoundTrack>>
	{
		@Override
		public Bank<SoundTrack> construct(String line, Bank<Bank<SoundTrack>> bank)
		{
			// The line contains the name of the bank
			Bank<SoundTrack> newBank = new Bank<>();
			bank.put(line, newBank);
			return newBank;
		}
	}
	
	private static class SoundTrackConstructor implements BankObjectConstructor<SoundTrack>
	{
		@Override
		public SoundTrack construct(String line, Bank<SoundTrack> bank)
		{
			// The line has the following format: trackName#resourceType#soundBankName#
			// soundName1,soundName2,...#
			// loopCount1,loopCount2,... (optional, by default every loopCount is 0)
			String[] arguments = line.split("#");
			
			if (arguments.length < 4)
				throw new ResourceInitializationException("Can't construct a soundTrack from " + 
						line);
			
			SoundResourceType type = SoundResourceType.parseFromString(arguments[1]);
			if (type == null)
				throw new ResourceInitializationException(
						"Can't parse a soundResourceType from " + arguments[1]);
			
			Bank<? extends Sound> soundBank = null;
			switch (type)
			{
				case WAV: soundBank = WavSoundBank.getWavSoundBank(arguments[2]); break;
				case MIDI: soundBank = MidiSoundBank.getMidiSoundBank(arguments[2]); break;
				case SOUNDTRACK: soundBank = SoundTrackBank.getSoundTrackBank(arguments[2]); 
						break;
			}
			
			String[] soundNames = arguments[3].split(",");
			int[] loopCounts = new int[soundNames.length];
			
			if (arguments.length < 5)
			{
				for (int i = 0; i < loopCounts.length; i++)
				{
					loopCounts[i] = 0;
				}
			}
			else
			{
				try
				{
					String[] loopCountStrings = arguments[4].split(",");
					for (int i = 0; i < loopCounts.length; i++)
					{
						if (loopCountStrings.length > i)
							loopCounts[i] = Integer.parseInt(loopCountStrings[i]);
						else
							loopCounts[i] = 0;
					}
				}
				catch (NumberFormatException e)
				{
					throw new ResourceInitializationException(
							"Can't parse a soundTrack from line " + line);
				}
			}
			
			SoundTrack newTrack = new SoundTrack(soundNames, loopCounts, soundBank, 
					arguments[0]);
			bank.put(arguments[0], newTrack);
			
			return newTrack;
		}	
	}
}
