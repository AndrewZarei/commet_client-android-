package com.solana.actions

import com.solana.ComputeBudgetProgram
import com.solana.api.getTime
import com.solana.core.Account
import com.solana.core.PublicKey
import com.solana.core.Transaction
import com.solana.customConfig.CustomProgramId
import com.solana.models.buffer.MessageModel
import com.solana.programs.TokenProgram
import com.syntifi.near.borshj.Borsh
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

fun Action.sendMessage(
    conversationId: PublicKey,
    acc: Account,
    message: MessageModel,
    onComplete: ((Result<Pair<String, PublicKey>>) -> Unit),
) {
    api.getTime {
        try {
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            df.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
            val date = Date()
            date.time = JSONObject(it).getLong("unixtime") * 1000;
            val nowAsISO = df.format(date)
            message.time = nowAsISO;
        } catch (e: JSONException) { }

        val transaction = Transaction()
        val programId = PublicKey(CustomProgramId.getConversationProgramId());
        val messageByte = Borsh.serialize(message)
        val finalData = ByteArray(1 + messageByte.size)
        finalData[0] = 2
        System.arraycopy(messageByte, 0, finalData, 1, messageByte.size)
        val initializeAccountInstruction = TokenProgram.initializeAccountWithSeed(
            programId = programId,
            data = finalData,
            conversationId = conversationId
        )
        val modifyComputeUnits = ComputeBudgetProgram.setComputeUnitLimit(1400000);
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
    }
}