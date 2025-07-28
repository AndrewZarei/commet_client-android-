package com.solana.api

import com.solana.core.Account
import com.solana.core.Transaction
import com.solana.models.RpcSendTransactionConfig
import java.util.Base64

fun Api.sendTransaction(
    transaction: Transaction,
    signers: List<Account>,
    onComplete: ((Result<String>) -> Unit)
) {
        getRecentBlockhash { result ->
            result.map { recentBlockHash ->
                transaction.setRecentBlockHash(recentBlockHash)
                transaction.sign(signers)
                val serializedTransaction: ByteArray = transaction.serialize()
                val base64Trx: String = Base64.getEncoder().encodeToString(serializedTransaction)
                listOf(base64Trx, RpcSendTransactionConfig())
            }.onSuccess {
                router.request("sendTransaction", it, String::class.java, onComplete)
            }.onFailure {
                onComplete(Result.failure(RuntimeException(it)))
            }
        }
}