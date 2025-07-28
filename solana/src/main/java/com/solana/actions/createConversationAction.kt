package com.solana.actions

import com.solana.ComputeBudgetProgram
import com.solana.api.getMinimumBalanceForRentExemption
import com.solana.core.Account
import com.solana.core.PublicKey
import com.solana.core.Transaction
import com.solana.customConfig.CustomProgramId
import com.solana.models.buffer.ConversationModel
import com.solana.models.buffer.UserModel
import com.solana.programs.SystemProgram
import com.solana.programs.TokenProgram
import com.syntifi.near.borshj.Borsh
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

fun Action.createConversationAction(
    accountId: PublicKey,
    acc: Account,
    conversationName: String,
    members: List<UserModel>,
    is_private: Boolean,
    onComplete: ((Result<Pair<String, PublicKey>>) -> Unit),
) {
    api.getMinimumBalanceForRentExemption(80000) {
        it.onSuccess { balance ->
            val transaction = Transaction()
            val programId = PublicKey(CustomProgramId.getConversationProgramId())
            val conversationId = PublicKey.createWithSeed(acc.publicKey, conversationName, programId)
            val createAccountInstruction = SystemProgram.createAccountWithSeed(
                fromPublicKey = acc.publicKey,
                basePublicKey = acc.publicKey,
                seed = conversationName,
                newAccountPublickey = conversationId,
                lamports = balance,
                space = 80000,
                programId = programId
            )
            transaction.addInstruction(createAccountInstruction)
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            df.timeZone = TimeZone.getTimeZone("GMT+3:30")
            val nowAsISO: String = df.format(Date())
            val conversation = Borsh.serialize(ConversationModel(conversationName, nowAsISO, ArrayList(), members, members.get(0),is_private))
            val finalData = ByteArray(1 + conversation.size)
            System.arraycopy(conversation, 0, finalData, 1, conversation.size)
            val initializeAccountInstruction = TokenProgram.initializeAccountWithSeed(
                conversationId = conversationId,
                programId = programId,
                data = finalData
            )
            val modifyComputeUnits = ComputeBudgetProgram.setComputeUnitLimit(1400000)
            val addPriorityFee = ComputeBudgetProgram.setComputeUnitPrice(1);
            transaction.setFeePayer(acc.publicKey)
            transaction.addInstruction(modifyComputeUnits)
            transaction.addInstruction(addPriorityFee)
            transaction.addInstruction(initializeAccountInstruction)
            this.serializeAndSendWithFee(transaction, listOf(acc), null) { result ->
                result.onSuccess { transactionId ->
                    onComplete(Result.success(Pair(transactionId, conversationId)))
                }.onFailure { error ->
                    onComplete(Result.failure(error))
                }
            }
        }.onFailure {
            onComplete(Result.failure(it))
        }
    }
}