package component;

import controller.MainController;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Memory {

    private final Map<Integer, Integer> data = new HashMap<>();
    private int address;

    @NotNull
    private MainController.MemoryWrite memoryWrite = MainController.MemoryWrite.FALSE;

    public void setAddress(int address) {
        this.address = address;
    }

    public void setMemoryWrite(@NotNull MainController.MemoryWrite memoryWrite) {
        this.memoryWrite = memoryWrite;
    }

    public void setMemoryRead(@NotNull MainController.MemoryRead memoryRead) {
        if (memoryRead == MainController.MemoryRead.TRUE)
            memoryWrite = MainController.MemoryWrite.FALSE;
        else
            memoryWrite = MainController.MemoryWrite.TRUE;
    }

    public void write(int value) {
        if (memoryWrite != MainController.MemoryWrite.TRUE)
            throw new IllegalStateException("Memory is read-only.");
        data.put(address, value);
    }

    public int read() {
        if (memoryWrite == MainController.MemoryWrite.TRUE)
            throw new IllegalStateException("Memory is write-only.");
        return data.get(address);
    }
}
