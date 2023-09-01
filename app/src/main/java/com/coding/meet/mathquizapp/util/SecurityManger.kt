package com.coding.meet.mathquizapp.util

import android.content.Context
import android.util.Base64
import android.util.Log
import com.cossacklabs.themis.SecureCell
import com.cossacklabs.themis.SymmetricKey
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SecurityManger(private val context: Context) {

//    companion object{
//        private const val KEYPASSWORD = ""
//        private const val IVPASSWORD = ""
//        private const val ALGORITHM = ""
//        private const val TRANSFORMATION = ""
//    }

    object Keys {

        init {
            System.loadLibrary("native-lib")
        }

        external fun Secretkey(): String
        external fun ALGORITHM() :String
        external fun TRANSFORMATION() :String
        external fun KEYPASSWORD() :String
        external fun IVPASSWORD () :String
    }


    private val secretKeySpec = generateKey()

    private fun generateKey() : SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        // Replace with actual encrypted Key
        val externalKey = Keys.Secretkey().toByteArray(
            StandardCharsets.UTF_8)
        Log.d("externalKey",String(externalKey))

        // Decrypt the External Encrypted string using the external key
        val KEYPASSWORD = decryptStringWithKey(externalKey, Keys.KEYPASSWORD())
        val bytes = KEYPASSWORD.toByteArray()
        digest.update(bytes,0,bytes.size)


        val ALGORITHM = decryptStringWithKey(externalKey, Keys.ALGORITHM())
        return SecretKeySpec(digest.digest(),ALGORITHM)
    }

    // Decrypts a encrypted string using provided key
    private fun decryptStringWithKey(key: ByteArray,encryptedString:String) : String{

        val symmetricKey = SymmetricKey(key)
        val secureCell =  SecureCell.SealWithKey(symmetricKey)

        val decodedData = Base64.decode(encryptedString, Base64.DEFAULT)
        val decryptedString = String(secureCell.decrypt(decodedData))

        Log.d("decryptedString",decryptedString)

        return  decryptedString
    }

    fun decryptFile(encryptFileName : String) :String{

        // Load Encrypt File From Asset
        val fis = context.assets.open(encryptFileName)


//        val encryptedFile = File(context.filesDir,encryptFileName)
//        val fis = FileInputStream(encryptedFile)


        // Replace with actual external key
        val externalKey = Keys.Secretkey().toByteArray(
            StandardCharsets.UTF_8)
        Log.d("externalKey",String(externalKey))


        // Decrypt the External Encrypted string using the external key
        val TRANSFORMATION = decryptStringWithKey(externalKey,Keys.TRANSFORMATION())
        val IVPASSWORD = decryptStringWithKey(externalKey,Keys.IVPASSWORD())

        val decryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE,secretKeySpec,IvParameterSpec(IVPASSWORD.toByteArray()))
        }

        val inputStream = CipherInputStream(fis,decryptCipher)

        val byteArrayOutputStream = ByteArrayOutputStream()
        var nextBytes = inputStream.read()
        while (nextBytes != -1){
            byteArrayOutputStream.write(nextBytes)
            nextBytes = inputStream.read()
        }

        Log.d("status","Decryption File Successfully")

        return byteArrayOutputStream.toByteArray().decodeToString().trim()
    }


}