package com.solana

import android.util.Log
import com.google.gson.Gson
import com.solana.actions.*
import com.solana.api.getAccountInfo
import com.solana.api.getBalance
import com.solana.api.getSignaturesForAddress
import com.solana.api.getTransactions
import com.solana.api.requestAirdrop
import com.solana.core.Account
import com.solana.core.PublicKey
import com.solana.customConfig.CustomProgramId
import com.solana.models.buffer.*
import com.solana.networking.OkHttpNetworkingRouter
import com.solana.networking.RPCEndpoint
import com.solana.vendor.TweetNaclFast
import com.syntifi.near.borshj.Borsh
import okhttp3.OkHttpClient
import org.bitcoinj.core.Base58
import org.bouncycastle.util.encoders.Base64
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Config{
    companion object {
       lateinit var network : String
    }
}

object SolanaHelper {
    private var solana: Solana? = null
    private var solana_dev: Solana? = null
    private var gson: Gson? = null
    private var gson_dev: Gson? = null

    private fun getSolana(): Solana {
        if (solana == null) {

            val httpClient = OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build()
            val network = OkHttpNetworkingRouter(RPCEndpoint.mainnetBetaSolana, httpClient)
            solana = Solana(network)
            gson = Gson();
        }
        return solana as Solana
    }

    private fun getSolana_dev(): Solana {
        if (solana_dev == null) {

            val httpClient = OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build()
            val network = OkHttpNetworkingRouter(RPCEndpoint.devnetSolana, httpClient)
            solana_dev = Solana(network)
            gson_dev = Gson();
        }
        return solana_dev as Solana
    }

