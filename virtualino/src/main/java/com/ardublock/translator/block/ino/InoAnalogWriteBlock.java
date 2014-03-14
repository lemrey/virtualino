package com.ardublock.translator.block.ino;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.virtualino.PinController;

/**
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class InoAnalogWriteBlock extends TranslatorBlock
{
    public InoAnalogWriteBlock(Long blockId, Translator translator, String codePrefix,
	    String codeSuffix, String label)
    {
	super(blockId, translator, codePrefix, codeSuffix, label);
    }

    @Override
    public String toCode() throws SocketNullException
    {
	int pin;
	String ret;
	PinController controller = new PinController();
	TranslatorBlock param = getRequiredTranslatorBlockAtSocket(0);

	pin = controller.getComponentPins(label).get(0).intValue();

	String timerSetup;
	switch (pin) {
	    case 3:
		timerSetup = "bitSet(TCCR2A, WGM21);";
		break;
	    /*case 5:
		timerSetup = "";
		break;
	    case 6:
		timerSetup = "";
		break;*/
	    default: {
		timerSetup = "";
	    }
	}

	String setupCode = "pinMode(" + pin + ", OUTPUT);";
	translator.addSetupCommand(setupCode);
	translator.addSetupCommand(timerSetup);

	if (param instanceof InoAnalogReadBlock) {
	    ret = "analogWrite(" + pin + ", map(" +param.toCode() + " , 0, 1023, 0, 255));\n";
	} else {
	    ret = "analogWrite(" + pin + "," + param.toCode() + ");\n";
	}

	return ret;
    }
}
