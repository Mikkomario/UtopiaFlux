package flux_sound;

import java.util.ArrayList;
import java.util.List;

import genesis_event.Event;
import genesis_event.EventSelector;
import genesis_event.StrictEventSelector;

/**
 * SoundEvents are created when a sound starts, ends or loops
 * 
 * @author Mikko Hilpinen
 * @since 4.3.2015
 */
public class SoundEvent implements Event
{
	// ATTRIBUTES	-----------------------------
	
	private Sound source;
	private SoundEventType type;
	
	
	// CONSTRUCTOR	-----------------------------
	
	/**
	 * Creates a new sound event
	 * @param source The sound that originated the event
	 * @param type The type of action that originated the event
	 */
	public SoundEvent(Sound source, SoundEventType type)
	{
		this.source = source;
		this.type = type;
	}
	
	
	// IMPLEMENTED METHODS	---------------------

	@Override
	public List<Event.Feature> getFeatures()
	{
		List<Event.Feature> features = new ArrayList<>();
		
		features.add(this.type);
		
		return features;
	}
	
	
	// GETTERS & SETTERS	---------------------
	
	/**
	 * @return The sound that originated the event
	 */
	public Sound getSource()
	{
		return this.source;
	}
	
	/**
	 * @return The type of action that originated the event
	 */
	public SoundEventType getType()
	{
		return this.type;
	}
	
	
	// OTHER METHODS	-------------------------
	
	/**
	 * Creates a new eventSelector that only accepts certain types of events
	 * @param type The type of event accepted by the selector
	 * @return A new eventSelector
	 */
	public static EventSelector<SoundEvent> createEventTypeSelector(SoundEventType type)
	{
		StrictEventSelector<SoundEvent, Feature> selector = new StrictEventSelector<>();
		selector.addRequiredFeature(type);
		return selector;
	}

	
	// INTERFACES	-----------------------------
	
	/**
	 * A feature that describes a soundEvent
	 * 
	 * @author Mikko Hilpinen
	 * @since 4.3.2015
	 */
	public interface Feature extends Event.Feature
	{
		// Used as a wrapper
	}
	
	
	// ENUMERATIONS	-----------------------------
	
	/**
	 * The event's type describes how it was originated
	 * @author Mikko Hilpinen
	 * @since 4.3.2015
	 */
	public enum SoundEventType implements Feature
	{
		/**
		 * This event originated when a sound started
		 */
		START,
		/**
		 * This event originated when a sound ended
		 */
		END;
	}
}
