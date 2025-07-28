package com.solana.actions

import android.util.Log
import com.solana.core.Account
import com.solana.core.PublicKey
import com.solana.core.Transaction
import com.solana.customConfig.CustomProgramId
import com.solana.programs.TokenProgram
import org.sol4k.PublicKey as Sol4kPublicKey

fun Action.addContact(
    acc: Account,
    accountId: PublicKey,
    userName: String,
    avatar: String,
    accountId1: PublicKey,
    onComplete: ((Result<Pair<String, PublicKey>>) -> Unit),
) {
    val transaction = Transaction()
    val programId = PublicKey(CustomProgramId.getContactProgramId())
    val initializeAccountInstruction = TokenProgram.addContact(
        accountId = accountId,
        accountId1 = accountId1,
        programId = programId,
        username = userName,
        avatar = avatar,
    )
    transaction.setFeePayer(acc.publicKey)
    transaction.addInstruction(initializeAccountInstruction)
    this.serializeAndSendWithFee(transaction, listOf(acc), null) { result ->
        result.onSuccess { transactionId ->
            onComplete(Result.success(Pair(transactionId, acc.publicKey)))
        }.onFailure { error ->
            Log.e("erfan", error.message.toString())
            onComplete(Result.failure(error))
        }
    }
}