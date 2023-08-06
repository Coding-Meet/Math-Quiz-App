package com.coding.meet.mathquizapp.util

import android.content.Context
import android.util.Log
import com.coding.meet.mathquizapp.BuildConfig
import java.io.ByteArrayOutputStream
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


    private val secretKeySpec = generateKey()

    private fun generateKey() : SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = BuildConfig.KEYPASSWORD.toByteArray()
        digest.update(bytes,0,bytes.size)
        return SecretKeySpec(digest.digest(),BuildConfig.ALGORITHM)
    }


    fun decryptFile(encryptFileName : String) :String{

        // Load Encrypt File From Asset
        val fis = context.assets.open(encryptFileName)


//        val encryptedFile = File(context.filesDir,encryptFileName)
//        val fis = FileInputStream(encryptedFile)


        val decryptCipher = Cipher.getInstance(BuildConfig.TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE,secretKeySpec,IvParameterSpec(BuildConfig.IVPASSWORD.toByteArray()))
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