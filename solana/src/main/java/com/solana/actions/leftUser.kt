package com.solana.actions

import com.solana.customConfig.CustomProgramId
import com.solana.core.Account
import com.solana.core.AccountMeta
import com.solana.core.PublicKey
import com.solana.core.Transaction
import com.solana.programs.TokenProgram

fun Action.leftUser(
    acc: Account,
    conversationId: PublicKey,
    userId: PublicKey,
    onComplete: ((Result<Pair<String, PublicKey>>) -> Unit),
) {

    val transaction = Transaction()
    val programId = PublicKey(CustomProgramId.getConversationProgramId());
    val keys: MutableList<AccountMeta> = java.util.ArrayList()
        keys.add(AccountMeta(conversationId, false, true))
        keys.add(AccountMeta(userId, false, true))
    val finalData = ByteArray(1)
    finalData[0] = 3
    val initializeAccountInstruction = TokenProgram.initializeAccountWithSeed(
        programId = programId,
        data = finalData,
        keys = keys
    )
    transaction.setFeePayer(acc.publicKey)
    transaction.addInstruction(initializeAccountInstruction)
    this.serializeAndSendWithFee(transaction, listOf(acc), null) { result ->
        result.onSuccess { transactionId ->
            onComplete(Result.success(Pair(transactionId, acc.publicKey)))
        }.onFailure { error ->
            onComplete(Result.failure(error))
        }
    }


}