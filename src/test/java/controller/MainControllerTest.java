package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import signal.Instruction;

import static controller.MainController.AluOperation.*;
import static controller.MainController.AluSource.IMMEDIATE;
import static controller.MainController.AluSource.REGISTER;
import static controller.MainController.MemoryToRegister.FROM_ALU_RESULT;
import static controller.MainController.MemoryToRegister.FROM_MEMORY;
import static controller.MainController.RegisterDestination.RD;
import static controller.MainController.RegisterDestination.RT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainControllerTest {

    private final Instruction add_signal = new Instruction("000000 00000000000000000000 100000");
    private final Instruction subtract_signal = new Instruction("000000 00000000000000000000 100010");
    private final Instruction load_word_signal = new Instruction("100011 00000000000000000000 000000");
    private final Instruction save_word_signal = new Instruction("101011 00000000000000000000 000000");
    private final Instruction branch_on_equal_signal = new Instruction("000100 00000000000000000000 000000");
    private MainController mainController;

    @BeforeEach
    void buildUp() {
        mainController = new MainController();
    }

    @Test
    void testGetAluOp() {
        mainController.setInstruction(add_signal);
        assertEquals(R_TYPE, mainController.getAluOperation());
        mainController.setInstruction(load_word_signal);
        assertEquals(MEMORY_REFERENCE, mainController.getAluOperation());
        mainController.setInstruction(save_word_signal);
        assertEquals(MEMORY_REFERENCE, mainController.getAluOperation());
        mainController.setInstruction(branch_on_equal_signal);
        assertEquals(BRANCH, mainController.getAluOperation());
    }

    @Test
    void testGetAluSource() {
        mainController.setInstruction(add_signal);
        assertEquals(REGISTER, mainController.getAluSource());
        mainController.setInstruction(subtract_signal);
        assertEquals(REGISTER, mainController.getAluSource());
        mainController.setInstruction(load_word_signal);
        assertEquals(IMMEDIATE, mainController.getAluSource());
        mainController.setInstruction(save_word_signal);
        assertEquals(IMMEDIATE, mainController.getAluSource());
        mainController.setInstruction(branch_on_equal_signal);
        assertEquals(REGISTER, mainController.getAluSource());
    }

    @Test
    void testGetMemoryRead() {
        mainController.setInstruction(add_signal);
        assertEquals(MainController.MemoryRead.FALSE, mainController.getMemoryRead());
        mainController.setInstruction(subtract_signal);
        assertEquals(MainController.MemoryRead.FALSE, mainController.getMemoryRead());
        mainController.setInstruction(load_word_signal);
        assertEquals(MainController.MemoryRead.TRUE, mainController.getMemoryRead());
        mainController.setInstruction(save_word_signal);
        assertEquals(MainController.MemoryRead.FALSE, mainController.getMemoryRead());
        mainController.setInstruction(branch_on_equal_signal);
        assertEquals(MainController.MemoryRead.FALSE, mainController.getMemoryRead());
    }

    @Test
    void testGetMemoryWrite() {
        mainController.setInstruction(add_signal);
        assertEquals(MainController.MemoryWrite.FALSE, mainController.getMemoryWrite());
        mainController.setInstruction(subtract_signal);
        assertEquals(MainController.MemoryWrite.FALSE, mainController.getMemoryWrite());
        mainController.setInstruction(load_word_signal);
        assertEquals(MainController.MemoryWrite.FALSE, mainController.getMemoryWrite());
        mainController.setInstruction(save_word_signal);
        assertEquals(MainController.MemoryWrite.TRUE, mainController.getMemoryWrite());
        mainController.setInstruction(branch_on_equal_signal);
        assertEquals(MainController.MemoryWrite.FALSE, mainController.getMemoryWrite());
    }

    @Test
    void testGetMemoryToRegister() {
        mainController.setInstruction(add_signal);
        assertEquals(FROM_ALU_RESULT, mainController.getMemoryToRegister());
        mainController.setInstruction(subtract_signal);
        assertEquals(FROM_ALU_RESULT, mainController.getMemoryToRegister());
        mainController.setInstruction(load_word_signal);
        assertEquals(FROM_MEMORY, mainController.getMemoryToRegister());
    }

    @Test
    void testGetRegisterDestination() {
        mainController.setInstruction(add_signal);
        assertEquals(RD, mainController.getRegisterDestination());
        mainController.setInstruction(subtract_signal);
        assertEquals(RD, mainController.getRegisterDestination());
        mainController.setInstruction(load_word_signal);
        assertEquals(RT, mainController.getRegisterDestination());
    }

    @Test
    void testGetRegisterWrite() {
        mainController.setInstruction(add_signal);
        assertEquals(MainController.RegisterWrite.TRUE, mainController.getRegisterWrite());
        mainController.setInstruction(subtract_signal);
        assertEquals(MainController.RegisterWrite.TRUE, mainController.getRegisterWrite());
        mainController.setInstruction(load_word_signal);
        assertEquals(MainController.RegisterWrite.TRUE, mainController.getRegisterWrite());
        mainController.setInstruction(save_word_signal);
        assertEquals(MainController.RegisterWrite.FALSE, mainController.getRegisterWrite());
        mainController.setInstruction(branch_on_equal_signal);
        assertEquals(MainController.RegisterWrite.FALSE, mainController.getRegisterWrite());
    }

    @Test
    void testGetBranch() {
        mainController.setInstruction(add_signal);
        assertEquals(MainController.Branch.FALSE, mainController.getBranch());
        mainController.setInstruction(subtract_signal);
        assertEquals(MainController.Branch.FALSE, mainController.getBranch());
        mainController.setInstruction(load_word_signal);
        assertEquals(MainController.Branch.FALSE, mainController.getBranch());
        mainController.setInstruction(save_word_signal);
        assertEquals(MainController.Branch.FALSE, mainController.getBranch());
        mainController.setInstruction(branch_on_equal_signal);
        assertEquals(MainController.Branch.TRUE, mainController.getBranch());
    }
}