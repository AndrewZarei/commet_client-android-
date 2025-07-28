package com.solana.core

import com.solana.vendor.ShortvecEncoding
import com.solana.vendor.TweetNaclFast
import org.bitcoinj.core.Base58
import java.nio.ByteBuffer
import java.util.*

class Transaction {
    private val message: Message = Message()
    private val signatures: MutableList<String> = ArrayList()
    private lateinit var serializedMessage: ByteArray
    fun addInstruction(instruction: TransactionInstruction): Transaction {
        message.addInstruction(instruction)
        return this
    }

    fun setRecentBlockHash(recentBlockhash: String) {
        message.setRecentBlockHash(recentBlockhash)
    }

    fun setFeePayer(feePayer: PublicKey?) {
        message.feePayer = feePayer
    }

    fun sign(signer: Account) {
        sign(listOf(signer))
    }

    fun sign2(signers: List<Account>) {
        require(signers.size != 0) { "No signers" }
        message.feePayer ?: let { message.feePayer = PublicKey("ExA2JMUhRBsnoPViTFqzsmZr4gEL8at3vcsJqeKgwSEi") }
        serializedMessage = message.serialize()
        val signatureProvider = TweetNaclFast.Signature(ByteArray(0), signers[0].secretKey)
        val signature = signatureProvider.detached(serializedMessage)
        signatures.add(Base58.encode(signature))

    }

    fun sign(signers: List<Account>) {
        require(signers.size != 0) { "No signers" }
        message.feePayer ?: let { message.feePayer = signers[0].publicKey }
        serializedMessage = message.serialize()
        for (signer in signers) {
            val signatureProvider = TweetNaclFast.Signature(ByteArray(0), signer.secretKey)
            val signature = signatureProvider.detached(serializedMessage)
            signatures.add(Base58.encode(signature))
        }
    }

    fun serialize(): ByteArray {
        val signaturesSize = signatures.size
        val signaturesLength = ShortvecEncoding.encodeLength(signaturesSize)
        val out = ByteBuffer
            .allocate(signaturesLength.size + signaturesSize * SIGNATURE_LENGTH + serializedMessage.size)
        out.put(signaturesLength)
        for (signature in signatures) {
            val rawSignature = Base58.decode(signature)
            out.put(rawSignature)
        }
        out.put(serializedMessage)
        return out.array()
    }

    fun serialize2(): ByteArray {
        val signaturesSize = signatures.size
        val signaturesLength = ShortvecEncoding.encodeLength(signaturesSize)
        val out = ByteBuffer
            .allocate(signaturesLength.size + signaturesSize * SIGNATURE_LENGTH + serializedMessage.size)
        out.put(signaturesLength)
        for (signature in signatures) {
            out.put(convertJsonStringToByteArray("[63,135,245,165,113,65,157,236,184,135,179,93,198,61,195,241,12,72,146,154,13,89,160,49,189,135,79,13,118,92,170,83,16,89,249,116,165,133,186,114,219,181,252,21,6,93,70,172,35,222,15,101,37,130,122,155,39,7,117,37,27,235,118,14]"))
        }
        out.put(serializedMessage)
        return out.array()
    }
    private fun convertJsonStringToByteArray(characters: String): ByteArray {
        // Create resulting byte array
        val buffer = ByteBuffer.allocate(64)

        // Convert json array into String array
        val sanitizedJson = characters.replace("\\[".toRegex(), "").replace("]".toRegex(), "")
        val chars = sanitizedJson.split(",").toTypedArray()

        // Convert each String character into byte and put it in the buffer
        Arrays.stream(chars).forEach { character: String ->
            val byteValue = character.trim().toInt().toByte()
            buffer.put(byteValue)
        }
        return buffer.array()
    }
    override fun toString(): String {
        return """Transaction(
            |  signatures: [${signatures.joinToString()}],
            |  message: ${message}
        |)""".trimMargin()
    }

    companion object {
        const val SIGNATURE_LENGTH = 64
    }
}
