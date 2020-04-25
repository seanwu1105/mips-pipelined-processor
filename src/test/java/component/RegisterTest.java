package component;

import controller.MainController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterTest {

    private Register register;

    @BeforeEach
    void buildUp() {
        register = new Register();
    }

    @Test
    void testWriteAndReadData() {
        int data1 = 10, data2 = 20;

        register.setRegisterWrite(MainController.RegisterWrite.TRUE);
        register.setWriteAddress(0);
        register.write(data1);
        register.setWriteAddress(1);
        register.write(data2);

        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
        register.setReadAddress1(0);
        register.setReadAddress2(1);

        assertEquals(data1, register.readData1());
        assertEquals(data2, register.readData2());
    }

    @Test
    void testWriteOnReadOnlyRegister() {
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
        register.setWriteAddress(0);
        assertThrows(IllegalStateException.class, () -> register.write(10));
    }

    @Test
    void testReadOnWriteOnlyRegister() {
        int address = 0;
        register.setRegisterWrite(MainController.RegisterWrite.TRUE);
        register.setWriteAddress(address);
        register.write(10);
        register.setReadAddress1(address);
        assertThrows(IllegalStateException.class, () -> register.readData1());
    }

    @Test
    void testReadUnwrittenData() {
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
        register.setReadAddress1(0);
        register.setReadAddress2(1);
        assertThrows(NullPointerException.class, () -> register.readData1());
        assertThrows(NullPointerException.class, () -> register.readData2());
    }
}