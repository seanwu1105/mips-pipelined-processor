package component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AluTest {

    @Test
    void testAdd() {
        Alu alu = new Alu();
        alu.setControl(Alu.AluControl.ADD);
        alu.setOperand1(1);
        alu.setOperand2(2);
        alu.run();
        assertEquals(1 + 2, alu.getResult());
    }

    @Test
    void testSubtract() {
        Alu alu = new Alu();
        alu.setControl(Alu.AluControl.SUBTRACT);
        alu.setOperand1(1);
        alu.setOperand2(2);
        alu.run();
        assertEquals(1 - 2, alu.getResult());
    }

    @Test
    void testAnd() {
        Alu alu = new Alu();
        alu.setControl(Alu.AluControl.AND);
        alu.setOperand1(1);
        alu.setOperand2(2);
        alu.run();
        assertEquals(1 & 2, alu.getResult());
    }

    @Test
    void testOr() {
        Alu alu = new Alu();
        alu.setControl(Alu.AluControl.OR);
        alu.setOperand1(1);
        alu.setOperand2(2);
        alu.run();
        assertEquals(1 | 2, alu.getResult());
    }

    @Test
    void testSetOnLessThan() {
        Alu alu = new Alu();
        alu.setControl(Alu.AluControl.SET_ON_LESS_THAN);
        alu.setOperand1(1);
        alu.setOperand2(2);
        alu.run();
        assertEquals(1, alu.getResult());
    }
}