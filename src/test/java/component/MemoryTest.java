package component;

import controller.MainController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import signal.Instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemoryTest {

    private Memory memory;

    @BeforeEach
    void buildUp() {
        memory = new Memory();
        memory.setAddress(0x04);
    }

    @Test
    void testWriteReadInteger() {
        final int expect = 9;
        memory.setMemoryWrite(MainController.MemoryWrite.TRUE);
        memory.write(expect);
        memory.setMemoryRead(MainController.MemoryRead.TRUE);
        assertEquals(expect, memory.read());
    }


    @Test
    void testWriteReadInstruction() {
        final Instruction expect = new Instruction("10001101000000010000000000000011");
        memory.setMemoryWrite(MainController.MemoryWrite.TRUE);
        memory.write(expect);
        memory.setMemoryRead(MainController.MemoryRead.TRUE);
        assertEquals(expect, memory.readInstruction());
    }

    @Test
    void testReadToWriteOnlyMemory() {
        memory.setMemoryWrite(MainController.MemoryWrite.TRUE);
        memory.write(5);
        memory.setMemoryRead(MainController.MemoryRead.FALSE);
        assertThrows(IllegalStateException.class, () -> memory.read());
    }

    @Test
    void testWriteToReadOnlyMemory() {
        memory.setMemoryWrite(MainController.MemoryWrite.FALSE);
        assertThrows(IllegalStateException.class, () -> memory.write(5));
    }

    @Test
    void testReadUnwrittenData() {
        memory.setMemoryRead(MainController.MemoryRead.TRUE);
        assertThrows(NullPointerException.class, () -> memory.read());
    }
}