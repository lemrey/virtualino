
package com.ardublock.translator.block.ino;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.virtualino.PinController;

/**
 *
 * @author lemrey
 */
public class InoDigitalReadBlock extends TranslatorBlock {

    public InoDigitalReadBlock(Long blockId, Translator translator, String codePrefix,
            String codeSuffix, String label)
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
        
	ret = "digitalRead(" + pin + ")";

        return ret;
    }
};

