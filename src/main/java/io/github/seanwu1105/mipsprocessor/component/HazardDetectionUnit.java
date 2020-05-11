package io.github.seanwu1105.mipsprocessor.component;

import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionDecodeToExecutionRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionFetchToInstructionDecodeRegister;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import org.jetbrains.annotations.NotNull;

public class HazardDetectionUnit {

    @NotNull
    private final InstructionFetchToInstructionDecodeRegister ifId;
    @NotNull
    private final InstructionDecodeToExecutionRegister idExe;

    public HazardDetectionUnit(
            @NotNull final InstructionFetchToInstructionDecodeRegister ifId,
            @NotNull final InstructionDecodeToExecutionRegister idExe
    ) {
        this.ifId = ifId;
        this.idExe = idExe;
    }

    public boolean needStalling() {
        final var fetchedInstruction = ifId.getInstruction();
        return idExe.getMemoryRead() == MainController.MemoryRead.TRUE
                && (idExe.getRt() == fetchedInstruction.getRs() || idExe.getRt() == fetchedInstruction.getRt());
    }
}
