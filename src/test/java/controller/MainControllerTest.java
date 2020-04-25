package controller;

import signal.Instruction;
import org.junit.jupiter.api.Test;

import static controller.MainController.AluOperation.*;
import static controller.MainController.AluSource.IMMEDIATE;
import static controller.MainController.AluSource.REGISTER;
import static controller.MainController.MemoryToRegister.FROM_ALU_RESULT;
import static controller.MainController.MemoryToRegister.FROM_MEMORY;
import static controller.MainController.RegisterDestination.RD;
import static controller.MainController.RegisterDestination.RT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainControllerTest {

    final String add_signal = "000000 00000000000000000000 100000";
    final String subtract_signal = "000000 00000000000000000000 100010";
    final String load_word_signal = "100011 00000000000000000000 000000";
    final String save_word_signal = "101011 00000000000000000000 000000";
    final String branch_on_equal_signal = "000100 00000000000000000000 000000";

    @Test
    void testGetAluOp() {
        Instruction instruction = new Instruction(add_signal);
        assertEquals(R_TYPE, new MainController(instruction).getAluOperation());
        instruction = new Instruction(load_word_signal);
        assertEquals(MEMORY_REFERENCE, new MainController(instruction).getAluOperation());
        instruction = new Instruction(save_word_signal);
        assertEquals(MEMORY_REFERENCE, new MainController(instruction).getAluOperation());
        instruction = new Instruction(branch_on_equal_signal);
        assertEquals(BRANCH, new MainController(instruction).getAluOperation());
    }

    @Test
    void testGetAluSource() {
        assertEquals(REGISTER, new MainController(new Instruction(add_signal)).getAluSource());
        assertEquals(REGISTER, new MainController(new Instruction(subtract_signal)).getAluSource());
        assertEquals(IMMEDIATE, new MainController(new Instruction(load_word_signal)).getAluSource());
        assertEquals(IMMEDIATE, new MainController(new Instruction(save_word_signal)).getAluSource());
        assertEquals(REGISTER, new MainController(new Instruction(branch_on_equal_signal)).getAluSource());
    }

    @Test
    void testGetMemoryRead() {
        assertEquals(MainController.MemoryRead.FALSE, new MainController(new Instruction(add_signal)).getMemoryRead());
        assertEquals(MainController.MemoryRead.FALSE, new MainController(new Instruction(subtract_signal)).getMemoryRead());
        assertEquals(MainController.MemoryRead.TRUE, new MainController(new Instruction(load_word_signal)).getMemoryRead());
        assertEquals(MainController.MemoryRead.FALSE, new MainController(new Instruction(save_word_signal)).getMemoryRead());
        assertEquals(MainController.MemoryRead.FALSE, new MainController(new Instruction(branch_on_equal_signal)).getMemoryRead());
    }

    @Test
    void testGetMemoryWrite() {
        assertEquals(MainController.MemoryWrite.FALSE, new MainController(new Instruction(add_signal)).getMemoryWrite());
        assertEquals(MainController.MemoryWrite.FALSE, new MainController(new Instruction(subtract_signal)).getMemoryWrite());
        assertEquals(MainController.MemoryWrite.FALSE, new MainController(new Instruction(load_word_signal)).getMemoryWrite());
        assertEquals(MainController.MemoryWrite.TRUE, new MainController(new Instruction(save_word_signal)).getMemoryWrite());
        assertEquals(MainController.MemoryWrite.FALSE, new MainController(new Instruction(branch_on_equal_signal)).getMemoryWrite());
    }

    @Test
    void testGetMemoryToRegister() {
        assertEquals(FROM_ALU_RESULT, new MainController(new Instruction(add_signal)).getMemoryToRegister());
        assertEquals(FROM_ALU_RESULT, new MainController(new Instruction(subtract_signal)).getMemoryToRegister());
        assertEquals(FROM_MEMORY, new MainController(new Instruction(load_word_signal)).getMemoryToRegister());
    }

    @Test
    void testGetRegisterDestination() {
        assertEquals(RD, new MainController(new Instruction(add_signal)).getRegisterDestination());
        assertEquals(RD, new MainController(new Instruction(subtract_signal)).getRegisterDestination());
        assertEquals(RT, new MainController(new Instruction(load_word_signal)).getRegisterDestination());
    }

    @Test
    void testGetRegisterWrite() {
        assertEquals(MainController.RegisterWrite.TRUE, new MainController(new Instruction(add_signal)).getRegisterWrite());
        assertEquals(MainController.RegisterWrite.TRUE, new MainController(new Instruction(subtract_signal)).getRegisterWrite());
        assertEquals(MainController.RegisterWrite.TRUE, new MainController(new Instruction(load_word_signal)).getRegisterWrite());
        assertEquals(MainController.RegisterWrite.FALSE, new MainController(new Instruction(save_word_signal)).getRegisterWrite());
        assertEquals(MainController.RegisterWrite.FALSE, new MainController(new Instruction(branch_on_equal_signal)).getRegisterWrite());
    }

    @Test
    void testGetBranch() {
        assertEquals(MainController.Branch.FALSE, new MainController(new Instruction(add_signal)).getBranch());
        assertEquals(MainController.Branch.FALSE, new MainController(new Instruction(subtract_signal)).getBranch());
        assertEquals(MainController.Branch.FALSE, new MainController(new Instruction(load_word_signal)).getBranch());
        assertEquals(MainController.Branch.FALSE, new MainController(new Instruction(save_word_signal)).getBranch());
        assertEquals(MainController.Branch.TRUE, new MainController(new Instruction(branch_on_equal_signal)).getBranch());
    }
}