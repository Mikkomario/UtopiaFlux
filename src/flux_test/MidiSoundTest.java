package flux_test;

import arc_bank.GamePhaseBank;
import arc_resource.ResourceActivator;
import flux_midi.MidiSound;
import flux_midi.MidiSoundBank;
import genesis_event.EventSelector;
import genesis_event.HandlerRelay;
import genesis_event.KeyEvent;
import genesis_event.KeyEvent.KeyEventType;
import genesis_event.KeyListener;
import genesis_util.LatchStateOperator;
import genesis_util.StateOperator;
import genesis_util.Vector3D;
import genesis_video.GamePanel;
import genesis_video.GameWindow;

/**
 * This class tests the basic functions in MidiSound class
 * @author Mikko Hilpinen
 * @since 27.4.2015
 */
public class MidiSoundTest
{
	// CONSTRUCTOR	--------------------------------
	
	private MidiSoundTest()
	{
		// The interface is static
	}

	
	// MAIN METHOD	--------------------------------
	
	/**
	 * Starts the test
	 * @param args Not used
	 */
	public static void main(String[] args)
	{
		// Initializes the resources
		MidiSoundBank.initializeMidiSoundResources("music.txt");
		GamePhaseBank.initializeGamePhaseResources("phases.txt", "default");
		ResourceActivator.startPhase(GamePhaseBank.getGamePhase("test"), true);
		
		// Sets up the window
		Vector3D screenDimensions = new Vector3D(500, 400);
		GameWindow window = new GameWindow(screenDimensions, "Flux midi test", true, 
				120, 20);
		GamePanel panel = window.getMainPanel().addGamePanel();
		HandlerRelay handlers = HandlerRelay.createDefaultHandlerRelay(window, panel);
		
		// Creates the test objects
		new TestMidiPlayer(handlers);
	}
	
	
	// SUBCLASSES	--------------------------------
	
	private static class TestMidiPlayer implements KeyListener
	{
		// ATTRIBUTES	----------------------------
		
		private StateOperator isDeadOperator, listensOperator;
		private EventSelector<KeyEvent> selector;
		private MidiSound testMusic;
		
		
		// CONSTRUCTOR	----------------------------
		
		public TestMidiPlayer(HandlerRelay handlers)
		{
			this.isDeadOperator = new LatchStateOperator(false);
			this.listensOperator = new StateOperator(true, true);
			this.selector = KeyEvent.createEventTypeSelector(KeyEventType.PRESSED);
			
			this.testMusic = MidiSoundBank.getSound("test", "test");
			
			handlers.addHandled(this);
		}
		
		
		// IMPLEMENTED METHODS	--------------------
		
		@Override
		public StateOperator getIsDeadStateOperator()
		{
			return this.isDeadOperator;
		}

		@Override
		public EventSelector<KeyEvent> getKeyEventSelector()
		{
			return this.selector;
		}

		@Override
		public StateOperator getListensToKeyEventsOperator()
		{
			return this.listensOperator;
		}

		@Override
		public void onKeyEvent(KeyEvent event)
		{
			switch (event.getKey())
			{
				// On Right, increases the tempo
				case KeyEvent.RIGTH:
					this.testMusic.setTempoFactor(this.testMusic.getTempoFactor() + 0.1);
					break;
				// On Left, decreases the tempo
				case KeyEvent.LEFT:
					this.testMusic.setTempoFactor(this.testMusic.getTempoFactor() - 0.1);
					break;
				// On Up, increases gain
				case KeyEvent.UP: this.testMusic.setGain(1); break;
				// On Down, decreases gain
				case KeyEvent.DOWN: this.testMusic.setGain(0.1); break;
				default:
					switch (event.getKeyChar())
					{
						// On P, plays the sound once
						case 'p': this.testMusic.play(null); break;
						// On L, loops the sound
						case 'l': this.testMusic.loop(null); break;
						// On S, stops the sound
						case 's': this.testMusic.stop(); break;
						// On Space, pauses / unpauses the sound
						case ' ':
							if (this.testMusic.isPaused())
								this.testMusic.unpause();
							else
								this.testMusic.pause();
							break;
					}
			}
			
		}
	}
}
