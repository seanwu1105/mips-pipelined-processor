package component;

import controller.MainController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testWriteReadDate() {
        int expect = 9;
        memory.setMemoryWrite(MainController.MemoryWrite.TRUE);
        memory.write(expect);
        memory.setMemoryWrite(MainController.MemoryWrite.FALSE);
        assertEquals(expect, memory.read());
    }

    @Test
    void testReadToWriteOnlyMemory() {
        memory.setMemoryWrite(MainController.MemoryWrite.TRUE);
        memory.write(5);
        assertThrows(IllegalStateException.class, () -> memory.read());
    }

    @Test
    void testWriteToReadOnlyMemory() {
        memory.setMemoryWrite(MainController.MemoryWrite.FALSE);
        assertThrows(IllegalStateException.class, () -> memory.write(5));
    }

    @Test
    void testReadUnwrittenData() {
        memory.setMemoryWrite(MainController.MemoryWrite.FALSE);
        assertThrows(NullPointerException.class, () -> memory.read());
    }
}