    fun getAccount(
        str: PublicKey,
        onComplete: OnResponse,
    ) {
        if (Config.network == "Main"){
            getSolana().api.getAccountInfo(
                str,
                AccountInfo::class.java
            ) { result ->
                result.onSuccess { account ->
                    if (account.data == null || account.data?.value == null) {
                        onComplete.onFailure(RuntimeException("Invalid data"))
                        return@onSuccess
                    }
                    account.data?.value?.let { account ->
                        onComplete.onSuccess(account)
                    }

                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        } else {
            getSolana_dev().api.getAccountInfo(
                str,
                AccountInfo::class.java
            ) { result ->
                result.onSuccess { account ->
                    if (account.data == null || account.data?.value == null) {
                        onComplete.onFailure(RuntimeException("Invalid data"))
                        return@onSuccess
                    }
                    account.data?.value?.let { account ->
                        onComplete.onSuccess(account)
                    }

                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        }
    }

    fun getSignaturesForAddress(
        customPublickey: PublicKey,
        onComplete: OnResponseData<GetSignaturesForAddressModel>,
    ) {
        if (Config.network == "Main"){
            getSolana().api.getSignaturesForAddress(customPublickey) { result ->
                if (result.equals("error")) {
                    onComplete.onFailure(RuntimeException("Invalid data"))
                } else {
                    try {
                        val getInfoAccountModel: GetSignaturesForAddressModel =
                            gson!!.fromJson(
                                result,
                                GetSignaturesForAddressModel::class.java
                            )
                        onComplete.onSuccess(getInfoAccountModel)
                    } catch (e: Exception) {
                        Log.e("errorGetContact", e.toString())
                    }
                }
            }
        } else {
            getSolana_dev().api.getSignaturesForAddress(customPublickey) { result ->
                if (result.equals("error")) {
                    onComplete.onFailure(RuntimeException("Invalid data"))
                } else {
                    try {
                        val getInfoAccountModel: GetSignaturesForAddressModel =
                            gson_dev!!.fromJson(
                                result,
                                GetSignaturesForAddressModel::class.java
                            )
                        onComplete.onSuccess(getInfoAccountModel)
                    } catch (e: Exception) {
                        Log.e("errorGetContact", e.toString())
                    }
                }
            }
        }
    }

    fun getTransaction(
        transaction: String,
        onComplete: OnResponseData<GetTransactionModel>,
    ) {
        if (Config.network == "Main"){
            getSolana().api.getTransactions(transaction) { result ->
                if (result.equals("error")) {
                    onComplete.onFailure(RuntimeException("Invalid data"))
                } else {
                    try {
                        val getInfoAccountModel: GetTransactionModel =
                            gson!!.fromJson(
                                result,
                                GetTransactionModel::class.java
                            )
                        onComplete.onSuccess(getInfoAccountModel)
                    } catch (e: Exception) {
                        Log.e("errorGetContact", e.toString())
                    }
                }
            }
        } else {
            getSolana_dev().api.getTransactions(transaction) { result ->
                if (result.equals("error")) {
                    onComplete.onFailure(RuntimeException("Invalid data"))
                } else {
                    try {
                        val getInfoAccountModel: GetTransactionModel =
                            gson_dev!!.fromJson(
                                result,
                                GetTransactionModel::class.java
                            )
                        onComplete.onSuccess(getInfoAccountModel)
                    } catch (e: Exception) {
                        Log.e("errorGetContact", e.toString())
                    }
                }
            }
        }
    }

    fun getContacts(
        str: PublicKey,
        onComplete: OnResponseData<ContactListModel>,
    ) {
        if (Config.network == "Main"){
            getSolana().api.getAccountInfo(
                str,
            ) { result ->
                if (result.equals("error")) {
                    onComplete.onFailure(RuntimeException("Invalid data"))
                } else {
                    try {
                        val getInfoAccountModel: GetInfoAccountModel =
                            gson!!.fromJson(
                                result,
                                GetInfoAccountModel::class.java
                            )
                        if (getInfoAccountModel.getResult().getValue().getData() == null) {
                            onComplete.onSuccess(null)
                            return@getAccountInfo
                        }
                        val contactListModel =
                            decrypt(
                                getInfoAccountModel.getResult().getValue().getData().get(0),
                                ContactListModel::class.java
                            )
                        onComplete.onSuccess(contactListModel)
                    } catch (e: Exception) {
                        Log.e("errorGetContact", e.toString())
                    }
                }
            }
        } else {
            getSolana_dev().api.getAccountInfo(
                str,
            ) { result ->
                if (result.equals("error")) {
                    onComplete.onFailure(RuntimeException("Invalid data"))
                } else {
                    try {
                        val getInfoAccountModel: GetInfoAccountModel =
                            gson_dev!!.fromJson(
                                result,
                                GetInfoAccountModel::class.java
                            )
                        if (getInfoAccountModel.getResult().getValue().getData() == null) {
                            onComplete.onSuccess(null)
                            return@getAccountInfo
                        }
                        val contactListModel =
                            decrypt(
                                getInfoAccountModel.getResult().getValue().getData().get(0),
                                ContactListModel::class.java
                            )
                        onComplete.onSuccess(contactListModel)
                    } catch (e: Exception) {
                        Log.e("errorGetContact", e.toString())
                    }
                }
            }
        }
    }

    fun getConversation(
        str: PublicKey,
        onComplete: OnResponseData<ConversationModel>,
    ) {
        if (Config.network == "Main"){
            getSolana().api.getAccountInfo(
                str,
            ) { result ->
                if (result.equals("error")) {
                    onComplete.onFailure(RuntimeException("Invalid data"))
                } else {
                    val getInfoAccountModel: GetInfoAccountModel =
                        gson!!.fromJson(
                            result,
                            GetInfoAccountModel::class.java
                        )
                    if (getInfoAccountModel.getResult().getValue() == null) {
                        onComplete.onSuccess(null)
                        return@getAccountInfo
                    }
                    val contactListModel =
                        decrypt(
                            getInfoAccountModel.getResult().getValue().getData().get(0),
                            ConversationModel::class.java
                        )
                    onComplete.onSuccess(contactListModel)
                }
            }
        } else {
            getSolana_dev().api.getAccountInfo(
                str,
            ) { result ->
                if (result.equals("error")) {
                    onComplete.onFailure(RuntimeException("Invalid data"))
                } else {
                    val getInfoAccountModel: GetInfoAccountModel =
                        gson_dev!!.fromJson(
                            result,
                            GetInfoAccountModel::class.java
                        )
                    if (getInfoAccountModel.getResult().getValue() == null) {
                        onComplete.onSuccess(null)
                        return@getAccountInfo
                    }
                    val contactListModel =
                        decrypt(
                            getInfoAccountModel.getResult().getValue().getData().get(0),
                            ConversationModel::class.java
                        )
                    onComplete.onSuccess(contactListModel)
                }
            }
        }
    }

    fun getConversations(
        str: PublicKey,
        onComplete: OnResponseData<ProfileModel>,
    ) {
        if (Config.network == "Main"){
            getSolana().api.getAccountInfo(
                str,
            ) { result ->
                if (result.equals("error")) {
                    onComplete.onFailure(RuntimeException("Invalid data"))
                } else {
                    try {
                        val getInfoAccountModel: GetInfoAccountModel =
                            gson!!.fromJson(
                                result,
                                GetInfoAccountModel::class.java
                            )
                        if (getInfoAccountModel.getResult().getValue().getData() == null) {
                            onComplete.onSuccess(null)
                            return@getAccountInfo
                        }
                        val contactListModel =
                            decrypt(
                                getInfoAccountModel.getResult().getValue().getData().get(0),
                                ProfileModel::class.java
                            )
                        onComplete.onSuccess(contactListModel)
                    } catch (error: Exception) {
                        onComplete.onSuccess(null)
                        return@getAccountInfo
                    }

                }
            }
        } else {
            getSolana_dev().api.getAccountInfo(
                str,
            ) { result ->
                if (result.equals("error")) {
                    onComplete.onFailure(RuntimeException("Invalid data"))
                } else {
                    try {
                        val getInfoAccountModel: GetInfoAccountModel =
                            gson_dev!!.fromJson(
                                result,
                                GetInfoAccountModel::class.java
                            )
                        if (getInfoAccountModel.getResult().getValue().getData() == null) {
                            onComplete.onSuccess(null)
                            return@getAccountInfo
                        }
                        val contactListModel =
                            decrypt(
                                getInfoAccountModel.getResult().getValue().getData().get(0),
                                ProfileModel::class.java
                            )
                        onComplete.onSuccess(contactListModel)
                    } catch (error: Exception) {
                        onComplete.onSuccess(null)
                        return@getAccountInfo
                    }
                }
            }
        }
    }

    fun getOrCreateAccount(
        key1: PublicKey?,
        key: PublicKey?,
        username: String,
        avatar: String,
        indexProfile: String,
        onComplete: OnResponse
    ) {
        if (key != null && key1 != null) {
            createAccount(key1, key, programId, account, username, avatar, indexProfile, onComplete)
        }
    }

    private fun createAccount(
        key1: PublicKey,
        key: PublicKey,
        programId: PublicKey,
        acc: Account,
        username: String,
        avatar: String,
        indexProfile: String,
        onComplete: OnResponse
    ) {
        if (Config.network == "Main"){
            getSolana().action.createAccountWithSeed(
                acc,
                key,
                programId,
                username,
                indexProfile
            ) { result ->
                result.onSuccess { res ->
                    if (Config.network == "Main"){
                        getSolana().action.addContact(acc, key, username, avatar, key1) { result2 ->
                            result2.onSuccess { sss ->
                                getAccountLoop(key, onComplete, 5)
                                Log.e("TAG", "createAccount: " + sss)
                            }.onFailure { eee ->
                                Log.e("erfan", "createAccount: " + eee)
                                onComplete.onFailure(RuntimeException(eee))
                            }
                        }
                    } else {
                        getSolana_dev().action.addContact(acc, key, username, avatar, key1) { result2 ->
                            result2.onSuccess { sss ->
                                getAccountLoop(key, onComplete, 5)
                                Log.e("TAG", "createAccount: " + sss)
                            }.onFailure { eee ->
                                Log.e("erfan", "createAccount: " + eee)
                                onComplete.onFailure(RuntimeException(eee))
                            }
                        }
                    }
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        } else {
            getSolana_dev().action.createAccountWithSeed(
                acc,
                key,
                programId,
                username,
                indexProfile
            ) { result ->
                result.onSuccess { res ->
                    if (Config.network == "Main"){
                        getSolana().action.addContact(acc, key, username, avatar, key1) { result2 ->
                            result2.onSuccess { sss ->
                                getAccountLoop(key, onComplete, 5)
                                Log.e("TAG", "createAccount: " + sss)
                            }.onFailure { eee ->
                                Log.e("erfan", "createAccount: " + eee)
                                onComplete.onFailure(RuntimeException(eee))
                            }

                        }
                    } else {
                        getSolana_dev().action.addContact(acc, key, username, avatar, key1) { result2 ->
                            result2.onSuccess { sss ->
                                getAccountLoop(key, onComplete, 5)
                                Log.e("TAG", "createAccount: " + sss)
                            }.onFailure { eee ->
                                onComplete.onFailure(RuntimeException(eee))
                            }

                        }
                    }
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        }
    }

    fun getAccountLoop(
        str: PublicKey,
        onComplete: OnResponse,
        count: Int,
    ) {
        if (Config.network == "Main"){
            getSolana().api.getAccountInfo(
                str,
                AccountInfo::class.java
            ) { result ->
                result.onSuccess { account ->
                    if (account.data == null || account.data?.value == null) {
                        onComplete.onFailure(RuntimeException("Invalid data"))
                        return@onSuccess;
                    }
                    account.data?.value?.let { account ->
                        onComplete.onSuccess(account)
                    }
                }.onFailure { error ->
                    if (error.message!!.contains("Account return Null")) {
                        if (count <= 0) {
                            onComplete.onFailure(RuntimeException(error))
                        }
                        Thread.sleep(5000)
                        onComplete.onSuccess(null)
                    } else
                        onComplete.onFailure(RuntimeException(error))
                }
            }
        } else {
            getSolana_dev().api.getAccountInfo(
                str,
                AccountInfo::class.java
            ) { result ->
                result.onSuccess { account ->
                    if (account.data == null || account.data?.value == null) {
                        onComplete.onFailure(RuntimeException("Invalid data"))
                        return@onSuccess;
                    }
                    account.data?.value?.let { account ->
                        onComplete.onSuccess(account)
                    }
                }.onFailure { error ->
                    if (error.message!!.contains("Account return Null")) {
                        if (count <= 0) {
                            onComplete.onFailure(RuntimeException(error))
                        }
                        Thread.sleep(5000)
                        onComplete.onSuccess(null)
                    } else
                        onComplete.onFailure(RuntimeException(error))
                }
            }
        }
    }

    fun getBalance(
        customPublickey: PublicKey,
        onComplete: OnResponseStr,
    ) {
        if (Config.network == "Main"){
            getSolana().api.getBalance(customPublickey) { result ->
                result.onSuccess { result1 ->
                    onComplete.onSuccess(result1.toString());
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        } else {
            getSolana_dev().api.getBalance(customPublickey) { result ->
                result.onSuccess { result1 ->
                    onComplete.onSuccess(result1.toString());
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        }
    }


    fun setAirdrop(
        customPublickey: PublicKey,
        onComplete: OnResponseStr,
    ) {
        val LAMPORTS_PER_SOL = 1000000000L
        getSolana_dev().api.requestAirdrop(customPublickey, LAMPORTS_PER_SOL * 4) { result ->
            result.onSuccess { res ->
//                getSolana().api.getConfirmedTransaction()

//                getSolana().api.getConfirmedTransaction(res) { result1 ->
//                    result1.onSuccess { res1 ->
//                        onComplete.onSuccess("success")
//                    }.onFailure { error ->
//                        onComplete.onFailure(RuntimeException(error))
//                    }
//                }
                onComplete.onSuccess("success")
            }.onFailure { error ->
                onComplete.onFailure(RuntimeException(error))
            }
        }
    }

    fun createConversation(
        accountId: PublicKey,
        account: Account,
        conversationName: String,
        contacts: List<UserModel>,
        is_private: Boolean,
        indexProfile: String,
        onComplete: OnResponseStr,
    ) {
        if (Config.network == "Main"){
            getSolana().action.createConversationAction(
                accountId,
                account,
                conversationName,
                contacts,
                is_private,

                ) { result ->
                result.onSuccess { res ->
                    if (Config.network == "Main"){
                        getSolana().action.addConversation(
                            res.second,
                            account,
                            conversationName,
                            contacts,
                            indexProfile,
                        ) { result2 ->
                            result2.onSuccess { account ->
                                onComplete.onSuccess(account.second.toBase58())

                            }.onFailure { error ->
                                onComplete.onFailure(RuntimeException(error))
                            }
                        }
                    } else {
                        getSolana_dev().action.addConversation(
                            res.second,
                            account,
                            conversationName,
                            contacts,
                            indexProfile,
                        ) { result2 ->
                            result2.onSuccess { account ->
                                onComplete.onSuccess(account.second.toBase58())

                            }.onFailure { error ->
                                onComplete.onFailure(RuntimeException(error))
                            }
                        }
                    }
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        } else {
            getSolana_dev().action.createConversationAction(
                accountId,
                account,
                conversationName,
                contacts,
                is_private,

                ) { result ->
                result.onSuccess { res ->
                    if (Config.network == "Main"){
                        getSolana().action.addConversation(
                            res.second,
                            account,
                            conversationName,
                            contacts,
                            indexProfile,
                        ) { result2 ->
                            result2.onSuccess { account ->
                                onComplete.onSuccess(account.second.toBase58())

                            }.onFailure { error ->
                                onComplete.onFailure(RuntimeException(error))
                            }
                        }
                    } else {
                        getSolana_dev().action.addConversation(
                            res.second,
                            account,
                            conversationName,
                            contacts,
                            indexProfile,
                        ) { result2 ->
                            result2.onSuccess { account ->
                                onComplete.onSuccess(account.second.toBase58())

                            }.onFailure { error ->
                                onComplete.onFailure(RuntimeException(error))
                            }
                        }
                    }
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        }
    }

    fun sendWithdraw(
        from: String,
        to: PublicKey,
        amount: Long,
        onComplete: OnResponseE,
    ) {
        val signer = createAccountWithString(from)
        if (Config.network == "Main"){
            getSolana().action.sendSOL(signer, to, amount) { result ->
                result.onSuccess { onComplete.onSuccess() }
                    .onFailure { error -> onComplete.onFailure(RuntimeException(error)) }
            }
        } else {
            getSolana_dev().action.sendSOL(signer, to, amount) { result ->
                result.onSuccess { onComplete.onSuccess() }
                    .onFailure { error -> onComplete.onFailure(RuntimeException(error)) }
            }
        }
    }

    fun sendMessage(
        userPrivateKey: String,
        conversationId: PublicKey,
        message: MessageModel,
        onComplete: OnResponseE,
    ) {
        val accountUser1 = createAccountWithString(userPrivateKey)
        if (Config.network == "Main"){
            getSolana().action.sendMessage(
                conversationId,
                accountUser1,
                message
            ) { result ->
                result.onSuccess { onComplete.onSuccess() }
                    .onFailure { error -> onComplete.onFailure(RuntimeException(error)) }
            }
        } else {
            getSolana_dev().action.sendMessage(
                conversationId,
                accountUser1,
                message
            ) { result ->
                result.onSuccess { onComplete.onSuccess() }
                    .onFailure { error -> onComplete.onFailure(RuntimeException(error)) }
            }
        }
    }

    fun addMember(
        conversationId: PublicKey,
        user: UserModel,
        conversationName: String,
        indexAvatar: String,
        onComplete: OnResponseE,
    ) {
        val members: MutableList<UserModel> = ArrayList()
        members.add(user)
        if (Config.network == "Main"){
            getSolana().action.addMember(
                account,
                conversationId,
                user
            ) { result ->
                result.onSuccess { res ->
                    if (Config.network == "Main"){
                        getSolana().action.addConversation(
                            conversationId,
                            account,
                            conversationName,
                            members,
                            indexAvatar
                        ) { result2 ->
                            result2.onSuccess { account ->
                                onComplete.onSuccess()

                            }.onFailure { error ->
                                onComplete.onFailure(RuntimeException(error))
                            }
                        }
                    } else {
                        getSolana_dev().action.addConversation(
                            conversationId,
                            account,
                            conversationName,
                            members,
                            indexAvatar
                        ) { result2 ->
                            result2.onSuccess { account ->
                                onComplete.onSuccess()

                            }.onFailure { error ->
                                onComplete.onFailure(RuntimeException(error))
                            }
                        }
                    }
                    onComplete.onSuccess()
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        } else {
            getSolana_dev().action.addMember(
                account,
                conversationId,
                user
            ) { result ->
                result.onSuccess { res ->
                    if (Config.network == "Main"){
                        getSolana().action.addConversation(
                            conversationId,
                            account,
                            conversationName,
                            members,
                            indexAvatar
                        ) { result2 ->
                            result2.onSuccess { account ->
                                onComplete.onSuccess()

                            }.onFailure { error ->
                                onComplete.onFailure(RuntimeException(error))
                            }
                        }
                    } else {
                        getSolana_dev().action.addConversation(
                            conversationId,
                            account,
                            conversationName,
                            members,
                            indexAvatar
                        ) { result2 ->
                            result2.onSuccess { account ->
                                onComplete.onSuccess()

                            }.onFailure { error ->
                                onComplete.onFailure(RuntimeException(error))
                            }
                        }
                    }
                    onComplete.onSuccess()
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        }
    }

    fun leftUser(
        conversationId: PublicKey,
        userId: PublicKey,
        onComplete: OnResponseE,
    ) {
        if (Config.network == "Main"){
            getSolana().action.leftUser(
                account,
                conversationId,
                userId
            ) { result ->
                result.onSuccess { res ->
                    onComplete.onSuccess()
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        } else {
            getSolana_dev().action.leftUser(
                account,
                conversationId,
                userId
            ) { result ->
                result.onSuccess { res ->
                    onComplete.onSuccess()
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        }
    }

    fun <T> decrypt(
        res: String,
        clazz: Class<T>,
    ): T {
        try {
            val base64decodedBytes: ByteArray = Base64.decode(res)
            val data = ByteArray(4)
            System.arraycopy(base64decodedBytes, 0, data, 0, 4)
            val dataLengthModel = Borsh.deserialize(
                data,
                DataLengthModel::class.java
            )
            var length = dataLengthModel.length

            if (length == 0) {
                length = 288;
            }
            val accountDataBuffer = ByteArray(length)
            System.arraycopy(base64decodedBytes, 4, accountDataBuffer, 0, length)
            return Borsh.deserialize(accountDataBuffer, clazz)
        } catch (error: Exception) {
            print(error.message);
            return Borsh.deserialize(ByteArray(2), clazz);
        }

    }

    fun createAccount2(
        key: PublicKey,
        payer: PublicKey,
        onComplete: OnResponse,
    ) {
        if (Config.network == "Main"){
            getSolana().action.createTokenAccount(payer, key) { result ->
                result.onSuccess { res ->
                    Log.i("TAG", "createAccount: " + res.first)
                    getAccount(res.second, onComplete)
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        } else {
            getSolana_dev().action.createTokenAccount(payer, key) { result ->
                result.onSuccess { res ->
                    Log.i("TAG", "createAccount: " + res.first)
                    getAccount(res.second, onComplete)
                }.onFailure { error ->
                    onComplete.onFailure(RuntimeException(error))
                }
            }
        }

    }

    private fun createAccountWithString(customSecretKey: String): Account {
        val secByte: ByteArray = Base58.decode(customSecretKey);
        val datass = TweetNaclFast.Signature.keyPair_fromSecretKey(secByte)
        val account = Account(datass)
        return account;
    }

//     A3mnyMyVrNkAfyF8oFii9tmzxkYZ874U3ydm98eHBF8g
    var account = Account(byteArrayOf(
            5,
            35,
            220.toByte(),
            81,
            175.toByte(),
            172.toByte(),
            110,
            30,
            97,
            135.toByte(),
            175.toByte(),
            206.toByte(),
            11,
            111,
            54,
            160.toByte(),
            129.toByte(),
            87,
            96,
            161.toByte(),
            82,
            202.toByte(),
            66,
            107,
            141.toByte(),
            119,
            146.toByte(),
            14,
            206.toByte(),
            173.toByte(),
            199.toByte(),
            32,
            207.toByte(),
            73,
            19,
            129.toByte(),
            244.toByte(),
            227.toByte(),
            204.toByte(),
            241.toByte(),
            137.toByte(),
            46,
            176.toByte(),
            86,
            160.toByte(),
            138.toByte(),
            72,
            93,
            147.toByte(),
            67,
            44,
            217.toByte(),
            192.toByte(),
            79,
            150.toByte(),
            164.toByte(),
            104,
            43,
            188.toByte(),
            42,
            105,
            242.toByte(),
            170.toByte(),
            255.toByte()
        ))

    var programId = PublicKey(CustomProgramId.getProfileProgramId());

    @Throws(IOException::class)
    fun createWithSeed(
        seed: String,
    ): PublicKey {
        return PublicKey.createWithSeed(account.publicKey, seed, programId)
    }

    fun getTimes(): String {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        df.timeZone = TimeZone.getTimeZone("GMT+3:30")
        val nowAsISO: String = df.format(Date())
        return nowAsISO;
    }

    interface OnResponse {
        fun onSuccess(accountInfo: AccountInfo?)
        fun onFailure(e: Exception?)
    }

    interface OnResponseE {
        fun onSuccess()
        fun onFailure(e: Exception?)
    }

    interface OnResponseStr {
        fun onSuccess(accountInfo: String?)
        fun onFailure(e: Exception?)
    }

    interface OnResponseData<T> {
        fun onSuccess(accountInfo: T?)
        fun onFailure(e: Exception?)
    }
}


