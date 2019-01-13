package io.mystudy.tnn.myevmapplication.wallet;

import com.google.android.gms.common.util.ArrayUtils;

import org.web3j.crypto.Base58;
import org.web3j.crypto.ECKeyPair;

import java.nio.ByteBuffer;
import java.util.Arrays;

import io.mystudy.tnn.myevmapplication.Application.Dlog;

/**
 * 4 byte: version bytes (mainnet: 0x0488B21E public, 0x0488ADE4 private; testnet: 0x043587CF public, 0x04358394 private)
 * 1 byte: depth: 0x00 for master nodes, 0x01 for level-1 derived keys, ....
 * 4 bytes: the fingerprint of the parent's key (0x00000000 if master key)
 * 4 bytes: child number. This is ser32(i) for i in xi = xpar/i, with xi the key being serialized. (0x00000000 if master key)
 * 32 bytes: the chain code
 * 33 bytes: the public key or private key data (serP(K) for public keys, 0x00 || ser256(k) for private keys)
 *
 * TODO Master key 는 Debug 출력이 일치하지만 그 외의 Key는 일치하지 않는다.
 */
class ExtendedKey {
    static final int MAIN_PRIVATE = 0x0488ADE4;
    static final int MAIN_PUBLIC = 0x0488B21E;
    static final int TEST_PRIVATE = 0x04358394;
    static final int TEST_PUBLIC = 0x043587CF;

    private int version;  // = new byte[4];
    private byte depth = 0x00;       // = new byte[1];
    private int fingerprint = 0x00000000;   // 4 bytes
    private int child_number = 0x00000000;  // 4 bytes
    private byte[] chain_code;  // = new byte[32];
    private byte[] key;         // = new byte[33];

    ExtendedKey(byte[] master_node){
        if ( master_node.length != 64 )
            throw new IllegalArgumentException("ERROR!!! : Wrong size of master node - "+master_node.length);

        this.version = MAIN_PRIVATE;

        byte[] key = Arrays.copyOfRange(master_node, 0, master_node.length / 2);
        byte[] code = Arrays.copyOfRange(master_node, master_node.length / 2, master_node.length);
        this.setKey(key);
        this.setChain_code(code);
    }

    ExtendedKey(int version, byte depth, int fingerprint, int index, byte[] chain_code, byte[] key){
        this.version = version;
        this.depth = depth;
        this.fingerprint = fingerprint;
        this.child_number = index;
        this.setChain_code(chain_code);
        this.setKey(key);
    }

    byte[] serialize(){

        byte[] version_bytes = ByteBuffer.allocate(4).putInt(this.version).array();
        byte[] depth = ByteBuffer.allocate(1).put(this.depth).array();
        byte[] fingerprint = ByteBuffer.allocate(4).putInt(this.fingerprint).array();
        byte[] child_number = ByteBuffer.allocate(4).putInt(this.child_number).array();

        // key field 는 33 bytes, 1 byte for prefix + 32 bytes for key data
        byte[] key = this.key;
        while ( key.length < 33 )
            key = ArrayUtils.concatByteArrays( new byte[]{0x00}, key);

        return ArrayUtils.concatByteArrays(
                version_bytes, depth, fingerprint, child_number, chain_code, key );
    }

    void debug(){
        byte[] checksumed = ArrayUtils.concatByteArrays( this.serialize(), this.getChecksum() );
        switch (this.depth){
            case 0: // m
                Dlog.d("  Master key : "+ Base58.encode( checksumed) );
                break;
            case 1: // 44'
                Dlog.i(" Purpose key : "+ Base58.encode( checksumed) );
                break;
            case 2: // 60'
                Dlog.i("    Type key : "+ Base58.encode( checksumed) );
                break;
            case 3: // 0'
                Dlog.d(" Account key : "+ Base58.encode( checksumed) );
                break;
            case 4: // 0
                Dlog.i("  Change key : "+ Base58.encode( checksumed) );
                break;
            case 5: // 0
                Dlog.d("   Index key : "+ Base58.encode( checksumed) );
                break;
        }
        Dlog.i("=============================================================================================");
    }

    void setKey(byte[] key){
        if ( key.length > 33)
            throw new IllegalArgumentException("Wrong size of key - "+key.length);


        this.key = key;
    }

    void setChain_code(byte[] chain_code){
        if (chain_code.length != 32)
            throw new IllegalArgumentException("Wrong size of chain code - "+chain_code.length);

        this.chain_code = chain_code;
    }

    byte[] getChain_code() {
        return chain_code;
    }

    /**
     * Double SHA256
     * @return first 4 bytes
     */
    private byte[] getChecksum(){
        return Arrays.copyOfRange( Hash.SHA256( Hash.SHA256( this.serialize() )), 0, 4);
    }

    byte getDepth() {
        return depth;
    }

    int getFingerprint() {
        byte[] hashed_key;

        if ( this.key[0] == 0x00){
            // Public key를 생성한다.
            ECKeyPair keyPair = ECKeyPair.create( this.key );
            byte[] pubKey = keyPair.getPublicKey().toByteArray();
            hashed_key = Hash.HASH160( pubKey );
        } else {
            hashed_key = Hash.HASH160( this.key );
        }

        // first 32 bits
        byte[] fingerprint = Arrays.copyOfRange(hashed_key, 0, 4);

        return ByteBuffer.wrap(fingerprint).getInt();
    }

    // Private key 인 경우 32 bytes 보다 작을 수 있다.
    public byte[] getKey() {
        byte[] key = this.key;
        while ( key.length < 32)
            key = ArrayUtils.concatByteArrays( new byte[]{0x00}, key);

        return key;
    }

    public boolean isPrivateKey(){
        return key.length < 33;
    }
}
