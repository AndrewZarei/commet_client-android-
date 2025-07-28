package com.solana.actions

import com.solana.api.getMinimumBalanceForRentExemption
import com.solana.core.Account
import com.solana.core.PublicKey
import com.solana.core.Transaction
import com.solana.programs.SystemProgram
import com.solana.programs.TokenProgram

fun Action.createAccountWithSeed(
    acc: Account,
    accountId: PublicKey,
    programId: PublicKey,
    userName: String,
    indexProfile: String,
    onComplete: ((Result<Pair<String, PublicKey>>) -> Unit)
) {
    api.getMinimumBalanceForRentExemption(6000) {
        it.onSuccess { balance ->
            val transaction = Transaction()
            val createAccountInstruction = SystemProgram.createAccountWithSeed(
                fromPublicKey = acc.publicKey,
                basePublicKey = acc.publicKey,
                seed = userName,
                newAccountPublickey = accountId,
                lamports = balance,
                space = 6000,
                programId =programId
            )
            transaction.addInstruction(createAccountInstruction)
            val initializeAccountInstruction = TokenProgram.initializeAccountWithSeed(
                accountId = accountId,
                programId = programId,
                username = userName,
                indexProfile = indexProfile
            )
            transaction.setFeePayer(acc.publicKey)
            transaction.addInstruction(initializeAccountInstruction)
            this.serializeAndSendWithFee(transaction, listOf(acc), null) { result ->
                result.onSuccess { transactionId ->
                    onComplete(Result.success(Pair(transactionId, accountId)))
                }.onFailure { error ->
                    onComplete(Result.failure(error))
                }
            }
        }.onFailure {
            onComplete(Result.failure(it))
        }
    }
}