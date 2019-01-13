package io.mystudy.tnn.myevmapplication.wallet;

import org.spongycastle.crypto.digests.KeccakDigest;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class Hash {

    static byte[] KECCAK256(byte[] input){
        KeccakDigest digest = new KeccakDigest(256);
        byte[] result = new byte[ digest.getDigestSize() ];
        digest.update(input, 0, input.length);
        digest.doFinal(result, 0);

        return result;
    }

    static byte[] HMAC_SHA512(byte[] key, byte[] data){
        String HASH_ALGORITHM = "HmacSHA512";
        byte[] result = null;

        try {
            SecretKeySpec keySpec = new SecretKeySpec( key, HASH_ALGORITHM );
            Mac hmacSHA512 = Mac.getInstance( HASH_ALGORITHM );
            hmacSHA512.init( keySpec );

            result = hmacSHA512.doFinal( data );
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return result;
    }

    // PBKDF2withHmacSHA512
    // https://stackoverflow.com/questions/22580853/reliable-implementation-of-pbkdf2-hmac-sha256-for-java
    static byte[] PBKDF2withHmacSHA512(String password, String salt, int iterationCount){
        byte[] result = null;
        try {
            PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());
            gen.init( password.getBytes("UTF-8"), salt.getBytes(), iterationCount);
            result = ((KeyParameter) gen.generateDerivedParameters(512)).getKey();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * RIPEMD160 after SHA256
     * @param publicKey from private key from secp256k1
     * @return 버전 prifix와 함께 Base58 인코딩 하면 Bitcoin Address 이다.
     */
    static byte[] HASH160(byte[] publicKey){
        return RIPEMD160( SHA256( publicKey ));
    }

    static byte[] SHA256(byte[] input){
        SHA256Digest digest = new SHA256Digest();
        byte[] result = new byte[ digest.getDigestSize() ];
        digest.update(input, 0, input.length);
        digest.doFinal(result, 0);

        return result;
    }

    static byte[] RIPEMD160(byte[] input){
        RIPEMD160Digest digest = new RIPEMD160Digest();
        byte[] result = new byte[ digest.getDigestSize() ];
        digest.update( input, 0, input.length);
        digest.doFinal( result, 0);

        return result;
    }
}
