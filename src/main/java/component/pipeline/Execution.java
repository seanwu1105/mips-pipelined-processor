package component.pipeline;

import component.Alu;
import controller.AluController;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Execution implements Stage {

    @NotNull
    private final InstructionDecodeToExecutionRegister idExe;

    @NotNull
    private final Alu alu;

    @NotNull
    private final Alu branchAdder;

    @Nullable
    private MainController.RegisterWrite registerWrite;

    @Nullable
    private MainController.MemoryToRegister memoryToRegister;

    @Nullable
    private MainController.Branch branch;

    @Nullable
    private MainController.MemoryRead memoryRead;

    @Nullable
    private MainController.MemoryWrite memoryWrite;

    private int registerData2, writeRegisterAddress;

    public Execution(
            @NotNull InstructionDecodeToExecutionRegister idExe,
            @NotNull Alu alu,
            @NotNull Alu branchAdder
    ) {
        this.idExe = idExe;
        this.alu = alu;
        this.branchAdder = branchAdder;
        branchAdder.setControl(Alu.AluControl.ADD);
    }

    @NotNull
    public MainController.RegisterWrite getRegisterWrite() {
        return Objects.requireNonNull(registerWrite);
    }

    @NotNull
    public MainController.MemoryToRegister getMemoryToRegister() {
        return Objects.requireNonNull(memoryToRegister);
    }

    @NotNull
    public MainController.Branch getBranch() {
        return Objects.requireNonNull(branch);
    }

    @NotNull
    public MainController.MemoryRead getMemoryRead() {
        return Objects.requireNonNull(memoryRead);
    }

    @NotNull
    public MainController.MemoryWrite getMemoryWrite() {
        return Objects.requireNonNull(memoryWrite);
    }

    public int getBranchResult() {
        return branchAdder.getResult();
    }

    public int getAluResult() {
        return alu.getResult();
    }

    public int getRegisterData2() {
        return registerData2;
    }

    public int getWriteRegisterAddress() {
        return writeRegisterAddress;
    }

    @Override
    public void run() {
        passControlSignals();
        configAlu();
        configBranchAdder();
        registerData2 = idExe.getRegisterData2();
        if (idExe.getRegisterDestination() == MainController.RegisterDestination.RT)
            writeRegisterAddress = idExe.getRt();
        else writeRegisterAddress = idExe.getRd();
    }

    private void passControlSignals() {
        registerWrite = idExe.getRegisterWrite();
        memoryToRegister = idExe.getMemoryToRegister();
        branch = idExe.getBranch();
        memoryRead = idExe.getMemoryRead();
        memoryWrite = idExe.getMemoryWrite();
    }

    private void configAlu() {
        MainController.AluOperation aluOperation = Objects.requireNonNull(idExe.getAluOperation());
        alu.setControl(AluController.getAluControl(aluOperation, idExe.getFunctionCode()));
        alu.setOperand1(idExe.getRegisterData1());

        if (idExe.getAluSource() == MainController.AluSource.REGISTER)
            alu.setOperand2(idExe.getRegisterData2());
        else alu.setOperand2(idExe.getImmediate());
    }

    private void configBranchAdder() {
        branchAdder.setOperand1(idExe.getProgramCounter());
        branchAdder.setOperand2(idExe.getImmediate() * 4);
    }
}
