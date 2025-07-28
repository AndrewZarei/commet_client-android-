package com.solana.actions


import com.solana.customConfig.CustomProgramId
import com.solana.core.Account
import com.solana.core.PublicKey
import com.solana.core.Transaction
import com.solana.models.buffer.UserModel
import com.solana.programs.SystemProgram
import com.solana.programs.TokenProgram
import com.syntifi.near.borshj.Borsh
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Action.addMember(
    acc: Account,
    conversationId: PublicKey,
    user: UserModel,
    onComplete: ((Result<Pair<String, PublicKey>>) -> Unit),
) {
    val transaction = Transaction()
    val programId = PublicKey(CustomProgramId.getConversationProgramId());
    val userByte = Borsh.serialize(user)
    val finalData = ByteArray(1 + userByte.size)
    finalData[0] = 1
    System.arraycopy(userByte, 0, finalData, 1, userByte.size)
    val initializeAccountInstruction = TokenProgram.initializeAccountWithSeed(
        programId = programId,
        data = finalData,
        conversationId = conversationId
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