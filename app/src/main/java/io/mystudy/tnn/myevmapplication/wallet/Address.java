package io.mystudy.tnn.myevmapplication.wallet;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.security.SecureRandom;
import java.util.Arrays;

import io.mystudy.tnn.myevmapplication.Application.Dlog;

public class Address {
    private byte[] address;
    private byte[] privateKey;

    public static int ADDRESS_BYTES = 20;
    public static int ADDRESS_KEY_BYTES = 32;

    Address(){
        SecureRandom random = new SecureRandom();
        create( random.generateSeed(ADDRESS_KEY_BYTES) );
    }

    Address(byte[] privateKey){
        create( privateKey );
    }

    private void create( byte[] privateKey ){
        // 1. Private key
        if ( privateKey.length != ADDRESS_KEY_BYTES)
            this.privateKey = Hash.sha3(privateKey);
        else
            this.privateKey = privateKey;

        // 2. Public key
        ECKeyPair keyPair = ECKeyPair.create( this.privateKey );
        byte[] publicKey = keyPair.getPublicKey().toByteArray();
        byte[] pubKeyHash = Hash.sha3( publicKey );

        // 3. Last 20 bytes
        this.address = Arrays.copyOfRange(
                pubKeyHash , pubKeyHash.length - ADDRESS_BYTES, pubKeyHash.length);

        Dlog.e("Address created");
        Dlog.e(getStringAddress());
        Dlog.e(getStringPrivateKey());
    }

    public byte[] getAddress() {
        return address;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    String getStringAddress(){
        return Numeric.toHexString( address );
    }

    String getStringPrivateKey() {
        return Numeric.toHexString( privateKey );
    }
}
