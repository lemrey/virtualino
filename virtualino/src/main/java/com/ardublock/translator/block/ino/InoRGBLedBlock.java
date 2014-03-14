package com.ardublock.translator.block.ino;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.virtualino.PinController;

/**
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class InoRGBLedBlock extends TranslatorBlock
{
    public InoRGBLedBlock(Long blockId, Translator translator, String codePrefix,
	    String codeSuffix, String label)
    {
	super(blockId, translator, codePrefix, codeSuffix, label);
    }

    public String toCode() throws SocketNullException
    {
	String ret = new String();
	PinController controller = new PinController();
	TranslatorBlock[] params = new TranslatorBlock[3];

	for (int i = 0; i < 3; i++) {
	    int pin = controller.getComponentPins(label).get(i).intValue();
	    params[i] = getRequiredTranslatorBlockAtSocket(i);

	    ret += "analogWrite(" + pin + ", ";

	    if (params[i] instanceof InoAnalogReadBlock) {
		ret += ("map(" + params[i].toCode() + ",0,1023,0,255)");
	    }
	    else {
		ret += params[i].toCode();
	    }

	    ret += ");\n";
	}

	for (Integer pin : controller.getComponentPins(label)) {
	    String setupCode = "pinMode(" + pin + ", OUTPUT);";
	    translator.addSetupCommand(setupCode);
	    // need to set appropriate PWM mode for pin 3
	    if (pin == 3) {
		setupCode = "bitSet(TCCR2A, WGM21); // setting PWM mode";
		translator.addSetupCommand(setupCode);
	    }
	}

	return ret;
    }
};
