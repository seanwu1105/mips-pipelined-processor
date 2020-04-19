package model;

import org.junit.jupiter.api.Test;

import static model.FunctionCode.*;
import static model.OpCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InstructionTest {

    @Test
    void testIncorrectLength() {
        assertThrows(IllegalArgumentException.class, () -> new Instruction("0           0                  0"));
    }

    @Test
    void testGetOpCode() {
        Instruction instruction = new Instruction("000000 00000000000000000000000000");
        assertEquals(R_TYPE, instruction.getOpCode());
        instruction = new Instruction("100011 00000000000000000000000000");
        assertEquals(LOAD_WORD, instruction.getOpCode());
        instruction = new Instruction("101011 00000000000000000000000000");
        assertEquals(SAVE_WORD, instruction.getOpCode());
        instruction = new Instruction("000100 00000000000000000000000000");
        assertEquals(BRANCH_ON_EQUAL, instruction.getOpCode());
    }

    @Test
    void testGetUnknownOpCode() {
        Instruction instruction = new Instruction("111111 00000000000000000000000000");
        assertThrows(IllegalStateException.class, instruction::getOpCode);
    }

    @Test
    void testGetFunctionCode() {
        Instruction instruction = new Instruction("00000000000000000000000000 100000");
        assertEquals(ADD, instruction.getFunctionCode());
        instruction = new Instruction("00000000000000000000000000 100010");
        assertEquals(SUBTRACT, instruction.getFunctionCode());
        instruction = new Instruction("00000000000000000000000000 100100");
        assertEquals(AND, instruction.getFunctionCode());
        instruction = new Instruction("00000000000000000000000000 100101");
        assertEquals(OR, instruction.getFunctionCode());
        instruction = new Instruction("00000000000000000000000000 101010");
        assertEquals(SET_ON_LESS_THAN, instruction.getFunctionCode());
    }

    @Test
    void testGetUnknownFunctionCode() {
        assertThrows(IllegalStateException.class, () -> new Instruction("00000000000000000000000000 000000").getFunctionCode());
    }
}