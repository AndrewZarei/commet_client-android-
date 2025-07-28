package com.solana.actions

import com.solana.customConfig.CustomProgramId
import com.solana.core.Account
import com.solana.core.AccountMeta
import com.solana.core.PublicKey
import com.solana.core.Transaction
import com.solana.models.buffer.ConversationItemModel
import com.solana.models.buffer.UserModel
import com.solana.programs.TokenProgram
import com.syntifi.near.borshj.Borsh

fun Action.addConversation(
    conversationId: PublicKey,
    acc: Account,
    conversationName: String,
    members: List<UserModel>,
    indexProfile : String,
    onComplete: ((Result<Pair<String, PublicKey>>) -> Unit),
) {
    val transaction = Transaction()
    val programId = PublicKey(CustomProgramId.getProfileProgramId())
    val conversationItemModel =Borsh.serialize(ConversationItemModel(conversationId.toBase58(), conversationName, indexProfile, false))
    val keys: MutableList<AccountMeta> = java.util.ArrayList()
    for (member in members) {
        keys.add(AccountMeta(PublicKey(member.user_address), false, true))
    }
    val finalData = ByteArray(1 + conversationItemModel.size)
    finalData[0] = 1
    System.arraycopy(conversationItemModel, 0, finalData, 1, conversationItemModel.size)
    val initializeAccountInstruction = TokenProgram.initializeAccountWithSeed(
        programId = programId,
        data = finalData,
        keys = keys
    )
    transaction.setFeePayer(acc.publicKey)
    transaction.addInstruction(initializeAccountInstruction)
    this.serializeAndSendWithFee(transaction, listOf(acc), null) { result ->
        result.onSuccess { transactionId ->
            onComplete(Result.success(Pair(transactionId, conversationId)))
        }.onFailure { error ->
            onComplete(Result.failure(error))
        }
    }
}