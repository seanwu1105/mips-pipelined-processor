package component;

import component.pipeline.ExecutionToMemoryAccessRegister;
import component.pipeline.InstructionDecodeToExecutionRegister;
import component.pipeline.MemoryAccessToWriteBackRegister;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import signal.Signal;

public class ForwardingUnit {

    @NotNull
    private final InstructionDecodeToExecutionRegister idExe;
    @NotNull
    private final ExecutionToMemoryAccessRegister exeMem;
    @NotNull
    private final MemoryAccessToWriteBackRegister memWb;

    public ForwardingUnit(
            @NotNull final InstructionDecodeToExecutionRegister idExe,
            @NotNull final ExecutionToMemoryAccessRegister exeMem,
            @NotNull final MemoryAccessToWriteBackRegister memWb
    ) {
        this.idExe = idExe;
        this.exeMem = exeMem;
        this.memWb = memWb;
    }

    @NotNull
    public ForwardingSignal getOperand1ForwardingSignal() {
        if (isExeHazard(exeMem, idExe.getRs())) return ForwardingSignal.FROM_EXE;
        else if (isMemHazard(memWb, idExe.getRs())) return ForwardingSignal.FROM_MEM;
        return ForwardingSignal.FROM_ID;
    }

    @NotNull
    public ForwardingSignal getOperand2ForwardingSignal() {
        if (isExeHazard(exeMem, idExe.getRt())) return ForwardingSignal.FROM_EXE;
        else if (isMemHazard(memWb, idExe.getRt())) return ForwardingSignal.FROM_MEM;
        return ForwardingSignal.FROM_ID;
    }

    private boolean isExeHazard(
            @NotNull final ExecutionToMemoryAccessRegister exeMem,
            final int operandAddress
    ) {
        return exeMem.getRegisterWrite() == MainController.RegisterWrite.TRUE
                && exeMem.getWriteRegisterAddress() == operandAddress;
    }

    private boolean isMemHazard(
            @NotNull final MemoryAccessToWriteBackRegister memWb,
            final int operandAddress
    ) {
        return memWb.getRegisterWrite() == MainController.RegisterWrite.TRUE
                && memWb.getWriteRegisterAddress() == operandAddress;
    }

    public enum ForwardingSignal implements Signal {
        FROM_ID("00"),
        FROM_MEM("01"),
        FROM_EXE("10");

        @NotNull
        private final String raw;

        ForwardingSignal(@NotNull final String raw) {
            this.raw = raw;
        }

        @NotNull
        @Override
        public String getRaw() {
            return raw;
        }
    }
}
