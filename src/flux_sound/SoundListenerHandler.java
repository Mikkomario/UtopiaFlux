package flux_sound;

import genesis_event.EventSelector;
import genesis_event.Handler;
import genesis_event.HandlerRelay;
import genesis_event.HandlerType;
import genesis_event.StrictEventSelector;
import genesis_util.StateOperator;

/**
 * Soundlistenerhandler informs multiple listeners about sound events of sounds 
 * it listens to
 *
 * @author Mikko Hilpinen.
 * @since 19.8.2013.
 */
public class SoundListenerHandler extends Handler<SoundListener> implements SoundListener
{
	// ATTRIBUTES	-----------------------------------------------------
	
	private SoundEvent lastevent;
	private StateOperator listensOperator;
	private EventSelector<SoundEvent> eventSelector;
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new soundlistenerhandler and adds it to the given handlers (if 
	 * possible)
	 *
	 * @param autodeath Will the handler automatically die when it runs out 
	 * of handleds
	 * @param superHandlers The handlers that will handle the handler (optional)
	 */
	public SoundListenerHandler(boolean autodeath, HandlerRelay superHandlers)
	{
		super(autodeath, superHandlers);
		
		this.lastevent = null;
		this.listensOperator = new AnyListensToSoundsOperator();
		this.eventSelector = new StrictEventSelector<>();
	}
	
	/**
	 * Creates a new soundListenerHandler. The handler won't be automatically informed about 
	 * sound events.
	 * @param autoDeath Will the handler automatically die once it runs out of handleds
	 */
	public SoundListenerHandler(boolean autoDeath)
	{
		super(autoDeath);
		
		this.lastevent = null;
		this.listensOperator = new AnyListensToSoundsOperator();
		this.eventSelector = new StrictEventSelector<>();
	}
	
	
	// IMPLEMENTED METHODS	---------------------------------------------
	
	@Override
	protected boolean handleObject(SoundListener l)
	{
		// If the listener is not willing to receive the event, skips it
		if (!l.getListensToSoundEventsOperator().getState() || 
				!l.getSoundEventSelector().selects(this.lastevent))
			return true;
		
		l.onSoundEvent(this.lastevent);
		
		return true;
	}
	
	@Override
	public HandlerType getHandlerType()
	{
		return FluxHandlerType.SOUNDLISTENERHANDLER;
	}
	
	@Override
	public void onSoundEvent(flux_sound.SoundEvent e)
	{
		// Updates status
		this.lastevent = e;
		// Informs listeners
		handleObjects();
		// Forgets the status
		this.lastevent = null;
	}

	@Override
	public StateOperator getListensToSoundEventsOperator()
	{
		return this.listensOperator;
	}

	@Override
	public EventSelector<flux_sound.SoundEvent> getSoundEventSelector()
	{
		return this.eventSelector;
	}
	
	
	// SUBCLASSES	-----------------------------
	
	private class AnyListensToSoundsOperator extends ForAnyHandledsOperator
	{
		// CONSTRUCTOR	-------------------------
		
		public AnyListensToSoundsOperator()
		{
			super(true);
		}
		
		
		// IMPLEMENTED METHODS	-----------------

		@Override
		protected StateOperator getHandledStateOperator(SoundListener l)
		{
			return l.getListensToSoundEventsOperator();
		}
	}
}
