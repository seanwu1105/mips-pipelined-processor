package controller;

import model.Instruction;
import org.junit.jupiter.api.Test;

import static controller.MainController.AluOp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void testGetUnknownAluOp() {
        assertThrows(IllegalStateException.class, () -> {
            // TODO: Mock an instruction returning an op code which cannot be recognized by MainController.
        });
    }
}