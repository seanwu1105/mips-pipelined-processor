package io.github.seanwu1105.mipsprocessor.component;

import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;

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

    public void setAddress(final int address) {
        this.address = address;
    }

    public void setMemoryWrite(@NotNull final MainController.MemoryWrite memoryWrite) {
        this.memoryWrite = memoryWrite;
    }

    public void setMemoryRead(@NotNull final MainController.MemoryRead memoryRead) {
        this.memoryRead = memoryRead;
    }

    public void write(final int value) {
        if (memoryWrite == MainController.MemoryWrite.FALSE)
            throw new IllegalStateException("Memory cannot be written.");
        data.put(address, value);
    }

    public void write(@NotNull final Instruction instruction) {
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

    @NotNull
    public Set<Integer> getWrittenAddresses() {
        return data.keySet();
    }
}
