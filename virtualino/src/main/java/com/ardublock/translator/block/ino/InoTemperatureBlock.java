/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ardublock.translator.block.ino;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.virtualino.PinController;

/**
 *
 * @author lemrey
 */
public class InoTemperatureBlock extends TranslatorBlock
{
     public InoTemperatureBlock(Long blockId, Translator translator,
	    String codePrefix, String codeSuffix, String label)
    {
	super(blockId, translator, codePrefix, codeSuffix, label);
    }

    public String toCode() throws SocketNullException
    {
	String ret;
        
        PinController controller = new PinController();
        int pin = controller.getComponentPins(label).get(0).intValue();

	ret = "((5.0 * analogRead(" + pin + ") * 100.0) / 1024)";

        return ret;
    }
    
}
