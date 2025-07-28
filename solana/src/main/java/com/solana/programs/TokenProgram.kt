package com.solana.programs

import com.solana.core.AccountMeta
import com.solana.core.PublicKey
import com.solana.core.TransactionInstruction
import com.solana.customConfig.CustomContactPda
import org.bitcoinj.core.Utils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * Class for creating Token Program [TransactionInstruction]s
 */
object TokenProgram : Program() {
    val PROGRAM_ID = PublicKey("GrPoQLvDroBp75ij21X19vVGGgyitJmBxd1CGj2wUURX")
    val SYSVAR_RENT_PUBKEY = PublicKey("SysvarRent111111111111111111111111111111111")
    private const val INITIALIZE_METHOD_ID = 1
    private const val TRANSFER_METHOD_ID = 3
    private const val CLOSE_ACCOUNT_METHOD_ID = 9
    private const val TRANSFER_CHECKED_METHOD_ID = 12

    /**
     * Transfers an SPL token from the owner's source account to destination account.
     * Destination pubkey must be the Token Account (created by token mint), and not the main SOL address.
     * @param source SPL token wallet funding this transaction
     * @param destination Destined SPL token wallet
     * @param amount 64 bit amount of tokens to send
     * @param owner account/private key signing this transaction
     * @return transaction id for explorer
     */
    fun transfer(
        source: PublicKey,
        destination: PublicKey,
        amount: Long,
        owner: PublicKey,
    ): TransactionInstruction {
        val keys: MutableList<AccountMeta> = ArrayList()
        keys.add(AccountMeta(source, false, true))
        keys.add(AccountMeta(destination, false, true))
        keys.add(AccountMeta(owner, true, false))
        val transactionData = encodeTransferTokenInstructionData(
            amount
        )
        return createTransactionInstruction(
            PROGRAM_ID,
            keys,
            transactionData
        )
    }

    fun transferChecked(
        source: PublicKey,
        destination: PublicKey,
        amount: Long,
        decimals: Byte,
        owner: PublicKey,
        tokenMint: PublicKey,
    ): TransactionInstruction {
        val keys: MutableList<AccountMeta> = ArrayList()
        keys.add(AccountMeta(source, false, true))
        // index 1 = token mint (https://docs.rs/spl-token/3.1.0/spl_token/instruction/enum.TokenInstruction.html#variant.TransferChecked)
        keys.add(AccountMeta(tokenMint, false, false))
        keys.add(AccountMeta(destination, false, true))
        keys.add(AccountMeta(owner, true, false))
        val transactionData = encodeTransferCheckedTokenInstructionData(
            amount,
            decimals
        )
        return createTransactionInstruction(
            PROGRAM_ID,
            keys,
            transactionData
        )
    }

    fun initializeAccount(
        account: PublicKey,
        mint: PublicKey,
        owner: PublicKey,
    ): TransactionInstruction {
        val keys: MutableList<AccountMeta> = ArrayList()
        keys.add(AccountMeta(account, false, true))
        keys.add(AccountMeta(mint, false, false))
        keys.add(AccountMeta(owner, false, true))
        keys.add(AccountMeta(SYSVAR_RENT_PUBKEY, false, false))
        val buffer = ByteBuffer.allocate(1)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.put(INITIALIZE_METHOD_ID.toByte())
        return createTransactionInstruction(
            PROGRAM_ID,
            keys,
            buffer.array()
        )
    }

