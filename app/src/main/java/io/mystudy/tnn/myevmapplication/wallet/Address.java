package io.mystudy.tnn.myevmapplication.wallet;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.math.ec.ECPoint;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

import io.mystudy.tnn.myevmapplication.Application.Dlog;

class Address{
    private byte[] address;
    private byte[] privateKey;

    private static final int ADDRESS_BYTES = 20;
    private static final int ADDRESS_KEY_BYTES = 32;

    Address( String address ){
        if ( AddressUtils.isValidAddress( address ) )
            this.address = new BigInteger(address, 16).toByteArray();
        else
            throw new IllegalArgumentException("Not Ethereum Address");
    }

    Address( byte[] privateKey ){
        // 1. Start from private key
        if ( privateKey.length != ADDRESS_KEY_BYTES )
            throw new IllegalArgumentException("Wrong size of PRIVATE KEY! : "+privateKey.length);
        else
            this.privateKey = privateKey;

        // 2. Public key from private key
        ECNamedCurveParameterSpec CURVE_PARAMS = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPoint publicPoint = CURVE_PARAMS.getG().multiply( new BigInteger(1, privateKey));
        byte[] publicKey = publicPoint.getEncoded(false);

        // 3. Remove prefix
        publicKey = Arrays.copyOfRange(publicKey, 1, publicKey.length);

        // 4. Hash public key
        byte[] pubKeyHash = Hash.KECCAK256( publicKey );

        // 5. Take last 20 bytes
        this.address = Arrays.copyOfRange(
                pubKeyHash , pubKeyHash.length - ADDRESS_BYTES, pubKeyHash.length);

        Dlog.i("Address created : "+getStringAddress());
        Dlog.i(privateKey.length+" - "+ Numeric.toHexString( this.privateKey ));
    }

    public byte[] getAddress() {
        return address;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    String getStringAddress()  {
        return AddressUtils.toChecksumAddress( Numeric.toHexString( address ) );
    }

}
