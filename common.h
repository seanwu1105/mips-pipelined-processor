#ifndef COMMON_H_INCLUDED
#define COMMON_H_INCLUDED

using namespace std;

/// Variables Declaration
extern vector<string> instruction_set;
extern vector<int> registers;
extern vector<int> memory;
extern bool isIF;
extern string no_instruction;

///Pipeline Registers
extern struct IFID
{
    unsigned int Write;
    unsigned int PC;
    string instruction;
}ifid;

extern struct IDEX
{
    int ReadData1;
    int ReadData2;
    string sign_ext;
    int Rs;
    int Rt;
    int Rd;
    struct
    {
        unsigned int RegDst;
        struct
        {
            unsigned int op1;
            unsigned int op0;
        }ALUOp;
        unsigned int ALUSrc;
        unsigned int Branch;
        unsigned int MemRead;
        unsigned int MemWrite;
        unsigned int RegWrite;
        unsigned int MemToReg;
    }control;
}idex;

extern struct EXMEM
{
    int ALUout;
    int WriteData;
    int RtRd;  // index of written-back register
    struct
    {
        unsigned int Branch;
        unsigned int MemRead;
        unsigned int MemWrite;
        unsigned int RegWrite;
        unsigned int MemToReg;
    }control;
}exmem;

extern struct MEMWB
{
    int ReadData;
    int ALUout;
    int RtRd;  // index of written-back register and comparison for data hazard
    struct
    {
        unsigned int RegWrite;
        unsigned int MemToReg;
    }control;
}memwb, memwbTemp;  // !important: "memwbTemp" for MEM data hazard,
                    // save the memwb member to detect hazard.
                    // Since the pipeline execute "backwardly",
                    // It'll lost the earliest instruction memwb as
                    // the next one override it (memwb).

///Function Prototype
void IF(string);
int ID(bool);
// Return value identify the stall signal. Parameter check whether the IF stage has been executed

void EX(void);
void MEM(void);
void WB(void);

#endif // COMMON_H_INCLUDED
