package controller;

import model.Instruction;
import org.junit.jupiter.api.Test;

import static controller.MainController.AluOp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainControllerTest {

    @Test
    void testGetAluOp() {
        Instruction instruction = new Instruction("000000 00000000000000000000000000");
        assertEquals(R_TYPE, MainController.getAluOp(instruction));
        instruction = new Instruction("100011 00000000000000000000000000");
        assertEquals(MEMORY_REFERENCE, MainController.getAluOp(instruction));
        instruction = new Instruction("101011 00000000000000000000000000");
        assertEquals(MEMORY_REFERENCE, MainController.getAluOp(instruction));
        instruction = new Instruction("000100 00000000000000000000000000");
        assertEquals(BRANCH, MainController.getAluOp(instruction));
    }
}