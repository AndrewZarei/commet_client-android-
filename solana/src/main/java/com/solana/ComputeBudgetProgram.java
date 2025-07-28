package com.solana;

import com.solana.core.PublicKey;
import com.solana.core.TransactionInstruction;
import com.solana.programs.Program;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;

public class ComputeBudgetProgram extends Program {

    private static final PublicKey PROGRAM_ID =
            new PublicKey("ComputeBudget111111111111111111111111111111");

    public static TransactionInstruction setComputeUnitPrice(int microLamports) {
        byte[] transactionData = encodeSetComputeUnitPriceTransaction(
                microLamports
        );

        return createTransactionInstruction(
                PROGRAM_ID,
                Collections.emptyList(),
                transactionData
        );
    }

    public static TransactionInstruction setComputeUnitLimit(int units) {
        byte[] transactionData = encodeSetComputeUnitLimitTransaction(
                units
        );

        return createTransactionInstruction(
                PROGRAM_ID,
                Collections.emptyList(),
                transactionData
        );
    }

    private static byte[] encodeSetComputeUnitPriceTransaction(int microLamports) {
        ByteBuffer result = ByteBuffer.allocate(9);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put(0, (byte) 0x03);
        result.putLong(1, microLamports);

        return result.array();
    }

    private static byte[] encodeSetComputeUnitLimitTransaction(int units) {
        ByteBuffer result = ByteBuffer.allocate(5);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put(0, (byte) 0x02);
        result.putInt(1, units);

        return result.array();
    }

}

