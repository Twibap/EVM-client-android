package io.mystudy.tnn.myevmapplication.wallet;

import com.google.android.gms.common.util.ArrayUtils;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.math.ec.ECPoint;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import io.mystudy.tnn.myevmapplication.Application.Dlog;

public class HDwallet {

    private static final byte[] SEED_KEY = "Bitcoin seed".getBytes();
    private static ECNamedCurveParameterSpec CURVE_PARAMS = ECNamedCurveTable.getParameterSpec("secp256k1");

    private byte[] MASTER_SEED;

    private ExtendedKey MASTER_KEY;  // m
    private int PURPOSE   = 0x8000002C;   // 44' for BIP-44
    private int COINTYPE  = 0x8000003C;   // 60' for ethereum
    private int ACCOUNT   = 0x80000000;
    final private int CHANGE    = 0x00000000;   // 항상 0 on Ethereum

    private ExtendedKey ACCOUNT_KEY;
    private int ADDRESS_INDEX = 0x00000000;

    public HDwallet(byte[] master_seed){
        MASTER_SEED = master_seed;
        byte[] seedHash = Hash.HMAC_SHA512(SEED_KEY, MASTER_SEED);
        MASTER_KEY = new ExtendedKey( seedHash );
        MASTER_KEY.debug();

        ExtendedKey level_1 = CKDpriv(MASTER_KEY, PURPOSE);      // m / 44'
        ExtendedKey level_2 = CKDpriv(level_1, COINTYPE);       // 60'
        ExtendedKey level_3 = CKDpriv(level_2, ACCOUNT);        // 0'
        ACCOUNT_KEY = CKDpriv(level_3, CHANGE);         // 0
    }

    public byte[] getAddressKey(int address_index){
        ExtendedKey level_5 = CKDpriv(ACCOUNT_KEY, address_index);  // 0

        return level_5.getKey();
    }

    /**
     * Child Key Derivation
     *
     * Check whether i ≥ 231 (whether the child is a hardened key).
     * If so (hardened child):
     *      let I = HMAC-SHA512(Key = cpar, Data = 0x00 || ser256(kpar) || ser32(i)).
     *      (Note: The 0x00 pads the private key to make it 33 bytes long.)
     * If not (normal child):
     *      let I = HMAC-SHA512(Key = cpar, Data = serP(point(kpar)) || ser32(i)).
     * Split I into two 32-byte sequences, IL and IR.
     * The returned child key ki is parse256(IL) + kpar (mod n).
     * The returned chain code ci is IR.
     * In case parse256(IL) ≥ n or ki = 0,
     * the resulting key is invalid, and one should proceed with the next value for i.
     * (Note: this has probability lower than 1 in 2127.)
     * @param extendedKey
     * @param input
     * @return
     */
    private ExtendedKey CKDpriv(ExtendedKey extendedKey, int input) {
        byte[] keyCode  = extendedKey.getKey();             // 32 bits
        byte[] chainCode = extendedKey.getChain_code();     // 32 bits
        byte[] data;                                        // 37 bits

        Dlog.e("     Key code : "+keyCode.length +" - "+ Numeric.toHexString( keyCode ));
        Dlog.e("   Chain code : "+chainCode.length +" - "+ Numeric.toHexString( chainCode));

        // integer to unsigned integer
        long unsignedInput= (long) input;    // 0xffffffff8000002c
        unsignedInput = unsignedInput << 8*4;   // 0x8000002c
        unsignedInput = unsignedInput >>> 8*4;  // 0x000000008000002c
        long target = 0x0000000080000000;   // 0xffffffff80000000 ?!?!
        target = target << 8*4;             // 0x80000000
        target = target >>> 8*4;            // 0x0000000080000000

        // whether the child is a hardened key
        // Check whether input ≥ 2^31 (whether the child is a hardened key).
        // If so (hardened child):
        //  let I = HMAC-SHA512(Key = cpar, Data = 0x00 || ser256(kpar) || ser32(i)).
        //          (Note: The 0x00 pads the private key to make it 33 bytes long.)
        // If not (normal child):
        //  let I = HMAC-SHA512(Key = cpar, Data = serP(point(kpar)) || ser32(i)).
        if ( unsignedInput >= target ){     // Hardened
            // Data = 0x00 || ser256(kpar) || ser32(i)
            data = concat( new byte[]{0x00} , keyCode, ser32( input ));
        }   // 37 bytes = prefix 1 byte + serialized private key 32 bytes + 4 bytes for input
        else {
            // Data = serP( point(kpar) ) || ser32(i)
            // ************* 중요사항 *************
            // keyCode의 양/음수 차이에 의해 point 결과가 달라진다.
            ECPoint pubKey = point( concat( new byte[]{0x00}, keyCode ));
            data = concat( serP( pubKey ), ser32( input ) );
        }   // 37 bytes = compressed public key 33 bytes + 4 bytes for input

        Dlog.e("         Data : "+data.length+" - "+ Numeric.toHexString( data ));
        byte[] result = Hash.HMAC_SHA512(chainCode, data);

        // Split result into two 32-byte sequences, result_L and result_R.
        // The returned childKey is parse256( result_L ) + kpar (mod n).
        // The returned chain code ci is IR.
        byte[] result_L = getLeft( result );    // child key
        byte[] result_R = getRight( result );   // child chain code

        BigInteger parent_key = parse256( keyCode );
        byte[] childKey = parse256( result_L ).add( parent_key ).mod( CURVE_PARAMS.getN() ).toByteArray();

        Dlog.e("    Child key : "+ childKey.length+" - "+Numeric.toHexString(childKey));
        Dlog.e("  Child chain : "+ result_R.length+" - "+Numeric.toHexString(result_R));

        // Add 연산으로 인해 32 bytes Overflow 가 일어날 수 있다.
        if ( childKey.length > 32 && childKey[0] == 0x00)
            childKey = Arrays.copyOfRange(childKey, 1, childKey.length);

        ExtendedKey childExtendedKey;
        // In case parse256(result_L) ≥ n or childPrivateKey = 0,
        // the resulting key is invalid,
        // and one should proceed with the next value for index.
        // (Note: this has probability lower than 1 in 2^127.)
        if ( parse256( result_L ).compareTo( CURVE_PARAMS.getN() ) >= 0 ||
                parse256( childKey ).compareTo( BigInteger.ZERO ) == 0){
            Dlog.w("ERROR!!! Child key is larger then Prime number N");
            childExtendedKey = CKDpriv(extendedKey, ++input);
        } else {
            childExtendedKey = new ExtendedKey(
                    ExtendedKey.MAIN_PRIVATE,
                    (byte) (extendedKey.getDepth()+1),
                    extendedKey.getFingerprint(),
                    input,
                    result_R,
                    childKey
            );
            childExtendedKey.debug();
        }
        return childExtendedKey;
    }

