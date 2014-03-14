package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.ino.InoAnalogReadBlock;
import com.ardublock.translator.block.ino.InoAnalogWriteBlock;
import com.ardublock.translator.block.ino.InoDigitalReadBlock;
import com.ardublock.translator.block.ino.InoDigitalWriteBlock;
import com.ardublock.translator.block.ino.InoRGBLedBlock;
import com.ardublock.translator.block.ino.InoTemperatureBlock;

public class TranslatorBlockFactory
{
    public TranslatorBlock buildTranslatorBlock(Translator translator, Long blockId, String blockName, String codePrefix, String codeSuffix, String label)
    {
	//System.out.println("block name : " + blockName + " captured");            

	//Arduino
	if (blockName.equals("loop")) {
	    return new LoopBlock(blockId, translator);
	}
	if (blockName.equals("ifelse")) {
	    return new IfelseBlock(blockId, translator);
	}
	if (blockName.equals("true")) {
	    return new TrueBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("and")) {
	    return new AndBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("not")) {
	    return new NotBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("or")) {
	    return new OrBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("digital-high")) {
	    return new DigitalHighBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("digital-low")) {
	    return new DigitalLowBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("digital-on")) {
	    return new DigitalOnBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("digital-off")) {
	    return new DigitalOffBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("false")) {
	    return new FalseBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("if")) {
	    return new IfBlock(blockId, translator);
	}
	if (blockName.equals("while")) {
	    return new WhileBlock(blockId, translator);
	}
	if (blockName.equals("repeat_times")) {
	    return new RepeatTimesBlock(blockId, translator);
	}

	// const number
	if (blockName.equals("number")) {
	    return new NumberBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("message")) {
	    return new MessageBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("glue_sn")) {
	    return new GlueSNBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("glue_sb")) {
	    return new GlueSBBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	// variables
	if (blockName.equals("setter_variable_number")) {
	    return new SetterVariableNumberBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("variable_number")) {
	    return new VariableNumberBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("setter_variable_digital")) {
	    return new SetterVariableDigitalBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("variable_digital")) {
	    return new VariableDigitalBlock(blockId, translator, codePrefix, codeSuffix, label);
	}

	// math
	if (blockName.equals("addition")) {
	    return new AdditionBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("subtraction")) {
	    return new SubtractionBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("multiplication")) {
	    return new MultiplicationBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("division")) {
	    return new DivisionBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("modulo")) {
	    return new ModuloBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("greater")) {
	    return new GreaterBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("less")) {
	    return new LessBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("equal")) {
	    return new EqualBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("greater_equal")) {
	    return new GreaterEqualBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("less_equal")) {
	    return new LessEqualBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("not_equal")) {
	    return new NotEqualBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("min")) {
	    return new MinBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("max")) {
	    return new MaxBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("abs")) {
	    return new AbsBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("pow")) {
	    return new PowBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("sqrt")) {
	    return new SqrtBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("sin")) {
	    return new SinBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("cos")) {
	    return new CosBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("tan")) {
	    return new TanBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	// utility
	if (blockName.equals("delay")) {
	    return new DelayBlock(blockId, translator);
	}
	if (blockName.equals("random")) {
	    return new RandomBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("serial_println")) {
	    return new SerialPrintlnBlock(blockId, translator);
	}
	if (blockName.equals("map_common")) {
	    return new MapCommonBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("map")) {
	    return new MapBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("delay_microseconds")) {
	    return new DelayMicrosecondsBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("millis")) {
	    return new MillisBlock(blockId, translator, codePrefix, codeSuffix, label);
	}

	// Virtualino

	if (blockName.equals("ino_led_r") || blockName.equals("ino_led_g")
	    || blockName.equals("ino_led_y")) {
	    return new InoDigitalWriteBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("ino_analog_led")) {
	    return new InoAnalogWriteBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("ino_rgb_led")) {
	    return new InoRGBLedBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("ino_button")) {
	    return new InoDigitalReadBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("ino_buzzer")) {
	    return new InoDigitalWriteBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("ino_slider")) {
	    return new InoAnalogReadBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("ino_ldr")) {
	    return new InoAnalogReadBlock(blockId, translator, codePrefix, codeSuffix, label);
	}
	if (blockName.equals("ino_temperature")) {
	    return new InoTemperatureBlock(blockId, translator, codePrefix, codeSuffix, label);
	}

	System.err.println(blockName + " translator block not found!");

	return null;
    }
}
