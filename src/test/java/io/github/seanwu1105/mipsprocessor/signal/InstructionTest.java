package io.github.seanwu1105.mipsprocessor.signal;

import org.junit.jupiter.api.Test;

import static io.github.seanwu1105.mipsprocessor.signal.FunctionCode.ADD;
import static io.github.seanwu1105.mipsprocessor.signal.FunctionCode.AND;
import static io.github.seanwu1105.mipsprocessor.signal.FunctionCode.OR;
import static io.github.seanwu1105.mipsprocessor.signal.FunctionCode.SET_ON_LESS_THAN;
import static io.github.seanwu1105.mipsprocessor.signal.FunctionCode.SUBTRACT;
import static io.github.seanwu1105.mipsprocessor.signal.OpCode.BRANCH_ON_EQUAL;
import static io.github.seanwu1105.mipsprocessor.signal.OpCode.LOAD_WORD;
import static io.github.seanwu1105.mipsprocessor.signal.OpCode.R_TYPE;
import static io.github.seanwu1105.mipsprocessor.signal.OpCode.SAVE_WORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InstructionTest {

    @Test
    void testFromLong() {
        final var expect = new Instruction("00000000000000000000000000000001");
        final var instruction = new Instruction(1);
        assertEquals(expect, instruction);
    }

    @Test
    void testToInt() {
        final var instruction = new Instruction("11111111111111111111111111111111");
        assertEquals(Integer.parseUnsignedInt("11111111111111111111111111111111", 2), instruction.toInt());
    }

    @Test
    void testIncorrectLength() {
        assertThrows(IllegalArgumentException.class, () -> new Instruction("0           0                  0"));
    }

    @Test
    void testEquals() {
        final var raw = "10101010101010101010101010101010";
        final var a = new Instruction(raw);
        final var b = new Instruction(raw);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToString() {
        final var raw = "10101010101010101010101010101010";
        assertEquals(raw, "" + new Instruction(raw));
    }

    @Test
    void testGetOpCode() {
        var instruction = new Instruction("000000 00000000000000000000000000");
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
        final var instruction = new Instruction("111111 00000000000000000000000000");
        assertThrows(IllegalStateException.class, instruction::getOpCode);
    }

    @Test
    void testGetRsAndRtAndRd() {
        final var instruction = new Instruction("000000 00000 00001 00010 00000 100000"); // add $0 $1 $2
        assertEquals(0, instruction.getRs());
        assertEquals(1, instruction.getRt());
        assertEquals(2, instruction.getRd());
    }

    @Test
    void testGetFunctionCode() {
        var instruction = new Instruction("00000000000000000000000000 100000");
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
        assertThrows(IllegalStateException.class, () -> new Instruction("00000000000000000000000000 111111").getFunctionCode());
    }

    @Test
    void testGetImmediate() {
        final var instruction = new Instruction("100011 00001 00010 0000000000000001"); // lw $1 1($2)
        assertEquals(1, instruction.getImmediate());
    }
}