package flux_sound;

import genesis_event.HandlerType;

/**
 * These are the handlerTypes introduced in the flux module
 * 
 * @author Mikko Hilpinen
 * @since 4.3.2015
 */
public enum FluxHandlerType implements HandlerType
{
	/**
	 * The handler that informs listeners about sounds starting or ending
	 */
	SOUNDLISTENERHANDLER;

	
	// IMPLEMENTED METHODS	-------------------------------
	
	@Override
	public Class<?> getSupportedHandledClass()
	{
		return SoundListener.class;
	}
}
