package component;

import controller.MainController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
        final int data1 = 10;
        final int data2 = 20;

        register.setRegisterWrite(MainController.RegisterWrite.TRUE);
        register.setWriteAddress(1);
        register.write(data1);
        register.setWriteAddress(2);
        register.write(data2);
        register.setReadAddress1(1);
        register.setReadAddress2(2);

        assertEquals(data1, register.readData1());
        assertEquals(data2, register.readData2());
    }

    @Test
    void testWriteTo0Address() {
        assertThrows(IllegalArgumentException.class, () -> register.setWriteAddress(0));
    }

    @Test
    void testWriteOnReadOnlyRegister() {
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
        register.setWriteAddress(1);
        assertThrows(IllegalStateException.class, () -> register.write(10));
    }

    @Test
    void testReadUnwrittenData() {
        register.setReadAddress1(1);
        register.setReadAddress2(2);
        assertThrows(NullPointerException.class, () -> register.readData1());
        assertThrows(NullPointerException.class, () -> register.readData2());
    }

    @Test
    void testGetInitializedAddresses() {
        final Set<Integer> expect = Set.of(0, 2, 4);

        register.setRegisterWrite(MainController.RegisterWrite.TRUE);
        for (final Integer address : expect) {
            if (address != 0) {
                register.setWriteAddress(address);
                register.write(address * 10);
            }
        }

        assertEquals(expect, register.getWrittenAddresses());
    }

    @AfterEach
    void tearDown() {
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
    }
}