    fun addContact(
        accountId: PublicKey,
        accountId1: PublicKey,
        programId: PublicKey,
        username: String,
        avatar: String,
    ): TransactionInstruction {
        val keys: MutableList<AccountMeta> = ArrayList()
        keys.add(AccountMeta(PublicKey(CustomContactPda.getContactPda()[1]),
            false,
            true))
        var testusername = "a"
        var testlastname = "b"
        var basePubKey = accountId
         val data =
             ByteArray(5 + username.toByteArray().size +
                     testlastname.toByteArray().size + 4 +
                     accountId.toBase58().toByteArray().size + 4 +
                     accountId1.toBase58().toByteArray().size + 4 +
                     avatar.toByteArray().size + 4
             )
        //Username
         Utils.uint32ToByteArrayLE(username.length.toLong(), data, 1)
         System.arraycopy(username.toByteArray(), 0, data, 4 + 1, username.toByteArray().size)

        //LastName
        Utils.uint32ToByteArrayLE(testlastname.length.toLong(), data, username.toByteArray().size + 4 + 1)
         System.arraycopy(testlastname.toByteArray(), 0, data, username.toByteArray().size + 4 + 1 + 4, testlastname.toByteArray().size)

        //publickey
        data[ username.toByteArray().size + 4 + 1 +
                testlastname.toByteArray().size + 4
        ] = 44
        System.arraycopy(accountId.toBase58().toByteArray(),
            0,
            data,
            username.toByteArray().size + 4 + 1 + testlastname.toByteArray().size + 8,
            accountId.toBase58().toByteArray().size)

        //basePubKey
         data[username.toByteArray().size + 4 + 1 + testlastname.toByteArray().size + 8 + accountId1.toBase58().toByteArray().size] = 44
         System.arraycopy(accountId1.toBase58().toByteArray(), 0, data, username.toByteArray().size + 4 + 1 + testlastname.toByteArray().size + 4 + accountId.toBase58().toByteArray().size + 8, accountId1.toBase58().toByteArray().size)
         Utils.uint32ToByteArrayLE(avatar.length.toLong(), data, 1 + 4 +username.toByteArray().size + testlastname.toByteArray().size + 4 + 44 + 4 + 44 + 4)
         System.arraycopy(avatar.toByteArray(), 0, data, 1 + 1 + 4 +username.toByteArray().size + testlastname.toByteArray().size + 4 + 44 + 4 + 44 + 4 + 3, avatar.toByteArray().size)

        return createTransactionInstruction(
            programId,
            keys,
            data
        )
    }

    fun initializeAccountWithSeed(
        accountId: PublicKey,
        programId: PublicKey,
        username: String,
        indexProfile: String
    ): TransactionInstruction {
        val keys: MutableList<AccountMeta> = ArrayList()
        keys.add(AccountMeta(accountId, false, true))
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        df.setTimeZone(TimeZone.getTimeZone("GMT+3:30"))
        val nowAsISO: String = df.format(Date())
        val index = indexProfile
        val data = ByteArray(5 + username.toByteArray().size + nowAsISO.toByteArray().size + 4 + 4 + index.toByteArray().size + 4)
        Utils.uint32ToByteArrayLE(username.length.toLong(), data, 1)
        System.arraycopy(username.toByteArray(), 0, data, 4 + 1, username.toByteArray().size)
        data[username.toByteArray().size + 4 + 1] = 24
        System.arraycopy(nowAsISO.toByteArray(),
            0,
            data,
            username.toByteArray().size + 8 + 1,
            nowAsISO.toByteArray().size)
        Utils.uint32ToByteArrayLE(index.length.toLong(), data, username.toByteArray().size + nowAsISO.toByteArray().size + 8 + 1)
        System.arraycopy(index.toByteArray(), 0, data,
            username.toByteArray().size + nowAsISO.toByteArray().size + 13, index.toByteArray().size)

        return createTransactionInstruction(
            programId,
            keys,
            data
        )
    }

    fun initializeAccountWithSeed(
        programId: PublicKey,
        data: ByteArray,
        keys: MutableList<AccountMeta>,
    ): TransactionInstruction {
        return createTransactionInstruction(
            programId,
            keys,
            data
        )
    }

    fun initializeAccountWithSeed(
        conversationId: PublicKey,
        programId: PublicKey,
        data: ByteArray,
    ): TransactionInstruction {
        val keys: MutableList<AccountMeta> = ArrayList()
        keys.add(AccountMeta(conversationId, false, true))
        return createTransactionInstruction(
            programId,
            keys,
            data
        )
    }

    fun closeAccount(
        account: PublicKey,
        destination: PublicKey,
        owner: PublicKey,
    ): TransactionInstruction {
        val keys: MutableList<AccountMeta> = ArrayList()
        keys.add(AccountMeta(account, false, true))
        keys.add(AccountMeta(destination, false, true))
        keys.add(AccountMeta(owner, false, false))
        val buffer = ByteBuffer.allocate(1)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.put(CLOSE_ACCOUNT_METHOD_ID.toByte())
        return createTransactionInstruction(
            PROGRAM_ID,
            keys,
            buffer.array()
        )
    }

    private fun encodeTransferTokenInstructionData(amount: Long): ByteArray {
        val result = ByteBuffer.allocate(9)
        result.order(ByteOrder.LITTLE_ENDIAN)
        result.put(TRANSFER_METHOD_ID.toByte())
        result.putLong(amount)
        return result.array()
    }

    private fun encodeTransferCheckedTokenInstructionData(amount: Long, decimals: Byte): ByteArray {
        val result = ByteBuffer.allocate(10)
        result.order(ByteOrder.LITTLE_ENDIAN)
        result.put(TRANSFER_CHECKED_METHOD_ID.toByte())
        result.putLong(amount)
        result.put(decimals)
        return result.array()
    }
}