    /**
     * As standard conversion functions, we assume:
     * point(p): returns the coordinate pair resulting from EC point multiplication
     *          (repeated application of the EC group operation) of the secp256k1 base point with the integer p.
     * ser32(i): serialize a 32-bit unsigned integer i as a 4-byte sequence, most significant byte first.
     * ser256(p): serializes the integer p as a 32-byte sequence, most significant byte first.
     * serP(P): serializes the coordinate pair P = (x,y) as a byte sequence using SEC1's compressed form:
     *          (0x02 or 0x03) || ser256(x), where the header byte depends on the parity of the omitted y coordinate.
     * parse256(p): interprets a 32-byte sequence as a 256-bit number, most significant byte first.
     */
    public ECPoint point(byte[] p){
        BigInteger privateKey = new BigInteger(p);
        return CURVE_PARAMS.getG().multiply( privateKey ).normalize();
    }
    byte[] serP(ECPoint publicKeyPoint ){
        return publicKeyPoint.getEncoded(true);
    }
    byte[] ser32(int i){
        return ByteBuffer.allocate(4).putInt( i ).array();    // int
    }
    byte[] concat(byte[]... bytes){
        return ArrayUtils.concatByteArrays(bytes);
    }
    BigInteger parse256(byte[] bytes){
        if ( bytes.length > 32 )
            throw new IllegalArgumentException("Wrong size of Argument");

        return new BigInteger( 1, bytes);
    }

    byte[] getLeft(byte[] input){
        if (input.length % 2 != 0)
            throw new IllegalArgumentException("Length of input is not EVEN!");

        int from = 0;
        int to = input.length / 2 ;
        return Arrays.copyOfRange( input, from, to);
    }

    byte[] getRight(byte[] input){
        if (input.length % 2 != 0)
            throw new IllegalArgumentException("Length of input is not EVEN!");
        int from = input.length / 2 ;
        int to = input.length;
        return Arrays.copyOfRange( input, from, to);
    }

}
