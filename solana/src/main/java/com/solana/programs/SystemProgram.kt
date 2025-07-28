package com.solana.programs

import com.solana.core.AccountMeta
import com.solana.core.PublicKey
import com.solana.core.TransactionInstruction
import com.solana.models.buffer.DataLengthModel
import com.syntifi.near.borshj.Borsh
import org.bitcoinj.core.Utils

object SystemProgram : Program() {
    val PROGRAM_ID = PublicKey("11111111111111111111111111111111")
    const val PROGRAM_INDEX_CREATE_ACCOUNT = 0
    const val CreateWithSeed = 3
    const val PROGRAM_INDEX_TRANSFER = 2

    @JvmStatic
    fun transfer(
        fromPublicKey: PublicKey,
        toPublickKey: PublicKey,
        lamports: Long
    ): TransactionInstruction {
        val keys = ArrayList<AccountMeta>()
        keys.add(AccountMeta(fromPublicKey, true, true))
        keys.add(AccountMeta(toPublickKey, false, true))

        // 4 byte instruction index + 8 bytes lamports
        val data = ByteArray(4 + 8)
        Utils.uint32ToByteArrayLE(PROGRAM_INDEX_TRANSFER.toLong(), data, 0)
        Utils.int64ToByteArrayLE(lamports, data, 4)
        return createTransactionInstruction(PROGRAM_ID, keys, data)
    }

    fun createAccount(
        fromPublicKey: PublicKey, newAccountPublickey: PublicKey,
        lamports: Long, space: Long, programId: PublicKey
    ): TransactionInstruction {
        val keys = ArrayList<AccountMeta>()
        keys.add(AccountMeta(fromPublicKey, true, true))
        keys.add(AccountMeta(newAccountPublickey, true, true))
        val data = ByteArray(4 + 8 + 8 + 32)
        Utils.uint32ToByteArrayLE(PROGRAM_INDEX_CREATE_ACCOUNT.toLong(), data, 0)
        Utils.int64ToByteArrayLE(lamports, data, 4)
        Utils.int64ToByteArrayLE(space, data, 12)
        System.arraycopy(programId.toByteArray(), 0, data, 20, 32)
        return createTransactionInstruction(PROGRAM_ID, keys, data)
    }

    fun createAccountWithSeed(
        fromPublicKey: PublicKey,
        basePublicKey: PublicKey,
        seed: String,
        newAccountPublickey: PublicKey,
        lamports: Long,
        space: Long,
        programId: PublicKey
    ): TransactionInstruction {
        val keys = ArrayList<AccountMeta>()
        keys.add(AccountMeta(fromPublicKey, true, true))
        keys.add(AccountMeta(newAccountPublickey, false, true))
        keys.add(AccountMeta(basePublicKey, true, true))

        val data = ByteArray(4 + 32 + seed.toByteArray().size + 8 + 8 + 8 + 32)
        Utils.uint32ToByteArrayLE(CreateWithSeed.toLong(), data, 0)
        System.arraycopy(basePublicKey.toByteArray(), 0, data, 4, 32)
        System.arraycopy(Borsh.serialize(DataLengthModel(seed.length)), 0, data, 36, 4)
        System.arraycopy(seed.toByteArray(), 0, data, 36+8, seed.toByteArray().size)
        Utils.int64ToByteArrayLE(lamports, data, seed.toByteArray().size + 36+8)
        Utils.int64ToByteArrayLE(space, data, seed.toByteArray().size + 44+8)
        System.arraycopy(programId.toByteArray(), 0, data, seed.toByteArray().size + 52+8, 32)

        return createTransactionInstruction(PROGRAM_ID, keys, data)
    }

    private fun concat(bytes: ByteArray, str: String): CharArray? {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(b.toInt())
        }
        sb.append(str)
        return sb.toString().toCharArray()
    }
}