package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;

public class SerialPrintlnBlock extends TranslatorBlock
{
    protected SerialPrintlnBlock(Long blockId, Translator translator)
    {
	super(blockId, translator);
    }

    public String toCode() throws SocketNullException
    {
	String ret;
	translator.addSetupCommand("Serial.begin(9600);");
	TranslatorBlock param = this.getRequiredTranslatorBlockAtSocket(0);


	ret = "Serial.println(" + param.toCode() + ");\n";

	return ret;
    }
}
