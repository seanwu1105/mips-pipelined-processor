package component;

import controller.MainController;
import org.jetbrains.annotations.NotNull;
import signal.Instruction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Memory {

    @NotNull
    private final Map<Integer, Integer> data = new HashMap<>();
    private int address;

    @NotNull
    private MainController.MemoryWrite memoryWrite = MainController.MemoryWrite.FALSE;

    @NotNull
    private MainController.MemoryRead memoryRead = MainController.MemoryRead.FALSE;

    public void setAddress(int address) {
        this.address = address;
    }

    public void setMemoryWrite(@NotNull MainController.MemoryWrite memoryWrite) {
        this.memoryWrite = memoryWrite;
    }

    public void setMemoryRead(@NotNull MainController.MemoryRead memoryRead) {
        this.memoryRead = memoryRead;
    }

    public void write(int value) {
        if (memoryWrite == MainController.MemoryWrite.FALSE)
            throw new IllegalStateException("Memory cannot be written.");
        data.put(address, value);
    }

    public void write(Instruction instruction) {
        write(instruction.toInt());
    }

    public int read() {
        if (memoryRead == MainController.MemoryRead.FALSE)
            throw new IllegalStateException("Memory cannot be read.");
        return data.get(address);
    }

    @NotNull
    public Instruction readInstruction() {
        return new Instruction(read());
    }

    public Set<Integer> getWrittenAddresses() {
        return data.keySet();
    }
}
