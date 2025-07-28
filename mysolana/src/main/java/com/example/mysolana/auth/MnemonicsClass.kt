package com.example.mysolana.auth

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.Mnemonics.WordCount
import cash.z.ecc.android.bip39.toSeed
import org.sol4k.Keypair
import java.util.Locale

object MnemonicsClass {
    val mnemonics: Mnemonics.MnemonicCode
        get() = Mnemonics.MnemonicCode(WordCount.COUNT_12, Locale.ENGLISH.language)

    fun getKeypair(mnemonicCode: Mnemonics.MnemonicCode): Keypair {
        return Keypair.fromSecretKey(mnemonicCode.toSeed())
    }

    fun convertToKeypair(phrase: String): Keypair {
        return Keypair.fromSecretKey(Mnemonics.MnemonicCode(phrase, Locale.ENGLISH.toString()).toSeed());
    }


}
