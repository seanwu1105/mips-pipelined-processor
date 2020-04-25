package component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AluTest {

    private Alu alu;
    private final int operand1 = 1;
    private final int operand2 = 2;

    @BeforeEach
    void buildUp() {
        Alu alu = new Alu();
        alu.setOperand1(operand1);
        alu.setOperand2(operand2);
    }

    @Test
    void testAdd() {
        alu.setControl(Alu.AluControl.ADD);
        alu.run();
        assertEquals(operand1 + operand2, alu.getResult());
    }

    @Test
    void testSubtract() {
        alu.setControl(Alu.AluControl.SUBTRACT);
        alu.run();
        assertEquals(operand1 - operand2, alu.getResult());
    }

    @Test
    void testAnd() {
        alu.setControl(Alu.AluControl.AND);
        alu.run();
        assertEquals(operand1 & operand2, alu.getResult());
    }

    @Test
    void testOr() {
        alu.setControl(Alu.AluControl.OR);
        alu.run();
        assertEquals(operand1 | operand2, alu.getResult());
    }

    @Test
    void testSetOnLessThan() {
        alu.setControl(Alu.AluControl.SET_ON_LESS_THAN);
        alu.run();
        assertEquals(1, alu.getResult());
    }
}