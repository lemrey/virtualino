package com.ardublock.translator.block.ino;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.virtualino.PinController;

/**
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class InoAnalogReadBlock extends TranslatorBlock
{
    public InoAnalogReadBlock(Long blockId, Translator translator,
	    String codePrefix, String codeSuffix, String label)
    {
	super(blockId, translator, codePrefix, codeSuffix, label);
    }

    public String toCode() throws SocketNullException
    {
	String ret;
	PinController controller = new PinController();
	int pin = controller.getComponentPins(label).get(0).intValue();

	String setupCode = "pinMode(" + pin + ", INPUT);";
	translator.addSetupCommand(setupCode);

	ret = "analogRead(" + pin + ")";

	return ret;
    }
}
