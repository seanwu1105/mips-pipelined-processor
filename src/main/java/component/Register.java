package component;

import controller.MainController;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Register {

    private final Map<Integer, Integer> data = new HashMap<>();

    @NotNull
    private MainController.RegisterWrite registerWrite = MainController.RegisterWrite.FALSE;
    private int writeAddress, readAddress1, readAddress2;

    public Register() {
        data.put(0, 0);
    }

    public void setRegisterWrite(@NotNull MainController.RegisterWrite registerWrite) {
        this.registerWrite = registerWrite;
    }

    public void setWriteAddress(int writeAddress) {
        if (writeAddress == 0) throw new IllegalArgumentException("Address 0 is not writable.");
        this.writeAddress = writeAddress;
    }

    public void setReadAddress1(int readAddress1) {
        this.readAddress1 = readAddress1;
    }

    public void setReadAddress2(int readAddress2) {
        this.readAddress2 = readAddress2;
    }

    public void write(int value) {
        if (registerWrite != MainController.RegisterWrite.TRUE)
            throw new IllegalStateException("The register is read-only.");
        data.put(writeAddress, value);
    }

    public int readData1() {
        return data.get(readAddress1);
    }

    public int readData2() {
        return data.get(readAddress2);
    }

    @NotNull
    public Set<Integer> getWrittenAddresses() {
        return data.keySet();
    }
}
