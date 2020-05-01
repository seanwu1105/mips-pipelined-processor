package component.pipeline;

import component.Memory;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MemoryAccess implements Stage {

    @NotNull
    private final ExecutionToMemoryAccessRegister exeMem;

    @NotNull
    private final Memory dataMemory;

    @Nullable
    private MainController.RegisterWrite registerWrite;

    @Nullable
    private MainController.MemoryToRegister memoryToRegister;

    private int memoryReadData, aluResult, writeRegisterAddress;

    public MemoryAccess(
            @NotNull ExecutionToMemoryAccessRegister exeMem,
            @NotNull Memory dataMemory
    ) {
        this.exeMem = exeMem;
        this.dataMemory = dataMemory;
    }

    @Override
    public void run() {
        passProperties();
        accessMemory();
    }

    private void passProperties() {
        registerWrite = exeMem.getRegisterWrite();
        memoryToRegister = exeMem.getMemoryToRegister();
        aluResult = exeMem.getAluResult();
        writeRegisterAddress = exeMem.getWriteRegisterAddress();
    }

    private void accessMemory() {
        dataMemory.setAddress(exeMem.getAluResult());
        dataMemory.setMemoryRead(Objects.requireNonNull(exeMem.getMemoryRead()));
        dataMemory.setMemoryWrite(Objects.requireNonNull(exeMem.getMemoryWrite()));
        if (exeMem.getMemoryRead() == MainController.MemoryRead.TRUE)
            memoryReadData = dataMemory.read();
        else if (exeMem.getMemoryWrite() == MainController.MemoryWrite.TRUE)
            dataMemory.write(exeMem.getRegisterData2());
    }

    @Nullable
    public MainController.RegisterWrite getRegisterWrite() {
        return registerWrite;
    }

    @Nullable
    public MainController.MemoryToRegister getMemoryToRegister() {
        return memoryToRegister;
    }

    public int getMemoryReadData() {
        return memoryReadData;
    }

    public int getAluResult() {
        return aluResult;
    }

    public int getWriteRegisterAddress() {
        return writeRegisterAddress;
    }
}
