package flux_test;

import arc_bank.GamePhaseBank;
import arc_resource.ResourceActivator;
import flux_wav.WavSoundBank;
import genesis_event.AdvancedMouseEvent;
import genesis_event.AdvancedMouseEvent.MouseButton;
import genesis_event.AdvancedMouseEvent.MouseButtonEventType;
import genesis_event.AdvancedMouseListener;
import genesis_event.EventSelector;
import genesis_event.HandlerRelay;
import genesis_event.StrictEventSelector;
import genesis_util.LatchStateOperator;
import genesis_util.StateOperator;
import genesis_util.Vector2D;
import genesis_video.GamePanel;
import genesis_video.GameWindow;

/**
 * This class tests the use of wavSounds
 * 
 * @author Mikko Hilpinen
 * @since 8.3.2015
 */
public class WavSoundTest
{
	// CONSTRUCTOR	-------------------------
	
	private WavSoundTest()
	{
		// The interface is static
	}

	
	// MAIN METHOD	-------------------------
	
	/**
	 * Starts the test
	 * @param args Not used
	 */
	public static void main(String[] args)
	{
		// Initializes the resources
		WavSoundBank.initializeWavSoundResources("sounds.txt");
		GamePhaseBank.initializeGamePhaseResources("phases.txt", "default");
		ResourceActivator.startPhase(GamePhaseBank.getGamePhase("test"), true);
		
		// Sets up the window
		Vector2D screenDimensions = new Vector2D(500, 400);
		GameWindow window = new GameWindow(screenDimensions, "Flux wav test", true, 
				120, 20);
		GamePanel panel = window.getMainPanel().addGamePanel();
		HandlerRelay handlers = HandlerRelay.createDefaultHandlerRelay(window, panel);
		
		// Creates the test objects
		new TestMusicPlayer(screenDimensions, handlers);
	}
	
	
	// SUBCLASSES	------------------------
	
	private static class TestMusicPlayer implements AdvancedMouseListener
	{
		// ATTRIBUTES	--------------------
		
		private StateOperator isDeadOperator, listensMouseOperator;
		private EventSelector<AdvancedMouseEvent> selector;
		private Vector2D screenDimensions;
		
		
		// CONSTRUCTOR	--------------------
		
		/**
		 * Creates a new music player
		 * @param screenDimensions The size of the screen
		 * @param handlers The handlers that will handle this player
		 */
		public TestMusicPlayer(Vector2D screenDimensions, HandlerRelay handlers)
		{
			this.isDeadOperator = new LatchStateOperator(false);
			this.listensMouseOperator = new StateOperator(true, true);
			this.screenDimensions = screenDimensions;
			
			StrictEventSelector<AdvancedMouseEvent, AdvancedMouseEvent.Feature> s = 
					new StrictEventSelector<>();
			s.addRequiredFeature(MouseButton.LEFT);
			s.addRequiredFeature(MouseButtonEventType.PRESSED);
			this.selector = s;
			
			handlers.addHandled(this);
		}
		
		
		// IMPLEMENTED METHODS	--------------
		
		@Override
		public StateOperator getIsDeadStateOperator()
		{
			return this.isDeadOperator;
		}

		@Override
		public StateOperator getListensToMouseEventsOperator()
		{
			return this.listensMouseOperator;
		}

		@Override
		public EventSelector<AdvancedMouseEvent> getMouseEventSelector()
		{
			return this.selector;
		}

		@Override
		public boolean isInAreaOfInterest(Vector2D position)
		{
			return false;
		}

		@Override
		public void onMouseEvent(AdvancedMouseEvent event)
		{
			// Plays a wavSound with specific settings
			Vector2D relativeCoordinates = 
					event.getPosition().dividedBy(this.screenDimensions);
			
			float volume = (float) (relativeCoordinates.getSecond() - 0.5) * -60;
			float pan = (float) relativeCoordinates.getFirst() * 2 - 1;
			
			System.out.println("Playing a sound with volume " + volume + " and pan " + pan);
			
			WavSoundBank.getSound("test", "test").play(volume, pan, null);
		}
	}
}
