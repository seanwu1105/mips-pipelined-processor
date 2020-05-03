package component.pipeline;

import component.Memory;
import controller.MainController;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MemoryAccess implements Stage {

    @NotNull
    private final ExecutionToMemoryAccessRegister exeMem;

    @NotNull
    private final Memory dataMemory;

    @NotNull
    private MainController.RegisterWrite registerWrite = MainController.RegisterWrite.FALSE;

    @NotNull
    private MainController.MemoryToRegister memoryToRegister = MainController.MemoryToRegister.FROM_ALU_RESULT;

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

    @Override
    public boolean hasInstruction() {
        return exeMem.getRegisterWrite() == MainController.RegisterWrite.TRUE
                || exeMem.getMemoryWrite() == MainController.MemoryWrite.TRUE
                || exeMem.getMemoryRead() == MainController.MemoryRead.TRUE
                || exeMem.shouldBranch();
    }

    private void passProperties() {
        registerWrite = exeMem.getRegisterWrite();
        memoryToRegister = exeMem.getMemoryToRegister();
        aluResult = exeMem.getAluResult();
        writeRegisterAddress = exeMem.getWriteRegisterAddress();
    }

    private void accessMemory() {
        dataMemory.setAddress(exeMem.getAluResult());
        dataMemory.setMemoryRead(exeMem.getMemoryRead());
        dataMemory.setMemoryWrite(exeMem.getMemoryWrite());
        if (exeMem.getMemoryRead() == MainController.MemoryRead.TRUE)
            memoryReadData = dataMemory.read();
        else if (exeMem.getMemoryWrite() == MainController.MemoryWrite.TRUE)
            dataMemory.write(exeMem.getRegisterData2());
    }

    @NotNull
    public MainController.RegisterWrite getRegisterWrite() {
        return registerWrite;
    }

    @NotNull
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

    public Set<Integer> getWrittenDataMemoryAddresses() {
        return dataMemory.getWrittenAddresses();
    }

    public int readDataMemory(int address) {
        dataMemory.setMemoryRead(MainController.MemoryRead.TRUE);
        dataMemory.setAddress(address);
        int data = dataMemory.read();
        dataMemory.setMemoryRead(MainController.MemoryRead.FALSE);
        return data;
    }
}
