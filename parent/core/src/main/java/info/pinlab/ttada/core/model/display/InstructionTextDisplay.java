package info.pinlab.ttada.core.model.display;

import info.pinlab.ttada.core.model.ExtendedResource;

/**
 * This is a subclass of {@link TextDisplay} that is displayed as instruction.
 * 
 * @author Gábor PINTÉR
 *
 */
public class InstructionTextDisplay extends TextDisplay implements Display, ExtendedResource<Display> {
	public InstructionTextDisplay(String intstruction){
		super(intstruction);
	}
}
