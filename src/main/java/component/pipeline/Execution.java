package component.pipeline;

import component.Alu;
import component.ForwardingUnit;
import controller.AluController;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import signal.FunctionCode;

import java.util.Objects;

public class Execution implements Stage {

    @NotNull
    private final InstructionDecodeToExecutionRegister idExe;

    @NotNull
    private final Alu alu;

    @NotNull
    private final Alu branchAdder;

    @Nullable
    private ForwardingUnit forwardingUnit;

    @Nullable
    private ExecutionToMemoryAccessRegister exeMem;

    @Nullable
    private MemoryAccessToWriteBackRegister memWb;

    @NotNull
    private MainController.RegisterWrite registerWrite = MainController.RegisterWrite.FALSE;

    @NotNull
    private MainController.MemoryToRegister memoryToRegister = MainController.MemoryToRegister.FROM_ALU_RESULT;

    @NotNull
    private MainController.Branch branch = MainController.Branch.FALSE;

    @NotNull
    private MainController.MemoryRead memoryRead = MainController.MemoryRead.FALSE;

    @NotNull
    private MainController.MemoryWrite memoryWrite = MainController.MemoryWrite.FALSE;

    private int registerData2, writeRegisterAddress;

    public Execution(
            @NotNull final InstructionDecodeToExecutionRegister idExe,
            @NotNull final Alu alu,
            @NotNull final Alu branchAdder
    ) {
        this.idExe = idExe;
        this.alu = alu;
        this.branchAdder = branchAdder;
        branchAdder.setControl(Alu.AluControl.ADD);
    }

    @NotNull
    public MainController.RegisterWrite getRegisterWrite() {
        return registerWrite;
    }

    @NotNull
    public MainController.MemoryToRegister getMemoryToRegister() {
        return memoryToRegister;
    }

    @NotNull
    public MainController.Branch getBranch() {
        return branch;
    }

    @NotNull
    public MainController.MemoryRead getMemoryRead() {
        return memoryRead;
    }

    @NotNull
    public MainController.MemoryWrite getMemoryWrite() {
        return memoryWrite;
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

    public void setForwardingUnit(@NotNull final ForwardingUnit forwardingUnit) {
        this.forwardingUnit = forwardingUnit;
    }

    public void setExecutionToMemoryAccessRegister(@NotNull final ExecutionToMemoryAccessRegister exeMem) {
        this.exeMem = exeMem;
    }

    public void setMemoryAccessToWriteBackRegister(@NotNull final MemoryAccessToWriteBackRegister memWb) {
        this.memWb = memWb;
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

    @Override
    public boolean hasInstruction() {
        return idExe.getFunctionCode() != FunctionCode.NOP;
    }

    private void passControlSignals() {
        registerWrite = idExe.getRegisterWrite();
        memoryToRegister = idExe.getMemoryToRegister();
        branch = idExe.getBranch();
        memoryRead = idExe.getMemoryRead();
        memoryWrite = idExe.getMemoryWrite();
    }

    private void configAlu() {
        alu.setControl(AluController.getAluControl(idExe.getAluOperation(), idExe.getFunctionCode()));
        resolveOperand1();
        resolveOperand2();
    }

    private void resolveOperand1() {
        assert forwardingUnit != null;
        if (forwardingUnit.getOperand1ForwardingSignal() == ForwardingUnit.ForwardingSignal.FROM_EXE)
            alu.setOperand1(Objects.requireNonNull(exeMem).getAluResult());
        else if (forwardingUnit.getOperand1ForwardingSignal() == ForwardingUnit.ForwardingSignal.FROM_MEM)
            alu.setOperand1(Objects.requireNonNull(memWb).getWriteRegisterData());
        else
            alu.setOperand1(idExe.getRegisterData1());
    }

    private void resolveOperand2() {
        if (idExe.getAluSource() == MainController.AluSource.REGISTER) {
            assert forwardingUnit != null;
            if (forwardingUnit.getOperand2ForwardingSignal() == ForwardingUnit.ForwardingSignal.FROM_EXE)
                alu.setOperand2(Objects.requireNonNull(exeMem).getAluResult());
            else if (forwardingUnit.getOperand2ForwardingSignal() == ForwardingUnit.ForwardingSignal.FROM_MEM)
                alu.setOperand2(Objects.requireNonNull(memWb).getWriteRegisterData());
            else
                alu.setOperand2(idExe.getRegisterData2());
        } else alu.setOperand2(idExe.getImmediate());
    }

    private void configBranchAdder() {
        branchAdder.setOperand1(idExe.getProgramCounter());
        branchAdder.setOperand2(idExe.getImmediate() * 4);
    }
}
