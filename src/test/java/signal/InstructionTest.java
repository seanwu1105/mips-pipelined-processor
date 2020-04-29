package signal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static signal.FunctionCode.*;
import static signal.OpCode.*;

class InstructionTest {

    @Test
    void testFromLong() {
        Instruction expect = new Instruction("11111111111111111111111111111111");
        Instruction instruction = new Instruction(4294967295L);
        assertEquals(expect, instruction);
    }

    @Test
    void testToLong() {
        Instruction instruction = new Instruction("11111111111111111111111111111111");
        assertEquals(4294967295L, instruction.toLong());
    }

    @Test
    void testIncorrectLength() {
        assertThrows(IllegalArgumentException.class, () -> new Instruction("0           0                  0"));
    }

    @Test
    void testEquals() {
        String raw = "00000000000000000000000000000000";
        Instruction a = new Instruction(raw);
        Instruction b = new Instruction(raw);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
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