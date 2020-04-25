package component.pipeline;

import org.jetbrains.annotations.NotNull;
import signal.Instruction;

public class InstructionFetch implements Stage {

    public int getNewProgramCounter() {
        return 0;
    }

    @NotNull
    public Instruction getInstruction() {
        return new Instruction("00000000000000000000000000000000");
    }

    @Override
    public void run() {

    }
}
