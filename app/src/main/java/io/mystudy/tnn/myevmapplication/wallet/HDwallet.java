package io.mystudy.tnn.myevmapplication.wallet;

import com.google.android.gms.common.util.ArrayUtils;

import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.ec.CustomNamedCurves;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.mystudy.tnn.myevmapplication.Application.Dlog;

public class HDwallet {

    private static String HASH_ALGORITHM = "HmacSHA512";
    private static final byte[] SEED_KEY = "Bitcoin seed".getBytes();

    private static byte[] MASTER_SEED;

    private static byte[] MASTER_NODE;  // m
    private static int PURPOSE   = 0x8000002C;   // 44' for BIP-44
    private static int COINTYPE  = 0x8000003C;   // 60' for ethereum
    private static int ACCOUNT   = 0x00000000;
    final private static int CHANGE    = 0x00000000;   // always 0 on Ethereum.
    private static int ADDRESS_INDEX = 0x00000000;

    private Mac HASH;

    HDwallet(byte[] seed){
        MASTER_SEED = seed;

        try {
            HASH = Mac.getInstance( HASH_ALGORITHM );
            SecretKeySpec keySpec = new SecretKeySpec(SEED_KEY, HASH_ALGORITHM);
            HASH.init(keySpec);

            MASTER_NODE = HASH.doFinal( MASTER_SEED );

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public byte[] getRootAddress(){
        byte[] level_1 = CKDpriv(MASTER_NODE, PURPOSE);
        byte[] level_2 = CKDpriv(level_1, COINTYPE);
        byte[] level_3 = CKDpriv(level_2, ACCOUNT);
        byte[] level_4 = CKDpriv(level_3, CHANGE);
        byte[] level_5 = CKDpriv(level_4, ADDRESS_INDEX);

        byte[] privateKeyForAddress = splitKeyCode(level_5);
        Dlog.e("private key for address - "+ Numeric.toHexString(privateKeyForAddress));

        return createAddress(privateKeyForAddress);
    }

    public byte[] createAddress(byte[] privateKey){
        byte[] publicKey;
        byte[] hash = Hash.sha3( privateKey );
        return Arrays.copyOfRange( hash, hash.length - 20 , hash.length);
    }

    // Child Key Drivation
    byte[] CKDpriv(byte[] extendedKey, long index){ return CKDpriv(extendedKey, new BigInteger( Long.toString( index))); }
    byte[] CKDpriv(byte[] extendedKey, BigInteger index){ return CKDpriv(extendedKey, index, false); }
    byte[] CKDpriv(byte[] extendedKey, BigInteger index, boolean harden) {
        byte[] keyCode  = splitKeyCode(extendedKey);
        byte[] chainCode = splitChainCode(extendedKey);
        byte[] hashData = null;

        SecretKeySpec hashKey = new SecretKeySpec( chainCode, HASH_ALGORITHM);
        try {
            HASH.init( hashKey );
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        if ( index.compareTo(new BigInteger(Long.toString(2 ^ 32))) > 0 || harden ){
            Dlog.e("Hardened child key");
//            hashData = 0x00 || ser256( keyCode ) || ser32( index );
            hashData = concat( concat( ByteBuffer.allocate(1).putInt( 0x00 ).array(), keyCode ), ser32( index ) );
        } else {    // Normal child
            Dlog.e("Normal child key");
//            hashData = serP( point( keyCode )) || ser32( index );
            hashData = concat(serP( point( keyCode )), ser32( index ) );
        }

        byte[] childKey = HASH.doFinal(hashData);
        byte[] childLeft = splitKeyCode(childKey);
        byte[] childRight = splitChainCode(childKey);
        BigInteger left = new BigInteger( childLeft );

        Dlog.e("length of childLeft  - "+ childLeft.length);
        Dlog.e("length of childRight - "+ childRight.length);


        X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
        byte[] childPrivateKey = left
                .add( new BigInteger(keyCode).mod( CURVE_PARAMS.getN() ) )
                .toByteArray();
        Dlog.e("length of childPK - "+ childPrivateKey.length);
        Dlog.e("childPK - "+ Numeric.toHexString( childPrivateKey));


        return concat(childPrivateKey, childRight);
    }

    /**
     * Entropy를 개인 키와 체인 코드로 확장한다.
     * @param hashedData
     * @return Left 256bit of hashedData
     */
    byte[] splitKeyCode(byte[] hashedData){
        if ( hashedData.length != 64 )
            throw new IllegalArgumentException("Wrong length of hashed data: "+hashedData.length);

        // 512bit 중 왼쪽 256bit
        int from = 0;
        int to = (hashedData.length / 2) ;
        return Arrays.copyOfRange(hashedData, from, to);
    }

    byte[] splitChainCode(byte[] hashedData){
        if ( hashedData.length != 64 )
            throw new IllegalArgumentException("Wrong length of hashed data: "+hashedData.length);

        // 512bit 중 오른쪽 256bit
        int from = ( hashedData.length / 2 ) ;
        int to = hashedData.length; // 64
        return Arrays.copyOfRange(hashedData, from, to);
    }

    /**
     * point(p): returns the coordinate pair resulting from EC point multiplication (repeated application of the EC group operation) of the secp256k1 base point with the integer p.
     * ser32(i): serialize a 32-bit unsigned integer i as a 4-byte sequence, most significant byte first.
     * ser256(p): serializes the integer p as a 32-byte sequence, most significant byte first.
     * serP(P): serializes the coordinate pair P = (x,y) as a byte sequence using SEC1's compressed form: (0x02 or 0x03) || ser256(x), where the header byte depends on the parity of the omitted y coordinate.
     * parse256(p): interprets a 32-byte sequence as a 256-bit number, most significant byte first.
     */
    ECKeyPair point(byte[] p){
        return ECKeyPair.create(p);
    }
    byte[] serP(ECKeyPair ecKeyPair){
        return Keys.serialize( ecKeyPair );
    }
    byte[] ser32(BigInteger i){
        return i.toByteArray();   // BigInteger
//        return ByteBuffer.allocate(4).putInt( i ).array();    // int
    }
    byte[] concat(byte[] a, byte[] b){
        return ArrayUtils.concatByteArrays(a, b);
    }

}
