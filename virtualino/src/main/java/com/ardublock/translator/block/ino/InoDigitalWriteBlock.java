
package com.ardublock.translator.block.ino;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.virtualino.PinController;

/**
 *
 * @author lemrey
 */
public class InoDigitalWriteBlock extends TranslatorBlock {

    public InoDigitalWriteBlock(Long blockId, Translator translator, String codePrefix,
            String codeSuffix, String label)
    {
        super(blockId, translator, codePrefix, codeSuffix, label);
    }

    public String toCode() throws SocketNullException
    {
        int pin;
        String ret;
        PinController controller = new PinController();
        TranslatorBlock translatorBlock = getRequiredTranslatorBlockAtSocket(0);

        pin = controller.getComponentPins(label).get(0).intValue();
        String setupCode = "pinMode(" + pin + ", OUTPUT);";
        translator.addSetupCommand(setupCode);

        ret = "digitalWrite(" + pin + "," + translatorBlock.toCode() + ");\n";

        return ret;
    }
};
